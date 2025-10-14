package com.laohei.bili_tube.features.player.state.media_v2

import android.content.Context
import android.util.Log
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.DefaultAllocator
import com.laohei.bili_tube.core.AudioQualityMap
import com.laohei.bili_tube.core.MOBILE_NET_AUDIO_QUALITY
import com.laohei.bili_tube.core.MOBILE_NET_VIDEO_QUALITY
import com.laohei.bili_tube.core.NormalAudioQuality
import com.laohei.bili_tube.core.VideoQualityMap
import com.laohei.bili_tube.core.WLAN_AUDIO_QUALITY
import com.laohei.bili_tube.core.WLAN_VIDEO_QUALITY
import com.laohei.bili_tube.data.PlayerDataSourceFactory
import com.laohei.bili_tube.data.local.prefs.PreferencesUtil
import com.laohei.bili_tube.network.NetworkType
import com.laohei.bili_tube.network.NetworkUtil
import com.laohei.bili_tube.util.AudioSupportChecker
import com.laohei.bili_tube.util.TrackSupportUtils.isUnsupportedDolbyTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

@UnstableApi
class ExoMediaController(
    private val context: Context,
    private val networkUtil: NetworkUtil,
    private val preferencesUtil: PreferencesUtil
) : MediaController<ExoPlayer>, AnalyticsListener, Player.Listener {

    companion object {
        private val TAG = ExoMediaController::class.simpleName
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    val networkType = networkUtil.getNetworkType()
    val videoQuality = if (networkType == NetworkType.NETWORK_TYPE_CELLULAR) {
        preferencesUtil.getValue(MOBILE_NET_VIDEO_QUALITY, 80)
    } else {
        preferencesUtil.getValue(WLAN_VIDEO_QUALITY, Int.MAX_VALUE)
    }

    val audioQuality = if (networkType == NetworkType.NETWORK_TYPE_CELLULAR) {
        preferencesUtil.getValue(MOBILE_NET_AUDIO_QUALITY, 30280)
    } else {
        preferencesUtil.getValue(WLAN_AUDIO_QUALITY, 30251)
    }

    private val _uiState = MutableStateFlow(
        MediaUIState(
            audioQuality = MediaQuality(audioQuality, AudioQualityMap[audioQuality]!!),
            videoQuality = MediaQuality(
                videoQuality,
                if (videoQuality == Int.MAX_VALUE) VideoQualityMap.maxBy { it.key }.value else VideoQualityMap[videoQuality]!!
            )
        )
    )
    override val uiState: StateFlow<MediaUIState> = _uiState.asStateFlow()

    private val renderFactory = DefaultRenderersFactory(context)
        .setEnableDecoderFallback(true)
        .setEnableAudioFloatOutput(AudioSupportChecker.supportsPcmFloat(context))
        .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)

    private val trackSelector = DefaultTrackSelector(context).apply {
        parameters = buildUponParameters()
            .setForceHighestSupportedBitrate(false)
            .build()
    }

    private val localControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(5_000, 120_000, 1_000, 5_000)
        .setAllocator(DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE * 10))
        .setPrioritizeTimeOverSizeThresholds(false)
        .build()

    override val player: ExoPlayer = ExoPlayer.Builder(context)
        .setRenderersFactory(renderFactory)
        .setTrackSelector(trackSelector)
        .setLoadControl(localControl)
        .build().apply {
            addAnalyticsListener(this@ExoMediaController)
            addListener(this@ExoMediaController)
        }

    private val networkDataSource = PlayerDataSourceFactory.build(context)
    private var backSources: List<MediaSource> = emptyList()
    private var currentSource: MediaSource? = null
    private var isUnsupportedCodecFound = false
    private var progressUpdateJob: Job? = null
    private var playbackEndListener: MediaController.OnPlaybackEndListener? = null

    override val isPlaying: Boolean
        get() = player.isPlaying
    override val duration: Long
        get() = player.duration
    override val currentPosition: Long
        get() = player.currentPosition
    override val bufferedPosition: Long
        get() = player.bufferedPosition

    private var skipSegments = emptyMap<SkipType, SkipSegment>()

    fun setSkipSegments(map: Map<SkipType, SkipSegment>) {
        skipSegments = map
    }

    override fun setOnPlaybackEndListener(listener: MediaController.OnPlaybackEndListener) {
        playbackEndListener = listener
    }

    override fun setSupportQualities(qualities: List<MediaQuality>) {
        _uiState.update { it.copy(supportQualities = qualities) }
    }

    override fun setQuality(quality: MediaQuality) {
        _uiState.update { it.copy(sessionQuality = quality) }
        coroutineScope.launch {
            val startPositionMs = withContext(Dispatchers.Main) { currentPosition }
            load(backSources.map { it.copy(startPositionMs = startPositionMs) })
        }
    }

    override fun setSpeed(speed: Float) {
        _uiState.update { it.copy(activeSpeed = speed) }
        player.setPlaybackSpeed(speed)
    }

    override fun setSessionSpeed(speed: Float) {
        _uiState.update { it.copy(sessionSpeed = speed) }
        setSpeed(speed)
    }

    private fun startProgressUpdate() {
        progressUpdateJob = CoroutineScope(Dispatchers.Main).launch {
            while (isPlaying) {
                val skip = skipSegments.run {
                    val op = get(SkipType.INTRO)
                    val end = get(SkipType.OUTRO)
                    val current = currentPosition / 1000
                    return@run when {
                        op != null && current in op.start until op.end -> op.end * 1000
                        end != null && current in end.start until end.end -> end.end * 1000
                        else -> null
                    }
                }
                val currentPos = currentPosition
                val totalDuration = duration.takeIf { it > 0 } ?: 1L
                val bufferedPos = bufferedPosition
                val progress = currentPos.toFloat() / totalDuration
                val bufferProgress = bufferedPos.toFloat() / totalDuration
                when {
                    skip != null -> {
                        Log.d(TAG, "startProgressUpdate: skip $skip")
                        seekTo(skip)
                        _uiState.update { it.copy(currentPosition = skip) }
                    }

                    else -> {
                        _uiState.update {
                            it.copy(
                                currentPosition = currentPos,
                                progress = progress,
                                bufferProgress = bufferProgress
                            )
                        }
                    }
                }
                delay(1000)
            }
        }
    }

    override suspend fun load(source: MediaSource) {
        Log.d(TAG, "load: $source")
        currentSource = source
        withContext(Dispatchers.Main) { pause() }
        _uiState.update {
            it.copy(
                activeQuality = source.videoQuality!!,
                isEnd = false,
                isPlaying = false,
                isLoading = true,
                isCoverVisible = true
            )
        }
        val videoSource = source.video?.let {
            ProgressiveMediaSource.Factory(
                networkDataSource
            ).createMediaSource(MediaItem.fromUri(it))
        }

        val audioSource = source.audio?.let {
            ProgressiveMediaSource.Factory(
                networkDataSource
            ).createMediaSource(MediaItem.fromUri(it))
        }

        if (audioSource == null && videoSource == null) {
            Log.d(TAG, "load: audio and video both null, play error")
            return
        }

        val mergeSource = when {
            videoSource != null && audioSource != null -> MergingMediaSource(
                videoSource,
                audioSource
            )

            videoSource != null -> videoSource
            else -> audioSource
        }
        withContext(Dispatchers.Main) {
            player.setMediaSource(mergeSource!!)
            player.seekTo(source.startPositionMs)
            player.prepare()
        }
    }

    override suspend fun load(sources: List<MediaSource>) {
        backSources = sources
        val sessionQuality = _uiState.value.sessionQuality
        // preferentially select user preferred quality
        val prepareSources = when {
            sessionQuality.id == Int.MAX_VALUE -> {
                backSources.groupBy { it.videoQuality!!.id }.maxBy { it.key }.value
            }

            else -> backSources.filter { it.videoQuality!!.id == sessionQuality.id }
        }

        val readySource = when {
            sessionQuality.id == Int.MAX_VALUE && prepareSources.first().videoQuality!!.id == 126
                    || audioQuality == 30250 -> {
                prepareSources.find { it.audioQuality!!.id == 30250 }
                    ?: prepareSources.find { it.audioQuality!!.id == 30251 }
                    ?: prepareSources.filter { it.audioQuality!!.id in NormalAudioQuality }
                        .maxByOrNull { it.audioQuality!!.id }
            }

            audioQuality == 30251 -> {
                prepareSources.find { it.audioQuality!!.id == 30251 }
                    ?: prepareSources.find { it.audioQuality!!.id == 30250 }
                    ?: prepareSources.filter { it.audioQuality!!.id in NormalAudioQuality }
                        .maxByOrNull { it.audioQuality!!.id }
            }

            else -> {
                prepareSources.filter { it.audioQuality!!.id in NormalAudioQuality }
                    .maxByOrNull { it.audioQuality!!.id }
            }
        } ?: return
        load(readySource)
    }

    override fun play() {
        val isEnd = _uiState.value.isEnd
        if (isEnd && currentSource != null) {
            coroutineScope.launch { load(currentSource!!.copy(startPositionMs = 0L)) }
        } else {
            player.play()
        }
    }

    override fun pause() {
        player.pause()
    }

    override fun stop() {
        player.stop()
    }

    override fun release() {
        player.release()
    }

    override fun seekTo(positionMs: Long) {
        _uiState.update {
            it.copy(
                currentPosition = positionMs,
                progress = positionMs / duration.toFloat()
            )
        }
        player.seekTo(positionMs)
    }

    override fun setVolume(volume: Float) {
        player.volume = volume
    }

    override fun setLooping(loop: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> {
                _uiState.update {
                    it.copy(
                        duration = duration,
                        isLoading = false,
                        isCoverVisible = false,
                        isEnd = false
                    )
                }
            }

            Player.STATE_BUFFERING -> {
                _uiState.update { it.copy(isLoading = true, isEnd = false) }
            }

            Player.STATE_ENDED -> {
                _uiState.update { it.copy(currentPosition = duration, progress = 1f, isEnd = true) }
                playbackEndListener?.onPlaybackEnded()
            }

            else -> {}
        }
    }

    override fun onRenderedFirstFrame() {
        _uiState.update { it.copy(isCoverVisible = false) }
    }

    override fun onPlaybackSuppressionReasonChanged(playbackSuppressionReason: Int) {
        when {
            playbackSuppressionReason != Player.PLAYBACK_SUPPRESSION_REASON_NONE -> {
                _uiState.update { it.copy(isLoading = true) }
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _uiState.update {
            it.copy(
                isPlaying = isPlaying,
                isLoading = if (isPlaying) false else it.isLoading
            ).also {
                if (isPlaying) {
                    startProgressUpdate()
                } else {
                    progressUpdateJob?.cancel()
                }
            }
        }
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
        val (width, height) = videoSize.run { width to height }
        if (width <= 0 || height <= 0) {
            return
        }
        val newAspect = width.toFloat() / height
        val currentAspect = _uiState.value.videoAspect
        val aspectDiff = abs(currentAspect - newAspect) > 0.01

        if (!aspectDiff) {
            return
        }

        _uiState.update {
            it.copy(
                videoWidth = width,
                videoHeight = height,
                videoAspect = newAspect
            )
        }
    }

    override fun onTracksChanged(tracks: Tracks) {
        isUnsupportedCodecFound = false
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo ?: return

        loop@ for (rendererIndex in 0 until mappedTrackInfo.rendererCount) {
            val trackGroups = mappedTrackInfo.getTrackGroups(rendererIndex)
            for (groupIndex in 0 until trackGroups.length) {
                val group = trackGroups[groupIndex]
                for (trackIndex in 0 until group.length) {
                    val format = group.getFormat(trackIndex)
                    if (isUnsupportedDolbyTrack(
                            mappedTrackInfo,
                            format,
                            rendererIndex,
                            groupIndex,
                            trackIndex
                        )
                    ) {
                        isUnsupportedCodecFound = true
                        break@loop
                    }
                }
            }
        }

        if (isUnsupportedCodecFound) {
            Log.w(TAG, "Unsupported Dolby Vision track detected, stopping player.")
            player.stop()
            // TODO switch other support source to instead of current dolby
            loadAfterPlayError()
        } else {
            Log.d(TAG, "All tracks supported, continue playback.")
            player.play()
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        Log.e(TAG, "onPlayerError: ${error.message}")
    }

    private fun loadAfterPlayError() {
        CoroutineScope(Dispatchers.IO).launch {
            if (currentSource == null) return@launch
            val prepareSources =
                backSources.filter { it.videoQuality!!.id < currentSource!!.videoQuality!!.id }
                    .sortedByDescending { it.audioQuality!!.id }
            val source =
                prepareSources.find { it.audioQuality!!.id == currentSource!!.audioQuality!!.id }
                    ?: prepareSources.filter { it.audioQuality!!.id in NormalAudioQuality }
                        .maxByOrNull { it.audioQuality!!.id } ?: return@launch
            load(source)
        }
    }
}
package com.laohei.bili_tube.features.player.state.media

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSink
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.cronet.CronetDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.audio.AudioCapabilities
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.DefaultAllocator
import com.laohei.bili_sdk.module_v2.video.DashItem
import com.laohei.bili_sdk.module_v2.video.SkipModel
import com.laohei.bili_sdk.module_v2.video.VideoURLModel
import com.laohei.bili_tube.core.NormalAudioQuality
import com.laohei.bili_tube.utill.HttpClientFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.chromium.net.CronetEngine
import java.io.File
import java.util.concurrent.Executors
import kotlin.math.abs

@UnstableApi
internal class DefaultMediaController(
    context: Context,
    cronetEngine: CronetEngine,
    simpleCache: SimpleCache,
    originalWidth: Int,
    originalHeight: Int
) : MediaController, AnalyticsListener, Player.Listener {

    companion object {
        private val TAG = DefaultMediaController::class.simpleName
        private const val DBG = true
    }

    private var _isUnsupportedCodecFound = false
    private val _isSupportsFloat =
        AudioCapabilities.getCapabilities(context, AudioAttributes.DEFAULT, null)
            .supportsEncoding(C.ENCODING_PCM_FLOAT)

    private val _renderersFactory = DefaultRenderersFactory(context)
        .setEnableDecoderFallback(true) // 允许解码器回退
        .setEnableAudioFloatOutput(_isSupportsFloat)
        .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)

    private val _defaultTrackSelector = DefaultTrackSelector(context).apply {
        parameters = buildUponParameters()
            .setForceHighestSupportedBitrate(false)
            .build()
    }

    private val _defaultLocalControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(5_000, 120_000, 1_000, 5_000)
//        .setBufferDurationsMs(100_000, 200_000, 3_000, 6_000)
        .setAllocator(DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE * 10))
        .setPrioritizeTimeOverSizeThresholds(false)
        .build()

    override val exoPlayer: ExoPlayer = ExoPlayer.Builder(context)
        .setRenderersFactory(_renderersFactory)
        .setTrackSelector(_defaultTrackSelector)
        .setLoadControl(_defaultLocalControl)
        .build()

    private var _media: VideoURLModel? = null

    //    private var mBackVideoSources: List<VideoSource>? = null
    private var _currentSelectedIndex = 0
    private var _currentWidth: Int? = null
    private var _currentHeight: Int? = null

    private val _cronetDataSource =
        CronetDataSource.Factory(cronetEngine, Executors.newFixedThreadPool(5)).apply {
            setDefaultRequestProperties(
                mapOf(
                    "referer" to HttpClientFactory.REFERER,
                    "User-Agent" to HttpClientFactory.USER_AGENT
                )
            )
        }

    private val _defaultLocalDataSourceFactory = DefaultDataSource.Factory(context)

    private val _defaultDataSourceFactory = CacheDataSource.Factory()
        .setCache(simpleCache)
        .setUpstreamDataSourceFactory(_cronetDataSource)
        .setCacheWriteDataSinkFactory(
            CacheDataSink.Factory()
                .setCache(simpleCache)
                .setFragmentSize(50 * 1024 * 1024)
        )

    private var _otherDataSourceFactory: DataSource.Factory? = null

    private var _progressUpdateJob: Job? = null

    private val _mediaState = MutableStateFlow(
        MediaState(
            width = originalWidth,
            height = originalHeight
        )
    )

    override val mediaState: StateFlow<MediaState> = _mediaState

    var playEnd: (() -> Unit)? = null
    var playError: (() -> Unit)? = null

    private var _skipModels: Map<String, SkipModel>? = null

    init {
        exoPlayer.addAnalyticsListener(this)
        exoPlayer.addListener(this)
    }

    override fun play(
        media: VideoURLModel,
        dataSourceFactory: DataSource.Factory?
    ) {
        _media = media
        _otherDataSourceFactory = dataSourceFactory
        _currentSelectedIndex = 0
        play(getInitPosition())
    }

    // play local video
    override fun play(videoUrl: String, audioUrl: String?) {
        val videoFile = File(videoUrl)
        if (videoFile.exists().not()) {
            updateMediaState(mediaState.value.copy(isError = true))
            return
        }
        if (audioUrl == null) {
            val mediaItem = MediaItem.fromUri(Uri.fromFile(videoFile))
            exoPlayer.setMediaItem(mediaItem)
        } else {
            val videoSource = ProgressiveMediaSource.Factory(_defaultLocalDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.fromFile(videoFile)))
            val audioMediaSource = ProgressiveMediaSource.Factory(_defaultLocalDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.fromFile(File(audioUrl))))
            val mergedMediaSource = MergingMediaSource(videoSource, audioMediaSource)
            exoPlayer.setMediaSource(mergedMediaSource)
        }
        exoPlayer.prepare()
    }

    override fun seekToFraction(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
        _mediaState.update {
            it.copy(
                currentDuration = positionMs,
                progress = positionMs / exoPlayer.duration.toFloat()
            )
        }
    }

    override fun seekToFraction(fraction: Float) {
        seekToFraction((exoPlayer.duration * fraction).toLong())
    }

    override fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    override fun release() {
        exoPlayer.release()
    }

    override fun setPlaybackSpeed(speed: Float) {
        _mediaState.update { it.copy(speed = speed) }
        exoPlayer.setPlaybackSpeed(speed)
    }

    fun setSkipModel(value: Map<String, SkipModel>?) {
        _skipModels = value
    }

    private fun startProgressUpdate() {
        _progressUpdateJob = CoroutineScope(Dispatchers.Main).launch {
            while (exoPlayer.isPlaying) {
                val skip = _skipModels?.run {
                    val op = get("op")
                    val end = get("end")
                    val current = exoPlayer.currentPosition / 1000
                    if (op == null && end == null) return@run null
                    if (op != null && current in op.start until op.end) {
                        op.end * 1000
                    } else if (end != null && current in end.start until end.end) {
                        end.end * 1000
                    } else {
                        null
                    }
                }
                val currentPos = exoPlayer.currentPosition
                val duration = exoPlayer.duration.takeIf { it > 0 } ?: 1L
                val bufferedPos = exoPlayer.bufferedPosition
                skip?.let { position ->
                    Log.d(TAG, "startProgressUpdate: skip $position")
                    exoPlayer.seekTo(position)
                    _mediaState.update { it.copy(currentDuration = position) }
                } ?: _mediaState.update {
                    it.copy(
                        currentDuration = currentPos,
                        progress = currentPos.toFloat() / duration,
                        bufferProgress = bufferedPos.toFloat() / duration,
                    )
                }
                delay(1000)
            }
        }
    }

    private fun stopProgressUpdate() {
        _progressUpdateJob?.cancel()
    }

    private fun getInitPosition(): Long {
        if (_media == null) {
            return 0L
        }
        val diff = _media!!.timeLength - _media!!.lastPlayTime
        return if (diff > 1000) {
            _media!!.lastPlayTime
        } else {
            0L
        }
    }

    private fun play(initialPosition: Long) {
        val qualities = _mediaState.value.quality
        var videoQuality = _mediaState.value.videoQuality
        val audioQuality = _mediaState.value.audioQuality
        val dash = _media!!.dash
        if (DBG) {
            Log.d("DefaultMediaManager", "play: selected quality: $videoQuality")
            Log.d("DefaultMediaManager", "play: all qualities: $audioQuality")
        }
        if (dash != null) {
            var currentQualityVideos =
                when {
                    videoQuality.first == Int.MAX_VALUE -> {
                        dash.video.sortedByDescending { it.id }
                    }

                    else -> {
                        dash.video.filter { it.id == videoQuality.first }
                    }
                }
            while (_isUnsupportedCodecFound || _currentSelectedIndex >= currentQualityVideos.size) {
                var nextQuality = qualities.indexOfFirst { videoQuality.first == it.first }
                nextQuality = if (nextQuality + 1 >= qualities.size) {
                    0
                } else {
                    nextQuality + 1
                }
                videoQuality = qualities[nextQuality]
                _mediaState.update { it.copy(videoQuality = videoQuality) }
                currentQualityVideos =
                    _media!!.dash!!.video.filter { it.id == videoQuality.first }
                break
            }
            if (DBG) {
                Log.d(TAG, "play: $currentQualityVideos")
            }
            val videoUrl = currentQualityVideos[_currentSelectedIndex].baseUrl
            if (DBG) {
                Log.d(TAG, "play: video quality ${videoQuality.first}")
                Log.d(TAG, "play: video url $videoUrl")
            }
            val audioItem = dash.let {
                return@let when (audioQuality) {
                    30251 -> getHiResAudio() ?: getDolbyAudio() ?: getNormalAudio()
                    30250 -> getDolbyAudio() ?: getHiResAudio() ?: getNormalAudio()
                    else -> {
                        it.audio.find { audio -> audio.id == audioQuality } ?: getNormalAudio()
                    }
                }
            }

            if (DBG) {
                Log.d(TAG, "play: audio quality ${audioItem?.id}")
            }
            _currentWidth = currentQualityVideos[_currentSelectedIndex].width
            _currentHeight = currentQualityVideos[_currentSelectedIndex].height
            val video = ProgressiveMediaSource.Factory(
                _otherDataSourceFactory ?: _defaultDataSourceFactory
            ).createMediaSource(MediaItem.fromUri(videoUrl))
            val audio = audioItem?.run {
                if (DBG) {
                    Log.d(TAG, "play: audio url ${this.baseUrl}")
                }
                ProgressiveMediaSource.Factory(
                    _otherDataSourceFactory ?: _defaultDataSourceFactory
                ).createMediaSource(MediaItem.fromUri(this.baseUrl))
            }
            val merge = audio?.run {
                MergingMediaSource(video, this)
            } ?: video
            exoPlayer.setMediaSource(merge)
        } else {
            val currentSources = _media!!.durl!!
            if (_currentSelectedIndex >= currentSources.size) {
                _currentSelectedIndex = 0
            }
            val videoUrl = currentSources[_currentSelectedIndex].url
            val video = ProgressiveMediaSource.Factory(
                _otherDataSourceFactory ?: _defaultDataSourceFactory
            )
                .createMediaSource(MediaItem.fromUri(videoUrl))
            exoPlayer.setMediaSource(video)
        }

        exoPlayer.seekTo(initialPosition)
        exoPlayer.prepare()
    }

    private fun getHiResAudio(): DashItem? {
        return _media?.dash?.flac?.audio
    }

    private fun getDolbyAudio(): DashItem? {
        return _media?.dash?.dolby?.audio?.run {
            if (isEmpty()) null else first()
        }
    }

    private fun getNormalAudio(): DashItem? {
        return _media?.dash?.audio?.filter { it.id in NormalAudioQuality }
            ?.maxByOrNull { it.id }
    }

    override fun setBuffering(enabled: Boolean) {
        if (enabled) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
        _mediaState.update { it.copy(isLoading = enabled) }
    }

    override fun updateMediaState(state: MediaState) {
        _mediaState.update { state }
    }

    override fun changeQuality(quality: Pair<Int, String>) {
        if (quality.first == _mediaState.value.videoQuality.first) {
            return
        }
        _mediaState.update { it.copy(videoQuality = quality, isUserSelectedQuality = true) }
        _currentSelectedIndex = 0
        play(_mediaState.value.currentDuration)
    }

    private fun autoSkip(duration: Long) {
        exoPlayer.seekTo(duration)
        _mediaState.update { it.copy(currentDuration = duration) }
    }

    fun getVideoSourceByQuality(quality: Int): Pair<List<String>, List<String>?> {
        if (DBG) {
            Log.d(TAG, "getVideoSourceByQuality: download selected quality $quality")
            Log.d(
                TAG,
                "getVideoSourceByQuality: audio source ids ${_media?.dash?.audio?.map { it.id }}"
            )
        }
        return if (_media!!.dash != null) {
            val currentQualityVideos =
                _media!!.dash!!.video.filter { it.id == quality }
            val audios = buildList {
                _media?.dash?.let { dash ->
                    if (quality >= 126) {
                        dash.dolby?.audio?.let { add(it.first().baseUrl) }
                        dash.flac?.audio?.let { add(it.baseUrl) }
                    }
                    addAll(
                        dash.audio.sortedByDescending { it.id }.map { it.baseUrl }
                    )
                }
            }
            Pair(currentQualityVideos.map { it.baseUrl }, audios)
        } else {
            val currentSources = _media!!.durl!!
            Pair(currentSources.map { it.url }, null)
        }
    }

    // TODO AnalyticsListener
    override fun onAudioDecoderInitialized(
        eventTime: AnalyticsListener.EventTime,
        decoderName: String,
        initializedTimestampMs: Long,
        initializationDurationMs: Long
    ) {
        if (DBG) {
            Log.d(TAG, "Audio decoder: $decoderName")
        }
    }

    override fun onVideoDecoderInitialized(
        eventTime: AnalyticsListener.EventTime,
        decoderName: String,
        initializedTimestampMs: Long,
        initializationDurationMs: Long
    ) {
        if (DBG) {
            Log.d(TAG, "Video decoder: $decoderName")
        }
    }

    override fun onVideoCodecError(
        eventTime: AnalyticsListener.EventTime,
        videoCodecError: Exception
    ) {
        if (DBG) {
            Log.d(TAG, "Video decoder: ${videoCodecError.message}")
        }
    }

    // TODO Player.Listener
    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> {
                _mediaState.update {
                    it.copy(
                        totalDuration = exoPlayer.duration,
                        isLoading = false,
                        showCover = false
                    )
                }
            }

            Player.STATE_BUFFERING -> {
                _mediaState.update { it.copy(isLoading = true) }
            }

            Player.STATE_ENDED -> {
                _mediaState.update {
                    it.copy(
                        currentDuration = exoPlayer.duration,
                        progress = 1f
                    )
                }
                playEnd?.invoke()
            }

            else -> {

            }
        }
    }

    override fun onRenderedFirstFrame() {
        _mediaState.update {
            it.copy(showCover = false)
        }
    }

    override fun onPlaybackSuppressionReasonChanged(playbackSuppressionReason: Int) {
        when {
            playbackSuppressionReason != Player.PLAYBACK_SUPPRESSION_REASON_NONE -> {
                _mediaState.update { it.copy(isLoading = true) }
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _mediaState.update {
            if (isPlaying) {
                it.copy(isPlaying = true, isLoading = false).also {
                    startProgressUpdate()
                }
            } else {
                it.copy(isPlaying = false).also {
                    stopProgressUpdate()
                }
            }
        }
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
        val newWidth = if (videoSize.width > 0) videoSize.width else _currentWidth ?: 0
        val newHeight = if (videoSize.height > 0) videoSize.height else _currentHeight ?: 0
        val originalWidth = _mediaState.value.width
        val originalHeight = _mediaState.value.height
        if (newWidth > 0 && newHeight > 0) {
            val newAspect = newWidth.toDouble() / newHeight
            val originalAspect = originalWidth.toDouble() / originalHeight
            val hasDiff = abs(originalAspect - newAspect) > 0.01
            if (hasDiff.not()) {
                return
            }
            _mediaState.update {
                it.copy(
                    width = newWidth,
                    height = newHeight
                )
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        if (DBG) {
            Log.d(TAG, "onPlayerError: ${error.message}")
        }
        // TODO auto switch other source when play error
    }

    override fun onTracksChanged(tracks: Tracks) {
        _isUnsupportedCodecFound = false
        val mappedTrackInfo = _defaultTrackSelector.currentMappedTrackInfo ?: return

        for (rendererIndex in 0 until mappedTrackInfo.rendererCount) {
            val trackGroups = mappedTrackInfo.getTrackGroups(rendererIndex)
            for (groupIndex in 0 until trackGroups.length) {
                val group = trackGroups.get(groupIndex)
                for (trackIndex in 0 until group.length) {
                    val format = group.getFormat(trackIndex)
                    if (format.sampleMimeType == MimeTypes.VIDEO_DOLBY_VISION) {
                        val support = mappedTrackInfo.getTrackSupport(
                            rendererIndex,
                            groupIndex,
                            trackIndex
                        )
                        val codec = format.codecs
                        if (DBG) {
                            Log.d(
                                TAG,
                                "MIME=${format.sampleMimeType}, CODEC=$codec, support=$support"
                            )
                        }

                        if (support == C.FORMAT_HANDLED) {
                            if (DBG) {
                                Log.d(TAG, "✅ 支持播放 Dolby Vision track: $codec")
                            }
                        } else {
                            if (DBG) {
                                Log.w(TAG, "❌ 不支持播放 Dolby Vision track: $codec")
                            }
                            _isUnsupportedCodecFound = true
                        }
                    }
                }
            }
        }
        if (_isUnsupportedCodecFound) {
            exoPlayer.stop()
            play(getInitPosition()) // switch other support source to instead of current dolby
        } else {
            exoPlayer.play()
        }
    }
}
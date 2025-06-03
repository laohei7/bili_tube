package com.laohei.bili_tube.presentation.player.state.media

import android.content.Context
import android.net.Uri
import android.util.Log
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
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.cronet.CronetDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.DefaultAllocator
import com.laohei.bili_sdk.module_v2.video.DashItem
import com.laohei.bili_sdk.module_v2.video.SkipModel
import com.laohei.bili_sdk.module_v2.video.VideoURLModel
import com.laohei.bili_tube.model.VideoSource
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

@UnstableApi
internal class DefaultMediaManager(
    context: Context,
    cronetEngine: CronetEngine,
    simpleCache: SimpleCache,
    originalWidth: Int,
    originalHeight: Int
) : MediaManager, AnalyticsListener, Player.Listener {

    companion object {
        private val TAG = DefaultMediaManager::class.simpleName
        private const val DBG = true
    }

    private var mUnsupportedCodecFound = false

    private val mRenderersFactory = DefaultRenderersFactory(context)
        .setEnableDecoderFallback(true) // 允许解码器回退
//        .setEnableAudioFloatOutput(true)
        .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)

    private val mDefaultTrackSelector = DefaultTrackSelector(context).apply {
        parameters = buildUponParameters()
            .setForceHighestSupportedBitrate(true)
            .build()
    }

    private val mDefaultLocalControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(100_000, 200_000, 3_000, 6_000)
        .setAllocator(DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE * 2))
        .build()

    private val mExoPlayer = ExoPlayer.Builder(context)
        .setRenderersFactory(mRenderersFactory)
        .setTrackSelector(mDefaultTrackSelector)
        .setLoadControl(mDefaultLocalControl)
        .build()

    private var mVideoURLModel: VideoURLModel? = null
    private var mBackVideoSources: List<VideoSource>? = null
    private var mCurrentSelectedIndex = 0
    private var mCurrentVideoWidth: Int? = null
    private var mCurrentVideoHeight: Int? = null

    private val mCronetDataSource =
        CronetDataSource.Factory(cronetEngine, Executors.newSingleThreadExecutor()).apply {
            setDefaultRequestProperties(
                mapOf("referer" to "https://www.bilibili.com", "User-Agent" to "K/3")
            )
        }

    private val mDefaultLocalDataSourceFactory = DefaultDataSource.Factory(context)

    private val mDefaultDataSourceFactory = CacheDataSource.Factory()
        .setCache(simpleCache)
        .setUpstreamDataSourceFactory(mCronetDataSource)
        .setCacheWriteDataSinkFactory(null)

    private var mOtherDataSourceFactory: DataSource.Factory? = null

    private var mProgressUpdateJob: Job? = null

    private val _mState = MutableStateFlow(
        MediaState(
            width = originalWidth,
            height = originalHeight
        )
    )

    override val state: StateFlow<MediaState>
        get() = _mState

    var playEndCallback: (() -> Unit)? = null
    var playErrorCallback: (() -> Unit)? = null

    private var mSkipModel: Map<String, SkipModel>? = null

    init {
        mExoPlayer.addAnalyticsListener(this)
        mExoPlayer.addListener(this)
    }

    override fun play(
        videoURLModel: VideoURLModel,
        dataSourceFactory: DataSource.Factory?
    ) {
        mVideoURLModel = videoURLModel
        mOtherDataSourceFactory = dataSourceFactory
        mCurrentSelectedIndex = 0
        play(getInitPosition())
    }

    // 播放本地视频
    override fun play(video: String, audio: String?) {
        val videoFile = File(video)
        if (videoFile.exists().not()) {
            updateMediaState(state.value.copy(isError = true))
            return
        }
        if (audio == null) {
            val mediaItem = MediaItem.fromUri(Uri.fromFile(videoFile))
            mExoPlayer.setMediaItem(mediaItem)
        } else {
            val videoSource = ProgressiveMediaSource.Factory(mDefaultLocalDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.fromFile(videoFile)))
            val audioMediaSource = ProgressiveMediaSource.Factory(mDefaultLocalDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.fromFile(File(audio))))
            val mergedMediaSource = MergingMediaSource(videoSource, audioMediaSource)
            mExoPlayer.setMediaSource(mergedMediaSource)
        }
        mExoPlayer.prepare()
    }

    override fun seekTo(duration: Long) {
        mExoPlayer.seekTo(duration)
        _mState.update {
            it.copy(
                currentDuration = duration,
                progress = duration / mExoPlayer.duration.toFloat()
            )
        }
    }

    override fun seekTo(position: Float) {
        seekTo((mExoPlayer.duration * position).toLong())
    }

    override fun togglePlayPause() {
        if (mExoPlayer.isPlaying) {
            mExoPlayer.pause()
        } else {
            mExoPlayer.play()
        }
    }

    override fun release() {
        mExoPlayer.release()
    }

    override fun exoPlayer(): ExoPlayer {
        return mExoPlayer
    }

    override fun setSpeed(speed: Float) {
        _mState.update { it.copy(speed = speed) }
        mExoPlayer.setPlaybackSpeed(speed)
    }

    fun setSkipModel(value: Map<String, SkipModel>?) {
        mSkipModel = value
    }

    private fun startProgressUpdate() {
        mProgressUpdateJob = CoroutineScope(Dispatchers.Main).launch {
            while (mExoPlayer.isPlaying) {
                val skip = mSkipModel?.run {
                    val op = get("op")
                    val end = get("end")
                    val current = mExoPlayer.currentPosition / 1000
                    if (op == null && end == null) return@run null
                    if (op != null && current in op.start until op.end) {
                        return@run op.end * 1000
                    }
                    if (end != null && current in end.start until end.end) {
                        return@run end.end * 1000
                    }
                    return@run null
                }
                skip?.let {
                    Log.d(TAG, "startProgressUpdate: skip $it")
                    autoSkip(it)
                } ?: run {
                    _mState.update {
                        it.copy(
                            currentDuration = mExoPlayer.currentPosition,
                            progress = mExoPlayer.currentPosition.toFloat() / mExoPlayer.duration,
                            bufferProgress = mExoPlayer.bufferedPosition.toFloat() / mExoPlayer.duration,
                        )
                    }
                    delay(1000)
                }
            }
        }
    }

    private fun stopProgressUpdate() {
        mProgressUpdateJob?.cancel()
    }

    private fun getInitPosition(): Long {
        if (mVideoURLModel == null) {
            return 0L
        }
        val diff = mVideoURLModel!!.timeLength - mVideoURLModel!!.lastPlayTime
        return if (diff > 1000) {
            mVideoURLModel!!.lastPlayTime
        } else {
            0L
        }
    }

    private fun play(initialPosition: Long) {
        val qualities = _mState.value.quality
        var videoQuality = _mState.value.videoQuality
        val audioQuality = _mState.value.audioQuality
        val dash = mVideoURLModel!!.dash
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
            while (mUnsupportedCodecFound || mCurrentSelectedIndex >= currentQualityVideos.size) {
                var nextQuality = qualities.indexOfFirst { videoQuality.first == it.first }
                nextQuality = if (nextQuality + 1 >= qualities.size) {
                    0
                } else {
                    nextQuality + 1
                }
                videoQuality = qualities[nextQuality]
                _mState.update { it.copy(videoQuality = videoQuality) }
                currentQualityVideos =
                    mVideoURLModel!!.dash!!.video.filter { it.id == videoQuality.first }
                break
            }
            if (DBG) {
                Log.d(TAG, "play: $currentQualityVideos")
            }
            val videoUrl = currentQualityVideos[mCurrentSelectedIndex].baseUrl
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
            mCurrentVideoWidth = currentQualityVideos[mCurrentSelectedIndex].width
            mCurrentVideoHeight = currentQualityVideos[mCurrentSelectedIndex].height
            val video = ProgressiveMediaSource.Factory(
                mOtherDataSourceFactory ?: mDefaultDataSourceFactory
            ).createMediaSource(MediaItem.fromUri(videoUrl))
            val audio = audioItem?.run {
                if (DBG) {
                    Log.d(TAG, "play: audio url ${this.baseUrl}")
                }
                ProgressiveMediaSource.Factory(
                    mOtherDataSourceFactory ?: mDefaultDataSourceFactory
                ).createMediaSource(MediaItem.fromUri(this.baseUrl))
            }
            val merge = audio?.run {
                MergingMediaSource(video, this)
            } ?: video
            mExoPlayer.setMediaSource(merge)
        } else {
            val currentSources = mVideoURLModel!!.durl!!
            if (mCurrentSelectedIndex >= currentSources.size) {
                mCurrentSelectedIndex = 0
            }
            val videoUrl = currentSources[mCurrentSelectedIndex].url
            val video = ProgressiveMediaSource.Factory(
                mOtherDataSourceFactory ?: mDefaultDataSourceFactory
            )
                .createMediaSource(MediaItem.fromUri(videoUrl))
            mExoPlayer.setMediaSource(video)
        }

        mExoPlayer.seekTo(initialPosition)
        mExoPlayer.prepare()
    }

    private fun getHiResAudio(): DashItem? {
        return mVideoURLModel?.dash?.flac?.audio
    }

    private fun getDolbyAudio(): DashItem? {
        return mVideoURLModel?.dash?.dolby?.audio?.run {
            if (isEmpty()) null else first()
        }
    }

    private fun getNormalAudio(): DashItem? {
        return mVideoURLModel?.dash?.audio?.filter { it.id in NormalAudioQuality }
            ?.maxByOrNull { it.id }
    }

    override fun toggleLoading() {
        mExoPlayer.pause()
        _mState.update { it.copy(isLoading = true) }
    }

    override fun updateMediaState(other: MediaState) {
        _mState.update { other }
    }

    override fun switchQuality(quality: Pair<Int, String>) {
        if (quality.first == _mState.value.videoQuality.first) {
            return
        }
        _mState.update { it.copy(videoQuality = quality) }
        mCurrentSelectedIndex = 0
        play(_mState.value.currentDuration)
    }

    private fun autoSkip(duration: Long) {
        mExoPlayer.seekTo(duration)
        _mState.update { it.copy(currentDuration = duration) }
    }

    fun getVideoSourceByQuality(quality: Int): Pair<List<String>, List<String>?> {
        if (DBG) {
            Log.d(TAG, "getVideoSourceByQuality: download selected quality $quality")
            Log.d(
                TAG,
                "getVideoSourceByQuality: audio source ids ${mVideoURLModel?.dash?.audio?.map { it.id }}"
            )
        }
        return if (mVideoURLModel!!.dash != null) {
            val currentQualityVideos =
                mVideoURLModel!!.dash!!.video.filter { it.id == quality }
            val audios = buildList {
                mVideoURLModel?.dash?.let { dash ->
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
            val currentSources = mVideoURLModel!!.durl!!
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
            Log.d(TAG, "音频解码器: $decoderName")
        }
    }

    override fun onVideoDecoderInitialized(
        eventTime: AnalyticsListener.EventTime,
        decoderName: String,
        initializedTimestampMs: Long,
        initializationDurationMs: Long
    ) {
        if (DBG) {
            Log.d(TAG, "视频解码器: $decoderName")
        }
    }

    override fun onVideoCodecError(
        eventTime: AnalyticsListener.EventTime,
        videoCodecError: Exception
    ) {
        if (DBG) {
            Log.d(TAG, "视频解码器: ${videoCodecError.message}")
        }
    }

    // TODO Player.Listener
    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> {
                _mState.update {
                    it.copy(
                        totalDuration = mExoPlayer.duration,
                        isLoading = false
                    )
                }
            }

            Player.STATE_BUFFERING -> {
                _mState.update { it.copy(isLoading = true) }
            }

            Player.STATE_ENDED -> {
                _mState.update {
                    it.copy(
                        currentDuration = mExoPlayer.duration,
                        progress = 1f
                    )
                }
                playEndCallback?.invoke()
            }

            else -> {

            }
        }
    }

    override fun onPlaybackSuppressionReasonChanged(playbackSuppressionReason: Int) {
        when {
            playbackSuppressionReason != Player.PLAYBACK_SUPPRESSION_REASON_NONE -> {
                _mState.update { it.copy(isLoading = true) }
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _mState.update {
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
        val width = mCurrentVideoWidth ?: videoSize.width
        val height = mCurrentVideoHeight ?: videoSize.height
        if (width > 0 && height > 0) {
            _mState.update {
                it.copy(
                    width = width,
                    height = height
                )
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        if (DBG) {
            Log.d(TAG, "onPlayerError: ${error.message}")
        }
        mBackVideoSources?.let { sources ->
            if (mCurrentSelectedIndex < sources.size) {
                mCurrentSelectedIndex++
                play(getInitPosition())
            } else {
                _mState.update { it.copy(isError = true) }
                playErrorCallback?.invoke()
            }
        }
    }

    override fun onTracksChanged(tracks: Tracks) {
        mUnsupportedCodecFound = false
        val mappedTrackInfo = mDefaultTrackSelector.currentMappedTrackInfo ?: return

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
                            mUnsupportedCodecFound = true
                        }
                    }
                }
            }
        }
        if (mUnsupportedCodecFound) {
            mExoPlayer.stop()
            play(getInitPosition())
        } else {
            mExoPlayer.play()
        }
    }
}
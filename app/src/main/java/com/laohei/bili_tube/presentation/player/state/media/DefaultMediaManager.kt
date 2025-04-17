package com.laohei.bili_tube.presentation.player.state.media

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.cronet.CronetDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.DefaultAllocator
import com.laohei.bili_sdk.module_v2.video.DashItem
import com.laohei.bili_sdk.module_v2.video.VideoURLModel
import com.laohei.bili_tube.model.SourceType
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
) : MediaManager {

    companion object {
        private val TAG = DefaultMediaManager::class.simpleName
        private const val DBG = true
    }

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

    init {
        mExoPlayer.addListener(
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
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
                    super.onPlaybackSuppressionReasonChanged(playbackSuppressionReason)
                    when {
                        playbackSuppressionReason != Player.PLAYBACK_SUPPRESSION_REASON_NONE -> {
                            _mState.update { it.copy(isLoading = true) }
                        }
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
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
                    super.onVideoSizeChanged(videoSize)
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
//                    mBackVideoSources?.let { sources ->
//                        if (sources.isEmpty()) {
//                            return@let
//                        }
//                        val video = sources[mCurrentSelectedIndex]
//                        val width = video.width ?: videoSize.width
//                        val height = video.height ?: videoSize.height
//
//                        if (width > 0 && height > 0) {
//                            _mState.update {
//                                it.copy(
//                                    width = width,
//                                    height = height
//                                )
//                            }
//                        }
//                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
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
            }
        )
    }

    @OptIn(UnstableApi::class)
    override fun play(mediaSource: MediaSource) {
        resetBackSources()
        mExoPlayer.setMediaSource(mediaSource)
        mExoPlayer.prepare()
        mExoPlayer.play()
    }

    override fun play(videoSources: List<VideoSource>, dataSourceFactory: DataSource.Factory?) {
        resetBackSources(videoSources)
        mOtherDataSourceFactory = dataSourceFactory

        val currentVideoSource = mBackVideoSources!![mCurrentSelectedIndex]
        val videoUrl = currentVideoSource.videoUrl
        val audioUrl = currentVideoSource.audioUrl
        val videoMediaSource = when (currentVideoSource.sourceType) {
            is SourceType.Hls -> HlsMediaSource.Factory(
                mOtherDataSourceFactory ?: mDefaultDataSourceFactory
            )
                .createMediaSource(MediaItem.fromUri(videoUrl))

            else -> ProgressiveMediaSource.Factory(
                mOtherDataSourceFactory ?: mDefaultDataSourceFactory
            )
                .createMediaSource(MediaItem.fromUri(videoUrl))
        }

        val mergedMediaSource = if (audioUrl != null) {
            val audioMediaSource = when (currentVideoSource.sourceType) {
                is SourceType.Hls -> HlsMediaSource.Factory(
                    mOtherDataSourceFactory ?: mDefaultDataSourceFactory
                )
                    .createMediaSource(MediaItem.fromUri(audioUrl))

                else -> ProgressiveMediaSource.Factory(
                    mOtherDataSourceFactory ?: mDefaultDataSourceFactory
                )
                    .createMediaSource(MediaItem.fromUri(audioUrl))
            }
            MergingMediaSource(videoMediaSource, audioMediaSource)
        } else {
            videoMediaSource
        }

        mExoPlayer.setMediaSource(mergedMediaSource)
        mExoPlayer.prepare()
        mExoPlayer.play()
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

    override fun play(path: String) {
        val videoFile = File(path)
        if (videoFile.exists().not()) {
            updateMediaState(state.value.copy(isError = true))
            return
        }
        val mediaItem = MediaItem.fromUri(Uri.fromFile(videoFile))
        mExoPlayer.setMediaItem(mediaItem)
        mExoPlayer.prepare()
        mExoPlayer.play()
    }

    override fun seekTo(duration: Long) {
        mExoPlayer.seekTo(duration)
        _mState.update { it.copy(progress = duration / mExoPlayer.duration.toFloat()) }
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

    fun startProgressUpdate() {
        mProgressUpdateJob = CoroutineScope(Dispatchers.Main).launch {
            while (mExoPlayer.isPlaying) {
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

    fun stopProgressUpdate() {
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
        if (DBG) {
            Log.d("DefaultMediaManager", "play: selected quality: ${_mState.value.defaultQuality}")
            Log.d("DefaultMediaManager", "play: all qualities: ${_mState.value.quality}")
        }
        if (mVideoURLModel!!.dash != null) {
            var currentQualityVideos =
                mVideoURLModel!!.dash!!.video.filter { it.id == _mState.value.defaultQuality.first }
            if (currentQualityVideos.isEmpty()) {
                return
            }
            if (mCurrentSelectedIndex >= currentQualityVideos.size) {
                var nextQuality =
                    _mState.value.quality.indexOfFirst { _mState.value.defaultQuality.first == it.first }
                nextQuality = if (nextQuality + 1 >= _mState.value.quality.size) {
                    0
                } else {
                    nextQuality + 1
                }
                _mState.update { it.copy(defaultQuality = it.quality[nextQuality]) }
                currentQualityVideos =
                    mVideoURLModel!!.dash!!.video.filter { it.id == _mState.value.defaultQuality.first }
            }
            val videoUrl = currentQualityVideos[mCurrentSelectedIndex].baseUrl
            if (DBG) {
                Log.d(TAG, "play: video url $videoUrl")
            }
            val audioItem = mVideoURLModel?.dash?.let { dash ->
                Log.d(TAG, "play: ${dash.audio}")
                Log.d(TAG, "play: ${dash.dolby}")
                Log.d(TAG, "play: ${dash.flac}")
//                val defaultQuality = _mState.value.defaultQuality.first
//                when {
//                    defaultQuality >= 126 -> {
//                        dash.dolby?.audio?.first() ?: run {
//                            dash.flac?.audio ?: getNormalAudio()
//                        }
//                    }
//
//                    else -> getNormalAudio()
//                }
                dash.dolby?.audio?.run {
                    if (isEmpty()) {
                        dash.flac?.audio ?: getNormalAudio()
                    } else {
                        first()
                    }
                } ?: run {
                    dash.flac?.audio ?: getNormalAudio()
                }
            }

            if (DBG) {
                Log.d(TAG, "play: audio quality ${audioItem?.id}")
            }
            mCurrentVideoWidth = currentQualityVideos[mCurrentSelectedIndex].width
            mCurrentVideoHeight = currentQualityVideos[mCurrentSelectedIndex].height
            val video = ProgressiveMediaSource.Factory(
                mOtherDataSourceFactory ?: mDefaultDataSourceFactory
            )
                .createMediaSource(MediaItem.fromUri(videoUrl))
            val audio = audioItem?.run {
                if (DBG) {
                    Log.d(TAG, "play: audio url ${this.baseUrl}")
                }
                ProgressiveMediaSource.Factory(
                    mOtherDataSourceFactory ?: mDefaultDataSourceFactory
                )
                    .createMediaSource(MediaItem.fromUri(this.baseUrl))
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
        mExoPlayer.play()
    }

    private fun getNormalAudio(): DashItem? {
        return mVideoURLModel?.dash?.audio?.filter { it.id in NormalAudioQuality }
            ?.maxByOrNull { it.id }
    }

    private fun resetBackSources(videoSources: List<VideoSource>? = null) {
        mBackVideoSources = videoSources
        mCurrentSelectedIndex = 0
    }

    override fun toggleLoading() {
        mExoPlayer.pause()
        _mState.update { it.copy(isLoading = true) }
    }

    override fun updateMediaState(other: MediaState) {
        _mState.update { other }
    }

    override fun switchQuality(quality: Pair<Int, String>) {
        if (quality.first == _mState.value.defaultQuality.first) {
            return
        }
        _mState.update { it.copy(defaultQuality = quality) }
        mCurrentSelectedIndex = 0
        play(_mState.value.currentDuration)
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


}
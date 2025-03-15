package com.laohei.bili_tube.presentation.player.state.media

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
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

@UnstableApi
internal class DefaultMediaManager(
    context: Context
) : MediaManager {

    private val mExoPlayer = ExoPlayer.Builder(context).build()

    private var mBackVideoSources: List<VideoSource>? = null
    private var mCurrentSelectedIndex = 0

    private val mDefaultDataSourceFactory = DefaultHttpDataSource.Factory().apply {
        setDefaultRequestProperties(
            mapOf("referer" to "https://www.bilibili.com", "User-Agent" to "K/3")
        )
    }
    private var mOtherDataSourceFactory: DataSource.Factory? = null

    private var mProgressUpdateJob: Job? = null

    private val _mState = MutableStateFlow(MediaState())

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
                    mBackVideoSources?.let { sources ->
                        if (sources.isEmpty()) {
                            return@let
                        }
                        val video = sources[mCurrentSelectedIndex]
                        val width = video.width ?: videoSize.width
                        val height = video.height ?: videoSize.height

                        if (width > 0 && height > 0) {
                            _mState.update {
                                it.copy(
                                    width = width,
                                    height = height
                                )
                            }
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    mBackVideoSources?.let { sources ->
                        if (mCurrentSelectedIndex < sources.size) {
                            mCurrentSelectedIndex++
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

    private fun resetBackSources(videoSources: List<VideoSource>? = null) {
        mBackVideoSources = videoSources
        mCurrentSelectedIndex = 0
    }

    override fun toggleLoading() {
        mExoPlayer.pause()
        _mState.update { it.copy(isLoading = true) }
    }


}
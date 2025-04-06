package com.laohei.bili_tube.presentation.player.state.media

import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import com.laohei.bili_sdk.module_v2.video.VideoURLModel
import com.laohei.bili_tube.model.VideoSource
import kotlinx.coroutines.flow.StateFlow

internal interface MediaManager {
    val state: StateFlow<MediaState>

    fun play(mediaSource: MediaSource)
    fun play(videoSources: List<VideoSource>, dataSourceFactory: DataSource.Factory? = null)
    fun play(videoURLModel: VideoURLModel, dataSourceFactory: DataSource.Factory? = null)
    fun play(path:String)
    fun seekTo(duration: Long)
    fun seekTo(position: Float)
    fun togglePlayPause()
    fun release()
    fun exoPlayer(): ExoPlayer
    fun setSpeed(speed: Float)
    fun toggleLoading()
    fun updateMediaState(other: MediaState)
    fun switchQuality(quality: Pair<Int, String>)
}
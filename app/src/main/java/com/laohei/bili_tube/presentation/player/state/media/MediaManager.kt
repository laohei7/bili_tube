package com.laohei.bili_tube.presentation.player.state.media

import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.ExoPlayer
import com.laohei.bili_sdk.module_v2.video.VideoURLModel
import kotlinx.coroutines.flow.StateFlow

internal interface MediaManager {
    val state: StateFlow<MediaState>

    fun play(videoURLModel: VideoURLModel, dataSourceFactory: DataSource.Factory? = null)
    fun play(video: String, audio: String?)
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
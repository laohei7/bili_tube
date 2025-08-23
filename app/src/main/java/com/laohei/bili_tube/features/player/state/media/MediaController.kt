package com.laohei.bili_tube.features.player.state.media

import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.ExoPlayer
import com.laohei.bili_sdk.module_v2.video.VideoURLModel
import kotlinx.coroutines.flow.StateFlow

internal interface MediaController {
    val mediaState: StateFlow<MediaState>
    val exoPlayer: ExoPlayer

    fun play(media: VideoURLModel, dataSourceFactory: DataSource.Factory? = null)
    fun play(videoUrl: String, audioUrl: String?)
    fun seekToFraction(positionMs: Long)
    fun seekToFraction(fraction: Float)
    fun togglePlayPause()
    fun release()
    fun setPlaybackSpeed(speed: Float,isUser: Boolean = true)
    fun setBuffering(enabled: Boolean)
    fun updateMediaState(state: MediaState)
    fun changeQuality(quality: Pair<Int, String>)
}
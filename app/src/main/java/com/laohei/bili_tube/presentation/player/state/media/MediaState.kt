package com.laohei.bili_tube.presentation.player.state.media

internal data class MediaState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = true,
    val isError:Boolean = false,
    val totalDuration: Long = 0L,
    val currentDuration: Long = 0L,
    val progress: Float = 0f,
    val bufferProgress: Float = 0f,
    val width: Int = 1920,
    val height: Int = 1080
)

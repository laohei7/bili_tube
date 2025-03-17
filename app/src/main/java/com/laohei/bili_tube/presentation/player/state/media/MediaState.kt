package com.laohei.bili_tube.presentation.player.state.media

internal val Quality = listOf(
    Pair(80, "1080P 高清"),
    Pair(74, "720P60 高帧率"),
    Pair(64, "720P 高清"),
    Pair(32, "480P 清晰"),
    Pair(16, "360P 流畅"),
)

internal data class MediaState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val totalDuration: Long = 0L,
    val currentDuration: Long = 0L,
    val progress: Float = 0f,
    val bufferProgress: Float = 0f,
    val width: Int = 1920,
    val height: Int = 1080,
    val speed: Float = 1.0f,
    val quality: List<Pair<Int, String>> = Quality,
    val defaultQuality : Pair<Int, String> = Quality.first()
)

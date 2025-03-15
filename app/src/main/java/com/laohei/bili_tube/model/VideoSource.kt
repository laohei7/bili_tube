package com.laohei.bili_tube.model

sealed interface SourceType {
    data object Normal : SourceType
    data object Dash : SourceType
    data object Hls : SourceType
    data object Ss : SourceType
    data object Rtsp : SourceType
}

data class VideoSource(
    val videoUrl: String,
    val audioUrl: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val sourceType: SourceType = SourceType.Normal
)
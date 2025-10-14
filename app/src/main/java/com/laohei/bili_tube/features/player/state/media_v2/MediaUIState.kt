package com.laohei.bili_tube.features.player.state.media_v2

data class MediaUIState(
    val videoWidth: Int = 1920,
    val videoHeight: Int = 1080,
    val videoAspect: Float = videoWidth.toFloat() / videoHeight,
    val isLoading: Boolean = true,
    val isCoverVisible: Boolean = true,
    val isPlaying: Boolean = false,
    val isEnd: Boolean = false,
    val duration: Long = 0L,
    val currentPosition: Long = 0L,
    val bufferedPosition: Long = 0L,
    val progress: Float = 0f,
    val bufferProgress: Float = 0f,
    val videoQuality: MediaQuality, // represents the preferred video quality by the user on the settings page.
    val audioQuality: MediaQuality, // represents the preferred audio quality by the user on the settings page.
    val sessionQuality: MediaQuality = videoQuality, // represents the selected quality by the user on the playing page.
    val activeQuality: MediaQuality = sessionQuality, // represents the actual quality currently applied to the playing video.
    val supportQualities: List<MediaQuality> = emptyList(),
    val sessionSpeed: Float = 1f,
    val activeSpeed: Float = sessionSpeed,
)

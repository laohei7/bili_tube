package com.laohei.bili_tube.features.player.state.media

import com.laohei.bili_tube.core.AudioQualities
import com.laohei.bili_tube.core.VideoQualities



internal data class MediaState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val showCover: Boolean = true,
    val totalDuration: Long = 0L,
    val currentDuration: Long = 0L,
    val progress: Float = 0f,
    val bufferProgress: Float = 0f,
    val width: Int = 1920,
    val height: Int = 1080,
    val speed: Float = 1.0f,
    val quality: List<Pair<Int, String>> = VideoQualities,
    val videoQuality: Pair<Int, String> = VideoQualities.first(),
    val audioQuality: Int = AudioQualities.first().first
) {
    fun reset(): MediaState {
        return this.copy(
            isPlaying = false,
            isLoading = true,
            isError = false,
            showCover = true,
            totalDuration = 0L,
            currentDuration = 0L,
            progress = 0f,
            bufferProgress = 0f,
            width = 1920,
            height = 1080
        )
    }
}

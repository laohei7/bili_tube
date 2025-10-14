package com.laohei.bili_tube.features.player.state.media_v2

import kotlinx.coroutines.flow.StateFlow

interface MediaController<T> {
    val isPlaying: Boolean
    val duration: Long
    val currentPosition: Long
    val bufferedPosition: Long
    val player: T

    val uiState: StateFlow<MediaUIState>

    suspend fun load(source: MediaSource)
    suspend fun load(sources: List<MediaSource>)
    fun play()
    fun pause()
    fun stop()
    fun release()

    fun seekTo(positionMs: Long)
    fun setVolume(volume: Float)
    fun setLooping(loop: Boolean)
    fun setSpeed(speed: Float)
    fun setSessionSpeed(speed: Float)

    fun setSupportQualities(qualities: List<MediaQuality>)
    fun setQuality(quality: MediaQuality)
    fun setOnPlaybackEndListener(listener: OnPlaybackEndListener)

    interface OnPlaybackEndListener {
        fun onPlaybackEnded()
    }
}

data class MediaSource(
    val video: String?,
    val audio: String?,
    val headers: Map<String, String> = emptyMap(),
    val startPositionMs: Long = 0L,
    val videoQuality: MediaQuality? = null,
    val audioQuality: MediaQuality? = null
)

data class MediaQuality(
    val id: Int,
    val label: String,
)

data class SkipSegment(
    val start: Long,
    val end: Long,
)

enum class SkipType {
    INTRO,   // 片头
    OUTRO,   // 片尾
    AD,      // 广告
    CHAPTER, // 章节跳过
    CUSTOM   // 自定义跳过部分（可以用于其他场景，如插播广告等）
}
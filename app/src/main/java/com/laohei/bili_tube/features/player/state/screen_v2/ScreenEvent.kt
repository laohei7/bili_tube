package com.laohei.bili_tube.features.player.state.screen_v2

import android.graphics.Bitmap

sealed interface ScreenEvent {
    data class SetBackgroundImage(val image: Bitmap?) : ScreenEvent

    data class HintVisibility(val isVisible: Boolean) : ScreenEvent

    data class PlaylistVisibility(val isVisible: Boolean) : ScreenEvent
    data class FolderCreationVisibility(val isVisible: Boolean) : ScreenEvent
    data class ControlVisibility(val isVisible: Boolean) : ScreenEvent
    data class LikeAnimationVisibility(val isVisible: Boolean) : ScreenEvent
    data class ReplyVisibility(val isVisible: Boolean) : ScreenEvent
    data class AddCoinVisibility(val isVisible: Boolean) : ScreenEvent
    data class FolderModificationVisibility(val isVisible: Boolean) : ScreenEvent
    data class DownloadVisibility(val isVisible: Boolean) : ScreenEvent
    data class VideoDetailVisibility(val isVisible: Boolean) : ScreenEvent
    data class UpInfoVisibility(val isVisible: Boolean) : ScreenEvent
    data class ArchiveVisibility(val isVisible: Boolean) : ScreenEvent
    data class WatchLaterVisibility(val isVisible: Boolean) : ScreenEvent
    data class FolderMediaVisibility(val isVisible: Boolean) : ScreenEvent
    data class SettingsVisibility(val isVisible: Boolean) : ScreenEvent
    data class SpeedSettingsVisibility(val isVisible: Boolean) : ScreenEvent
    data class QualitySettingsVisibility(val isVisible: Boolean) : ScreenEvent
    data class OtherSettingsVisibility(val isVisible: Boolean) : ScreenEvent
    data class VideoMenuVisibility(val isVisible: Boolean) : ScreenEvent
    data class SetUserFullscreenToggle(val flag: Boolean) : ScreenEvent
    data object ResetScrollPosition : ScreenEvent
}
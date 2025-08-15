package com.laohei.bili_tube.features.player.state.screen

import android.graphics.Bitmap

sealed interface ScreenAction {

    data object ToUserSpaceAction : ScreenAction
    data object SubscribeAction : ScreenAction
    data object ShowRelatedAction : ScreenAction


    data class LockScreenAction(val flag: Boolean) : ScreenAction


    data class ShowPlaylistSheetAction(val flag: Boolean) : ScreenAction
    data class CreatedFolderUIAction(val flag: Boolean) : ScreenAction
    data class ShowControlUIAction(val flag: Boolean) : ScreenAction
    data class SetBackgroundAction(val bitmap: Bitmap?) : ScreenAction
    data class ShowLikeAnimationAction(val flag: Boolean) : ScreenAction
    data class ReplyUIAction(val flag: Boolean) : ScreenAction
    data class AddCoinUIAction(val flag: Boolean) : ScreenAction
    data class ModifyFolderUIAction(val flag: Boolean) : ScreenAction
    data class DownloadUIAction(val flag: Boolean) : ScreenAction
    data class VideoDetailUIAction(val flag: Boolean) : ScreenAction
    data class UpInfoUIAction(val flag: Boolean) : ScreenAction
    data class ArchiveUIAction(val flag: Boolean) : ScreenAction
    data class SettingUIAction(val flag: Boolean) : ScreenAction
    data class SettingSpeedUIAction(val flag: Boolean) : ScreenAction
    data class SettingQualityUIAction(val flag: Boolean) : ScreenAction
    data class OtherSettingUIAction(val flag: Boolean) : ScreenAction
    data class VideoMenuUIAction(val flag: Boolean) : ScreenAction

}
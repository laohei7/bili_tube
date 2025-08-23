package com.laohei.bili_tube.features.player.state.screen

import android.graphics.Bitmap

sealed interface ScreenAction {

    data object NavigateToUserSpace : ScreenAction
    data object Subscribe : ScreenAction
    data object ShowRelated : ScreenAction


    data class SetLockScreen(val flag: Boolean) : ScreenAction


    data class SetPlaylistVisible(val flag: Boolean) : ScreenAction
    data class SetCreatedFolderVisible(val flag: Boolean) : ScreenAction
    data class SetControlVisible(val flag: Boolean) : ScreenAction
    data class SetBackground(val bitmap: Bitmap?) : ScreenAction
    data class SetLikeAnimationVisible(val flag: Boolean) : ScreenAction
    data class SetReplyVisible(val flag: Boolean) : ScreenAction
    data class SetAddCoinVisible(val flag: Boolean) : ScreenAction
    data class SetModifyFolderVisible(val flag: Boolean) : ScreenAction
    data class SetDownloadVisible(val flag: Boolean) : ScreenAction
    data class SetVideoDetailVisible(val flag: Boolean) : ScreenAction
    data class SetUpInfoVisible(val flag: Boolean) : ScreenAction
    data class SetArchiveVisible(val flag: Boolean) : ScreenAction
    data class SetSettingVisible(val flag: Boolean) : ScreenAction
    data class SetSettingSpeedVisible(val flag: Boolean) : ScreenAction
    data class SetSettingQualityVisible(val flag: Boolean) : ScreenAction
    data class SetOtherSettingVisible(val flag: Boolean) : ScreenAction
    data class SetVideoMenuVisible(val flag: Boolean) : ScreenAction

    data class SetUserSwitch(val flag: Boolean) : ScreenAction
    data class SetHintVisible(val flag: Boolean) : ScreenAction

}
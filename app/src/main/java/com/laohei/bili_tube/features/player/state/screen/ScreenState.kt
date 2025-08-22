package com.laohei.bili_tube.features.player.state.screen

import android.graphics.Bitmap
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class ScreenState(
    val screenWidth: Int,
    val screenHeight: Int,
    val isFullscreen: Boolean = false,
    val maximumHeight: Dp = 400.dp,
    val minimumHeight: Dp = 200.dp,
    val videoHeight: Dp = 200.dp,
    val originalVideoHeight: Dp = videoHeight,
    val listState: LazyListState = LazyListState(),
    val basicDelta: Float = 0f,
    val minBound: Float = 300f,
    val maxBound: Float = 0f,
    val isDrag: Boolean = false,
    val maskAlpha: Float = 0f,
    val isShowRelatedList: Boolean = false,
    val relatedListOffset: Float = 500f,
    val isShowVideoSettingsSheet: Boolean = false,

    val isShowDownloadSheet: Boolean = false,

    val archiveListState: LazyListState = LazyListState(),
    val isLockScreen: Boolean = false,
    val isShowAddCoinUI: Boolean = false,
    val isShowFolderSheet: Boolean = false,
    val isShowLikeAnimation: Boolean = false,
    val isShowUpInfoSheet: Boolean = false,
    val isShowPlaylistSheet: Boolean = false,
    val isShowAddFolder: Boolean = false,

    // background image
    val background: Bitmap? = null,

    // ui control
    val isShowControlUI: Boolean = false,
    val isShowVideoDetailUI: Boolean = false,
    val isShowReplyUI: Boolean = false,
    val isShowArchiveUI: Boolean = false,
    val isShowSpeedUI: Boolean = false,
    val isShowQualityUI: Boolean = false,
    val isShowOtherSettingUI: Boolean = false,
    val isShowVideoMenuUIAction: Boolean = false,

    // adaption system rotation
    val isAutoRotateEnabled: Boolean = false,
    val isUserSwitch: Boolean = false
)
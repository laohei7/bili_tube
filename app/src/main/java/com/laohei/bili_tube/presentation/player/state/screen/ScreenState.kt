package com.laohei.bili_tube.presentation.player.state.screen

import android.graphics.Bitmap
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class ScreenState(
    val screenWidth: Int,
    val screenHeight: Int,
    val isShowUI: Boolean = false,
    val isFullscreen: Boolean = false,
    val maxLimitedHeight: Dp = 400.dp,
    val minLimitedHeight: Dp = 200.dp,
    val videoHeight: Dp = 200.dp,
    val originalVideoHeight: Dp = videoHeight,
    val bitmap: Bitmap? = null,
    val listState: LazyListState = LazyListState(),
    val basicDelta: Float = 0f,
    val minBound: Float = 300f,
    val maxBound: Float = 0f,
    val isDrag: Boolean = false,
    val isShowReplySheet: Boolean = false,
    val isShowDetailSheet: Boolean = false,
    val maskAlpha: Float = 0f,
    val isShowRelatedList: Boolean = false,
    val relatedListOffset: Float = 500f,
    val isShowVideoSettingsSheet: Boolean = false,
    val isShowSpeedSheet: Boolean = false,
    val isShowQualitySheet: Boolean = false,
    val isShowDownloadSheet: Boolean = false,
    val isShowOtherSettingsSheet: Boolean = false,
    val isShowArchiveSheet: Boolean = false,
    val archiveListState:LazyListState = LazyListState(),
    val isLockScreen: Boolean = false,
    val isShowCoinSheet:Boolean = false,
    val isShowFolderSheet:Boolean = false,
    val isShowLikeAnimation: Boolean = false,
)
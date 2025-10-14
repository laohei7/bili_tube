package com.laohei.bili_tube.features.player.state.screen_v2

import android.graphics.Bitmap
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ScreenState(
    val screenWidth: Int,
    val screenHeight: Int,
    val isFullScreenActive: Boolean = false,
    val maxHeight: Dp = 400.dp,
    val minHeight: Dp = 200.dp,
    val currentVideoHeight: Dp = 200.dp,
    val initialVideoHeight: Dp = currentVideoHeight,
    val dragDelta: Float = 0f,
    val minLimit: Float = 300f,
    val maxLimit: Float = 0f,
    val isDragging: Boolean = false,
    val maskOpacity: Float = 0f,
    val contentScrollState: LazyListState = LazyListState(),

    val backgroundImage: Bitmap? = null,

    val showRelatedList: Boolean = false,
    val relatedListOffsetY: Float = 500f,

    val showVideoSettingsSheet: Boolean = false,
    val showDownloadSheet: Boolean = false,

    val archiveListScrollState: LazyListState = LazyListState(),
    val isScreenLocked: Boolean = false,
    val showAddCoinUI: Boolean = false,
    val showFolderSheet: Boolean = false,
    val showLikeAnimation: Boolean = false,
    val showUpInfoSheet: Boolean = false,
    val showPlaylistSheet: Boolean = false,
    val showAddFolder: Boolean = false,

    val showControlUI: Boolean = false,
    val showVideoDetailUI: Boolean = false,
    val showReplyUI: Boolean = false,
    val showArchiveUI: Boolean = false,
    val showSpeedUI: Boolean = false,
    val showQualityUI: Boolean = false,
    val showOtherSettingUI: Boolean = false,
    val showVideoMenuUIAction: Boolean = false,
    val isHintVisible: Boolean = false,
    val isWatchLaterVisible: Boolean = false,
    val isFolderMediaVisible: Boolean = false,
    val watchLaterListState: LazyListState = LazyListState(),
    val folderMediaListState: LazyListState = LazyListState(),

    val isAutoRotationEnabled: Boolean = false,
    val isUserFullscreenToggle: Boolean = false
)

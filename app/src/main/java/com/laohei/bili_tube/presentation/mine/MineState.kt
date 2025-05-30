package com.laohei.bili_tube.presentation.mine

import com.laohei.bili_sdk.module_v2.folder.FolderItem
import com.laohei.bili_sdk.module_v2.history.HistoryItem
import com.laohei.bili_sdk.module_v2.video.VideoView

data class MineState(
    val watchLaterList: List<VideoView> = emptyList(),
    val watchLaterCount: Int = 0,
    val historyList: List<HistoryItem> = emptyList(),
    val folderList: List<FolderItem> = emptyList(),
    val following: Int = 0,
    val follower: Int = 0,
    val dynamicCount: Int = 0,
    val isRefreshing: Boolean = false
)

package com.laohei.bili_tube.features.main.profile

import com.laohei.bili_sdk.model_v2.folder.FolderItem
import com.laohei.bili_sdk.model_v2.history.HistoryItem
import com.laohei.bili_sdk.model_v2.video.VideoView

data class ProfileUIState(
    val watchlist: List<VideoView> = emptyList(),
    val watchLaterCount: Int = 0,
    val historyList: List<HistoryItem> = emptyList(),
    val folderList: List<FolderItem> = emptyList(),
    val following: Int = 0,
    val follower: Int = 0,
    val dynamicCount: Int = 0,
    val isRefreshing: Boolean = false,
    val isShowAddFolder: Boolean = false,
    val folderName: String = "",
    val isPrivateFolder: Boolean = false,
    val isHistoryOptionsVisible: Boolean = false
)

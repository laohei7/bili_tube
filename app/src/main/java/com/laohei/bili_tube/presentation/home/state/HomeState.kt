package com.laohei.bili_tube.presentation.home.state

import com.laohei.bili_sdk.module_v2.folder.FolderSimpleItem

data class HomeState(
    val folders: List<FolderSimpleItem> = emptyList(),
    val currentAid: Long? = null,
    val currentBvid:String? = null,
    val isShowMenuSheet: Boolean = false,
    val isShowFolderSheet: Boolean = false,
)

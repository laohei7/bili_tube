package com.laohei.bili_tube.presentation.playlist

import com.laohei.bili_sdk.module_v2.folder.FolderModel

data class PlaylistState(
    val watchLaterCover: String = "",
    val folderList: List<FolderModel> = emptyList(),
    val isLoading: Boolean = true
)
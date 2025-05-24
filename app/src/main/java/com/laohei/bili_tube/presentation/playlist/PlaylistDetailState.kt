package com.laohei.bili_tube.presentation.playlist

import androidx.paging.PagingData
import com.laohei.bili_sdk.module_v2.folder.FolderMediaItem
import com.laohei.bili_sdk.module_v2.video.VideoView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class PlaylistDetailState(
    val toViewList: List<VideoView> = emptyList(),
    val folderResources: Flow<PagingData<FolderMediaItem>> = flow { PagingData.empty<FolderMediaItem>() },
)

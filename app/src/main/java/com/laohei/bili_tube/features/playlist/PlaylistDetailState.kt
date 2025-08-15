package com.laohei.bili_tube.features.playlist

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.paging.PagingData
import com.laohei.bili_sdk.module_v2.folder.FolderMediaItem
import com.laohei.bili_sdk.module_v2.video.VideoView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class PlaylistDetailState(
    val toViewList: List<VideoView> = emptyList(),
    val folderResources: Flow<PagingData<FolderMediaItem>> = flow { PagingData.empty<FolderMediaItem>() },
    val folderState: LazyGridState = LazyGridState(),
    val toViewState: LazyListState = LazyListState()
)

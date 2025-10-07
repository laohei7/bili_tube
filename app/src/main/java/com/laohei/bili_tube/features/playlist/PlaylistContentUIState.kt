package com.laohei.bili_tube.features.playlist

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.paging.PagingData
import com.laohei.bili_sdk.model_v2.folder.MediaItem
import com.laohei.bili_sdk.model_v2.video.VideoView
import com.laohei.bili_tube.nav.AppRoute
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class PlaylistContentUIState(
    val param: AppRoute.PlaylistContent,
    val watchLaterList: List<VideoView> = emptyList(),
    val folderMediaFlow: Flow<PagingData<MediaItem>> = flow { PagingData.empty<MediaItem>() },
    val folderMediaListState: LazyGridState = LazyGridState(),
    val watchLaterListState: LazyGridState = LazyGridState(),
    val isShareLinkVisible: Boolean = false,
    val shareLink: String = ""
)

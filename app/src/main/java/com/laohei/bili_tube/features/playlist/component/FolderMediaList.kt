package com.laohei.bili_tube.features.playlist.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.laohei.bili_sdk.module_v2.folder.FolderMediaItem
import com.laohei.bili_tube.model.play.PlayParam
import com.laohei.bili_tube.ui.viewmodel.SharedViewModel
import com.laohei.bili_tube.model.FolderMedia
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.component.state.LoadingStatePlaceholder
import com.laohei.bili_tube.ui.component.video.HorizontalVideoItem
import com.laohei.bili_tube.ui.theme.PaddingLg
import com.laohei.bili_tube.ui.theme.PaddingMd
import com.laohei.bili_tube.ui.util.HorizontalItemRules
import com.laohei.bili_tube.ui.util.rememberGridColumnCount
import com.laohei.bili_tube.util.toTimeString
import com.laohei.bili_tube.util.toTimeAgoString
import com.laohei.bili_tube.util.toViewString
import org.koin.compose.koinInject


@Composable
internal fun FolderMediaList(
    modifier: Modifier = Modifier,
    gridState: LazyGridState,
    param: AppRoute.PlaylistContent,
    resources: LazyPagingItems<FolderMediaItem>,
    navigateToAppRoute: (AppRoute) -> Unit
) {
    val sharedViewModel = koinInject<SharedViewModel>()
    val mediaKeys = remember(resources.itemCount) {
        List(resources.itemCount) { index ->
            resources[index]?.let { FolderMedia(it.title, it.id, it.bvid, -1L) }
        }.filterNotNull()
    }
    AdaptiveLayout { uiType, width, height ->
        val fixedCount = rememberGridColumnCount(width, HorizontalItemRules)
        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Fixed(fixedCount),
            state = gridState,
            horizontalArrangement = Arrangement.spacedBy(PaddingMd),
            verticalArrangement = Arrangement.spacedBy(PaddingLg)
        ) {
            item(key = "header_info", span = { GridItemSpan(fixedCount) }) {
                PlaylistInfoCard(param = param)
            }
            items(resources.itemCount, { resources[it]!!.bvid }) { index ->
                val item = resources[index] ?: return@items
                HorizontalVideoItem(
                    cover = item.cover,
                    title = item.title,
                    ownerName = item.upper.name,
                    duration = item.duration.toTimeString(false),
                    view = item.cntInfo.play.toViewString(),
                    publishDate = item.pubtime.toTimeAgoString(),
                    leadingIcon = null,
                    onClick = {
                        sharedViewModel.setPlayParam(
                            PlayParam.MediaList(
                                medias = mediaKeys,
                                aid = item.id,
                                bvid = item.bvid,
                                cid = -1,
                                title = param.title,
                                count = param.count,
                                isToView = false,
                                fid = param.fid
                            )
                        )
                        navigateToAppRoute(AppRoute.Play)
                    }
                )
            }
            item(
                key = "no_more_data",
                span = { GridItemSpan(fixedCount) }) { LoadingStatePlaceholder(resources.loadState.append) }
        }
    }
}
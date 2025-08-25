package com.laohei.bili_tube.features.playlist.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastMap
import com.laohei.bili_sdk.module_v2.video.VideoView
import com.laohei.bili_tube.PlayParam
import com.laohei.bili_tube.SharedViewModel
import com.laohei.bili_tube.model.FolderMedia
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.component.video.HorizontalVideoItem
import com.laohei.bili_tube.ui.theme.LargePadding
import com.laohei.bili_tube.ui.theme.MediumPadding
import com.laohei.bili_tube.ui.util.HorizontalItemRules
import com.laohei.bili_tube.ui.util.rememberGridColumnCount
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.toTimeAgoString
import com.laohei.bili_tube.utill.toViewString
import org.koin.compose.koinInject


@Composable
internal fun WatchLaterList(
    modifier: Modifier = Modifier,
    param: AppRoute.PlaylistContent,
    gridState: LazyGridState,
    watchLaterList: List<VideoView>,
    navigateToAppRoute: (AppRoute) -> Unit
) {
    val sharedViewModel = koinInject<SharedViewModel>()
    val mediaKeys = remember(watchLaterList) {
        watchLaterList.fastMap { FolderMedia(it.title, it.aid, it.bvid, it.cid) }
    }
    AdaptiveLayout { uiType, width, height ->
        val fixedCount = rememberGridColumnCount(width, HorizontalItemRules)
        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Fixed(fixedCount),
            state = gridState,
            horizontalArrangement = Arrangement.spacedBy(MediumPadding),
            verticalArrangement = Arrangement.spacedBy(LargePadding)
        ) {
            item(key = "header_info", span = { GridItemSpan(fixedCount) }) {
                PlaylistInfoCard(param = param)
            }
            items(watchLaterList, { it.bvid }) { item ->
                HorizontalVideoItem(
                    cover = item.pic,
                    title = item.title,
                    ownerName = item.owner.name,
                    duration = item.duration.formatTimeString(false),
                    view = item.stat.view.toViewString(),
                    publishDate = item.pubdate.toTimeAgoString(),
                    leadingIcon = null,
                    onClick = {
                        sharedViewModel.setPlayParam(
                            PlayParam.MediaList(
                                medias = mediaKeys,
                                aid = item.aid,
                                bvid = item.bvid,
                                cid = item.cid,
                                title = param.title,
                                count = param.count
                            )
                        )
                        navigateToAppRoute(AppRoute.Play)
                    }
                )
            }
        }
    }
}

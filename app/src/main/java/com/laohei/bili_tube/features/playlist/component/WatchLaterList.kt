package com.laohei.bili_tube.features.playlist.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastMap
import com.laohei.bili_sdk.model_v2.video.VideoView
import com.laohei.bili_tube.features.playlist.PlaylistContentAction
import com.laohei.bili_tube.model.FolderMedia
import com.laohei.bili_tube.model.play.PlayParam
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.component.video.HorizontalVideoCard
import com.laohei.bili_tube.ui.theme.PaddingLg
import com.laohei.bili_tube.ui.theme.PaddingMd
import com.laohei.bili_tube.ui.util.HorizontalItemRules
import com.laohei.bili_tube.ui.util.rememberGridColumnCount
import com.laohei.bili_tube.ui.viewmodel.SharedViewModel
import com.laohei.bili_tube.util.toTimeAgoString
import com.laohei.bili_tube.util.toTimeString
import com.laohei.bili_tube.util.toViewString
import com.laohei.compose.uicore.ActionButtonListItem
import org.koin.compose.koinInject


@Composable
internal fun WatchLaterList(
    modifier: Modifier = Modifier,
    param: AppRoute.PlaylistContent,
    gridState: LazyGridState,
    watchLaterList: List<VideoView>,
    navigateToAppRoute: (AppRoute) -> Unit,
    onPlaylistContentAction: (PlaylistContentAction) -> Unit
) {
    val sharedViewModel = koinInject<SharedViewModel>()
    val mediaKeys = remember(watchLaterList) {
        watchLaterList.fastMap { FolderMedia(it.title, it.aid, it.bvid, it.cid) }
    }
    var isOpenBvid by remember { mutableStateOf<String?>(null) }

    fun resetOpenBvid() {
        isOpenBvid = null
    }

    LaunchedEffect(gridState.isScrollInProgress) {
        if (isOpenBvid != null) {
            resetOpenBvid()
        }
    }

    AdaptiveLayout { uiType, width, height ->
        val fixedCount = rememberGridColumnCount(width, HorizontalItemRules)
        LazyVerticalGrid(
            modifier = modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    if (isOpenBvid != null) {
                        resetOpenBvid()
                    }
                },
            columns = GridCells.Fixed(fixedCount),
            state = gridState,
            horizontalArrangement = Arrangement.spacedBy(PaddingMd),
            verticalArrangement = Arrangement.spacedBy(PaddingLg)
        ) {
            item(key = "header_info", span = { GridItemSpan(fixedCount) }) {
                PlaylistInfoCard(param = param)
            }
            items(watchLaterList, { it.bvid }) { item ->
                var isOpen by remember { mutableStateOf(false) }
                ActionButtonListItem(
                    isOpen = isOpenBvid == item.bvid && isOpen,
                    actionVerticalSpace = PaddingLg * 2,
                    actionHorizontalSpace = PaddingMd,
                    onOpenChange = {
                        isOpenBvid = if (it) item.bvid else null
                        isOpen = it
                    },
                    onClick = {
                        if (isOpenBvid != null) {
                            resetOpenBvid()
                        } else {
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
                    }
                ) {
                    HorizontalVideoCard(
                        coverUrl = item.pic,
                        title = item.title,
                        ownerName = item.owner.name,
                        duration = item.duration.toTimeString(false),
                        viewCount = item.stat.view.toViewString(),
                        publishDate = item.pubdate.toTimeAgoString(false),
                    )
                    FilledIconButton(
                        modifier = Modifier.aspectRatio(1f),
                        onClick = {
                            onPlaylistContentAction(
                                PlaylistContentAction.DelToView(aid = item.aid)
                            )
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = Icons.Rounded.Delete.name
                        )
                    }
                    FilledIconButton(
                        modifier = Modifier.aspectRatio(1f),
                        onClick = {
                            onPlaylistContentAction(
                                PlaylistContentAction.ShareLink(
                                    true,
                                    "【${item.owner.name}】${item.title} ${item.shortLinkV2}"
                                )
                            )
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.IosShare,
                            contentDescription = Icons.Rounded.IosShare.name
                        )
                    }
                }
            }
        }
    }
}

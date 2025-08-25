package com.laohei.bili_tube.features.search.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import androidx.paging.compose.LazyPagingItems
import com.laohei.bili_sdk.module_v2.search.SearchResultItemType
import com.laohei.bili_tube.PlayParam
import com.laohei.bili_tube.R
import com.laohei.bili_tube.SharedViewModel
import com.laohei.bili_tube.model.UIModel
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.component.layout.DeviceConfiguration
import com.laohei.bili_tube.ui.component.placeholder.NoMoreData
import com.laohei.bili_tube.ui.component.video.HorizontalVideoItem
import com.laohei.bili_tube.ui.theme.MediumPadding
import com.laohei.bili_tube.ui.theme.Pink
import com.laohei.bili_tube.utill.completeUrl
import com.laohei.bili_tube.utill.formatDateToYearString
import com.laohei.bili_tube.utill.toTimeAgoString
import com.laohei.bili_tube.utill.toViewString
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchResultList(
    paddingValues: PaddingValues,
    list: LazyPagingItems<UIModel<out Any?>>,
    showHeader: Boolean,
    navigateToAppRoute: (AppRoute) -> Unit,
    scrollToPage: (Int) -> Unit,
) {
    AdaptiveLayout { uiType, _, _ ->
        val fixedCount = when (uiType) {
            DeviceConfiguration.MOBILE_PORTRAIT -> 1
            DeviceConfiguration.MOBILE_LANDSCAPE,
            DeviceConfiguration.TABLE_PORTRAIT,
            DeviceConfiguration.TABLE_LANDSCAPE,
            DeviceConfiguration.DESKTOP -> {
                when {
                    maxWidth >= 500.dp && maxWidth < 1280.dp -> 2
                    else -> 3
                }
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(fixedCount),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(MediumPadding),
            horizontalArrangement = Arrangement.spacedBy(MediumPadding)
        ) {
            items(
                count = list.itemCount,
                span = { index ->
                    when (list[index]) {
                        is UIModel.Header<*> -> {
                            GridItemSpan(fixedCount)
                        }

                        else -> {
                            GridItemSpan(1)
                        }
                    }
                }
            ) { index ->
                list[index]?.let { item ->
                    when (item) {
                        is UIModel.Header<*> -> {
                            if (showHeader.not()) {
                                return@let
                            }
                            GetHeader(
                                type = item.header,
                                onClick = { scrollToPage(it) }
                            )
                        }

                        is UIModel.Item<*> -> {
                            item.item?.let {
                                GetSearchItem(
                                    item = it as SearchResultItemType,
                                    navigateToAppRoute = navigateToAppRoute
                                )
                            }
                        }
                    }
                }
            }
            item(span = { GridItemSpan(fixedCount) }) { NoMoreData(list.loadState.append) }
            item(span = { GridItemSpan(fixedCount) }) {
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }
}


@Composable
private fun GetHeader(
    type: Any?,
    onClick: (Int) -> Unit
) {
    val title = when (type) {
        SearchResultItemType.TYPE_VIDEO -> {
            Pair(R.string.str_video, R.drawable.bili_emoji1)
        }

        SearchResultItemType.TYPE_MEDIA_FT -> {
            Pair(R.string.str_moive, R.drawable.bili_emoji4)
        }

        SearchResultItemType.TYPE_MEDIA_BANGUMI -> {
            Pair(
                R.string.str_bangumi,
                R.drawable.bili_emoji5
            )
        }

        else -> null
    }
    title?.let {
        ListItem(
            leadingContent = {
                Image(
                    painter = painterResource(it.second),
                    contentDescription = "icon",
                    modifier = Modifier.size(36.dp)
                )
            },
            headlineContent = {
                Text(
                    text = stringResource(it.first),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Pink
                )
            },
            trailingContent = {
                TextButton(onClick = { onClick.invoke(it.first) }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.str_see_more),
                            style = MaterialTheme.typography.labelMedium
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                            contentDescription = Icons.AutoMirrored.Outlined.KeyboardArrowRight.name,
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun GetSearchItem(
    item: SearchResultItemType,
    navigateToAppRoute: (AppRoute) -> Unit
) {
    val sharedViewModel = koinInject<SharedViewModel>()
    when (item) {
        is SearchResultItemType.MediaBangumiItem -> {
            BangumiItem(
                title = item.title,
                cover = item.cover,
                areas = item.areas,
                date = item.pubTime.formatDateToYearString(false),
                styles = item.styles,
                score = item.mediaScore.score,
                userCount = item.mediaScore.userCount.toViewString(),
                episodes = item.eps?.fastMap { it.title },
                onClick = {
                    sharedViewModel.setPlayParam(
                        PlayParam.BangumiParam(
                            mediaId = item.mediaId,
                            seasonId = item.seasonId,
                            epId = item.eps?.first()?.id,
                            aid = -1, cid = -1, bvid = ""
                        )
                    )
                    navigateToAppRoute(AppRoute.Play)
                }
            )
        }

        is SearchResultItemType.MediaFTItem -> {
            BangumiItem(
                title = item.title,
                cover = item.cover,
                areas = item.areas,
                date = item.pubTime.formatDateToYearString(false),
                styles = item.styles,
                score = item.mediaScore.score,
                userCount = item.mediaScore.userCount.toViewString(),
                episodes = item.eps?.fastMap { it.title },
                onClick = {
                    sharedViewModel.setPlayParam(
                        PlayParam.BangumiParam(
                            mediaId = item.mediaId,
                            seasonId = item.seasonId,
                            epId = item.eps?.first()?.id,
                            aid = -1, cid = -1, bvid = ""
                        )
                    )
                    navigateToAppRoute(AppRoute.Play)
                }
            )
        }

        is SearchResultItemType.VideoItem -> {
            HorizontalVideoItem(
                cover = item.pic.completeUrl(),
                title = item.title,
                ownerName = item.author,
                rcmdReason = "",
                duration = item.duration,
                view = item.play.toViewString(),
                publishDate = item.pubDate.toTimeAgoString(),
                onClick = {
                    sharedViewModel.setPlayParam(
                        PlayParam.VideoParam(
                            aid = item.aid,
                            bvid = item.bvid,
                            cid = -1L,
                        )
                    )
                    navigateToAppRoute(AppRoute.Play)
                },
                trailingOnClick = {

                },
                leadingIcon = null
            )
        }

        SearchResultItemType.UnknownItem -> {}
    }
}
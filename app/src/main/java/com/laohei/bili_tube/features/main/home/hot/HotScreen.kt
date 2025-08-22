package com.laohei.bili_tube.features.main.home.hot

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.laohei.bili_sdk.module_v2.hot.HotItem
import com.laohei.bili_tube.PlayParam
import com.laohei.bili_tube.SharedViewModel
import com.laohei.bili_tube.ui.component.placeholder.NoMoreData
import com.laohei.bili_tube.features.main.home.HomeAction
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.component.layout.DeviceConfiguration
import com.laohei.bili_tube.ui.component.video.HorizontalVideoItem
import com.laohei.bili_tube.ui.theme.LargePadding
import com.laohei.bili_tube.ui.theme.MediumPadding
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.toTimeAgoString
import com.laohei.bili_tube.utill.toViewString
import org.koin.compose.koinInject

private const val TAG = "HotScreen"

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotScreen(
    hotVideos: LazyPagingItems<HotItem>,
    gridState: LazyGridState = rememberLazyGridState(),
    navigateToAppRoute: (AppRoute) -> Unit,
    onHomeAction: (HomeAction) -> Unit,
) {
    val sharedViewModel = koinInject<SharedViewModel>()
    val refreshState = rememberPullToRefreshState()

    AdaptiveLayout { uiType, width, height ->
        val fixedCount = when (uiType) {
            DeviceConfiguration.MOBILE_PORTRAIT -> 1
            DeviceConfiguration.MOBILE_LANDSCAPE,
            DeviceConfiguration.TABLE_PORTRAIT,
            DeviceConfiguration.TABLE_LANDSCAPE,
            DeviceConfiguration.DESKTOP -> {
                when {
                    maxWidth >= 500.dp && maxWidth < 1280.dp -> 2
                    else -> 4
                }
            }
        }
        PullToRefreshBox(
            isRefreshing = hotVideos.loadState.refresh is LoadState.Loading,
            state = refreshState,
            onRefresh = { hotVideos.refresh() },
            indicator = {
                Indicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .offset(y = 50.dp),
                    isRefreshing = hotVideos.loadState.refresh is LoadState.Loading,
                    state = refreshState,
                )
            }
        ) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(fixedCount),
                contentPadding = PaddingValues(horizontal = if (fixedCount == 1) 0.dp else 8.dp),
                horizontalArrangement = Arrangement.spacedBy(MediumPadding),
                verticalArrangement = Arrangement.spacedBy(LargePadding),
            ) {
                item(span = { GridItemSpan(fixedCount) }, key = "Hots-top-padding") {
                    when (uiType) {
                        DeviceConfiguration.MOBILE_PORTRAIT,
                        DeviceConfiguration.TABLE_PORTRAIT -> {
                            Spacer(Modifier
                                .statusBarsPadding()
                                .height(72.dp))
                        }

                        DeviceConfiguration.MOBILE_LANDSCAPE,
                        DeviceConfiguration.TABLE_LANDSCAPE,
                        DeviceConfiguration.DESKTOP -> {
                            Spacer(Modifier
                                .height(42.dp))
                        }
                    }
                }

                items(hotVideos.itemCount) { index ->
                    hotVideos[index]?.let {
                        HorizontalVideoItem(
                            cover = it.pic,
                            title = it.title,
                            ownerName = it.owner.name,
                            rcmdReason = it.rcmdReason.content ?: "",
                            duration = it.duration.formatTimeString(false),
                            view = it.stat.view.toViewString(),
                            publishDate = it.pubdate.toTimeAgoString(),
                            onClick = {
                                sharedViewModel.setPlayParam(
                                    PlayParam.VideoParam(
                                        aid = it.aid,
                                        bvid = it.bvid,
                                        cid = it.cid,
                                        width = it.dimension.width,
                                        height = it.dimension.height
                                    )
                                )
                                navigateToAppRoute(AppRoute.Play)
                            },
                            trailingOnClick = {
                                onHomeAction(HomeAction.MenuSheetUIAction(true, it.aid, it.bvid))
                            },
                            leadingIcon = null
                        )
                    }
                }
                item(span = { GridItemSpan(fixedCount) }) {
                    NoMoreData(hotVideos.loadState.append)
                }
                item(span = { GridItemSpan(fixedCount) }) {
                    Spacer(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .height(80.dp)
                    )
                }
            }
        }
    }
}
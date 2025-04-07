package com.laohei.bili_tube.presentation.home.hot

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import com.laohei.bili_sdk.model.BiliHotVideoItem
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.component.placeholder.NoMoreData
import com.laohei.bili_tube.component.video.HorizontalVideoItem
import com.laohei.bili_tube.presentation.home.state.HomePageAction
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.toTimeAgoString
import com.laohei.bili_tube.utill.toViewString

private const val TAG = "HotScreen"

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotScreen(
    hotVideos: LazyPagingItems<BiliHotVideoItem>,
    gridState: LazyGridState = rememberLazyGridState(),
    navigateToRoute: (Route) -> Unit,
    homeActionHandle: (HomePageAction) -> Unit,
) {
    val refreshState = rememberPullToRefreshState()

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val fixedCount = when {
            maxWidth < 500.dp -> 1
            maxWidth >= 500.dp && maxWidth < 1280.dp -> 2
            else -> 4
        }
        PullToRefreshBox(
            isRefreshing = hotVideos.loadState.refresh is LoadState.Loading,
            state = refreshState,
            onRefresh = { hotVideos.refresh() },
            indicator = {
                Indicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding(),
                    isRefreshing = hotVideos.loadState.refresh is LoadState.Loading,
                    state = refreshState,
                )
            }
        ) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(fixedCount),
                contentPadding = PaddingValues(horizontal = if (fixedCount == 1) 0.dp else 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(span = { GridItemSpan(fixedCount) }) {
                    Spacer(
                        modifier = Modifier
                            .statusBarsPadding()
                            .height(40.dp)
                    )
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
                                navigateToRoute(
                                    Route.Play(
                                        aid = it.aid,
                                        bvid = it.bvid,
                                        cid = it.cid,
                                        width = it.dimension.width,
                                        height = it.dimension.height
                                    )
                                )
                            },
                            trailingOnClick = {
                                homeActionHandle.invoke(
                                    HomePageAction.ShowVideoMenuSheetAction(
                                        flag = true,
                                        aid = it.aid,
                                        bvid = it.bvid
                                    )
                                )
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
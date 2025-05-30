package com.laohei.bili_tube.presentation.home.recommend

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.laohei.bili_sdk.module_v2.recomment.RecommendItem
import com.laohei.bili_tube.app.PlayParam
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.app.SharedViewModel
import com.laohei.bili_tube.component.placeholder.RecommendPlaceholder
import com.laohei.bili_tube.component.video.VideoItem
import com.laohei.bili_tube.presentation.home.state.HomePageAction
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.toTimeAgoString
import com.laohei.bili_tube.utill.toViewString
import org.koin.compose.koinInject

private const val TAG = "RecommendScreen"

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendScreen(
    randomVideos: LazyPagingItems<RecommendItem>,
    gridState: LazyGridState = rememberLazyGridState(),
    navigateToRoute: (Route) -> Unit,
    homeActionHandle: (HomePageAction) -> Unit,
) {
    val sharedViewModel = koinInject<SharedViewModel>()
    val refreshState = rememberPullToRefreshState()

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val fixedCount = when {
            maxWidth < 500.dp -> 1
            maxWidth >= 500.dp && maxWidth < 800.dp -> 2
            maxWidth >= 800.dp && maxWidth < 1280.dp -> 3
            else -> 4
        }
        PullToRefreshBox(
            isRefreshing = randomVideos.loadState.refresh is LoadState.Loading,
            state = refreshState,
            onRefresh = { randomVideos.refresh() },
            indicator = {
                Indicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding(),
                    isRefreshing = randomVideos.loadState.refresh is LoadState.Loading,
                    state = refreshState,
                )
            }
        ) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(fixedCount),
                contentPadding = PaddingValues(horizontal = if (fixedCount == 1) 0.dp else 8.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                item(span = { GridItemSpan(fixedCount) }) {
                    Spacer(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(top = 12.dp)
                    )
                }
                if (randomVideos.itemCount == 0) {
                    items(20) {
                        RecommendPlaceholder(isSingleLayout = fixedCount == 1)
                    }
                } else {
                    items(randomVideos.itemCount) { index ->
                        randomVideos[index]?.let {
                            VideoItem(
                                isSingleLayout = fixedCount == 1,
                                key = it.bvid,
                                cover = it.pic,
                                title = it.title,
                                face = it.owner?.face ?: "",
                                ownerName = it.owner?.name ?: "",
                                view = it.stat?.view?.toViewString() ?: "",
                                date = it.pubDate.toTimeAgoString(),
                                duration = it.duration.formatTimeString(false),
                                onClick = {
                                    sharedViewModel.setPlayParam(
                                        PlayParam.Video(
                                            aid = it.id,
                                            bvid = it.bvid,
                                            cid = it.cid,
                                        )
                                    )
                                    navigateToRoute(Route.Play)
                                },
                                onMenuClick = {
                                    homeActionHandle.invoke(
                                        HomePageAction.ShowVideoMenuSheetAction(
                                            flag = true,
                                            aid = it.id,
                                            bvid = it.bvid
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
                item(span = { GridItemSpan(fixedCount) }) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        when (randomVideos.loadState.append) {
                            is LoadState.Loading -> CircularProgressIndicator()
                            is LoadState.Error -> Text("加载失败，点击重试")
                            else -> {}
                        }
                    }
                }
                item(span = { GridItemSpan(fixedCount) }) {
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
        }
    }

}
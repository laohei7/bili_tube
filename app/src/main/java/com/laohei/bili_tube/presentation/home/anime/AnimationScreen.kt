package com.laohei.bili_tube.presentation.home.anime

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import com.laohei.bili_sdk.module_v2.bangumi.BangumiItem
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.component.placeholder.NoMoreData
import com.laohei.bili_tube.model.BangumiFilterModel
import com.laohei.bili_tube.presentation.home.anime.component.BangumiWidget
import com.laohei.bili_tube.presentation.home.anime.component.FilterWidget
import com.laohei.bili_tube.presentation.home.state.HomePageAction
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun AnimationScreen(
    gridState: LazyGridState = rememberLazyGridState(),
    animationFilterModel: BangumiFilterModel,
    animations: LazyPagingItems<BangumiItem>,
    homePageActionClick: (HomePageAction) -> Unit,
    navigateToRoute: (Route) -> Unit
) {
    val refreshState = rememberPullToRefreshState()
    val isRefreshing = animations.loadState.refresh is LoadState.Loading

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val fixedCount = when {
            maxWidth < 500.dp -> 3
            maxWidth >= 500.dp && maxWidth < 800.dp -> 4
            maxWidth >= 800.dp && maxWidth < 1280.dp -> 5
            else -> 6
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            state = refreshState,
            onRefresh = { animations.refresh() },
            indicator = {
                Indicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding(),
                    isRefreshing = isRefreshing,
                    state = refreshState,
                )
            }
        ) {
            LazyVerticalGrid(
                state = gridState,
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                columns = GridCells.Fixed(fixedCount),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item(span = { GridItemSpan(fixedCount) }) { Spacer(Modifier.height(30.dp)) }
                item(span = { GridItemSpan(fixedCount) }) {
                    FilterWidget(
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                        bangumiFilterModel = animationFilterModel,
                        filters = AnimationFilters
                    ) { key, value ->
                        homePageActionClick.invoke(
                            HomePageAction.AnimeFilterAction(
                                true,
                                key,
                                value
                            )
                        )
                    }
                }
                items(
                    animations.itemCount,
                    key = { animations[it]?.seasonId ?: Uuid.random() }) { index ->
                    animations[index]?.let {
                        BangumiWidget(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    navigateToRoute.invoke(
                                        Route.Play(
                                            seasonId = it.seasonId,
                                            epId = it.firstEp.epId,
                                            mediaId = it.mediaId,
                                            isVideo = false
                                        )
                                    )
                                },
                            cover = it.cover,
                            title = it.title,
                            label = it.indexShow
                        )
                    }
                }
                item(span = { GridItemSpan(fixedCount) }) {
                    NoMoreData(animations.loadState.append)
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
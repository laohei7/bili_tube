package com.laohei.bili_tube.features.main.home.anime

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import com.laohei.bili_tube.PlayParam
import com.laohei.bili_tube.SharedViewModel
import com.laohei.bili_tube.features.main.home.HomeAction
import com.laohei.bili_tube.features.main.home.anime.component.BangumiWidget
import com.laohei.bili_tube.features.main.home.anime.component.FilterWidget
import com.laohei.bili_tube.model.BangumiFilterModel
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.foundation.DeviceConfiguration
import com.laohei.bili_tube.ui.component.placeholder.NoMoreData
import com.laohei.bili_tube.ui.theme.LargePadding
import com.laohei.bili_tube.ui.theme.SmallPadding
import org.koin.compose.koinInject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun AnimationScreen(
    gridState: LazyGridState = rememberLazyGridState(),
    animationFilterModel: BangumiFilterModel,
    animations: LazyPagingItems<BangumiItem>,
    onHomeAction: (HomeAction) -> Unit,
    navigateToAppRoute: (AppRoute) -> Unit
) {
    val sharedViewModel = koinInject<SharedViewModel>()
    val refreshState = rememberPullToRefreshState()
    val isRefreshing = animations.loadState.refresh is LoadState.Loading

    AdaptiveLayout { uiType, _, _ ->
        val fixedCount = when (uiType) {
            DeviceConfiguration.MOBILE_PORTRAIT -> 3
            DeviceConfiguration.MOBILE_LANDSCAPE,
            DeviceConfiguration.TABLE_PORTRAIT,
            DeviceConfiguration.TABLE_LANDSCAPE,
            DeviceConfiguration.DESKTOP -> {
                when {
                    maxWidth >= 500.dp && maxWidth < 800.dp -> 4
                    maxWidth >= 800.dp && maxWidth < 1280.dp -> 5
                    else -> 6
                }
            }
        }
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            state = refreshState,
            onRefresh = { animations.refresh() },
            indicator = {
                Indicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .offset(y = 50.dp),
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
                verticalArrangement = Arrangement.spacedBy(LargePadding)
            ) {
                item(span = { GridItemSpan(fixedCount) }, key = "Anime-top-padding") {
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
                                .height(SmallPadding))
                        }
                    }
                }

                item(span = { GridItemSpan(fixedCount) }) {
                    FilterWidget(
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                        bangumiFilterModel = animationFilterModel,
                        filters = AnimationFilters
                    ) { key, value ->
                        onHomeAction(HomeAction.AnimeFilterAction(true, key, value))
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
                                    sharedViewModel.setPlayParam(
                                        PlayParam.BangumiParam(
                                            seasonId = it.seasonId,
                                            epId = it.firstEp.epId,
                                            mediaId = it.mediaId,
                                            aid = -1,
                                            cid = -1,
                                            bvid = ""
                                        )
                                    )
                                    navigateToAppRoute(AppRoute.Play)
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
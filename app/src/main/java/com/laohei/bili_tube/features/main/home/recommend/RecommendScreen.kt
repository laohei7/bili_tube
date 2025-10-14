package com.laohei.bili_tube.features.main.home.recommend

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.laohei.bili_sdk.model_v2.recommend.RecommendItem
import com.laohei.bili_tube.features.main.home.HomeAction
import com.laohei.bili_tube.model.play.MediaPlayConfig
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.component.state.LoadingStatePlaceholder
import com.laohei.bili_tube.ui.component.state.RecommendationPlaceholder
import com.laohei.bili_tube.ui.component.video.VerticalVideoCard
import com.laohei.bili_tube.ui.foundation.DeviceConfiguration
import com.laohei.bili_tube.ui.theme.PaddingLg
import com.laohei.bili_tube.ui.theme.PaddingNone
import com.laohei.bili_tube.ui.theme.PaddingSm
import com.laohei.bili_tube.ui.viewmodel.SharedViewModel
import com.laohei.bili_tube.util.toTimeAgoString
import com.laohei.bili_tube.util.toTimeString
import com.laohei.bili_tube.util.toViewString
import org.koin.compose.koinInject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val TAG = "RecommendScreen"

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun RecommendScreen(
    recommends: LazyPagingItems<RecommendItem>,
    gridState: LazyGridState = rememberLazyGridState(),
    navigateToAppRoute: (AppRoute) -> Unit,
    onHomeAction: (HomeAction) -> Unit,
) {
    val sharedViewModel = koinInject<SharedViewModel>()
    val refreshState = rememberPullToRefreshState()
    val isRefreshing = recommends.loadState.refresh is LoadState.Loading

    AdaptiveLayout(
        modifier = Modifier.fillMaxSize()
    ) { uiType, width, height ->
        val fixedCount = when (uiType) {
            DeviceConfiguration.MOBILE_PORTRAIT -> 1
            DeviceConfiguration.MOBILE_LANDSCAPE,
            DeviceConfiguration.TABLE_PORTRAIT,
            DeviceConfiguration.TABLE_LANDSCAPE,
            DeviceConfiguration.DESKTOP -> {
                when {
                    width >= 500.dp && width < 800.dp -> 2
                    width >= 800.dp && width < 1280.dp -> 3
                    else -> 4
                }
            }
        }
        val isSingle = fixedCount == 1
        val shape = when {
            isSingle -> RoundedCornerShape(PaddingNone)
            else -> RoundedCornerShape(PaddingSm)
        }
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            state = refreshState,
            onRefresh = { recommends.refresh() },
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
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = if (isSingle) PaddingNone else PaddingSm),
                state = gridState,
                columns = GridCells.Fixed(fixedCount),
                verticalArrangement = Arrangement.spacedBy(PaddingLg),
                horizontalArrangement = Arrangement.spacedBy(if (isSingle) PaddingNone else PaddingSm)
            ) {
                item(span = { GridItemSpan(fixedCount) }, key = "Recommend-top-padding") {
                    when (uiType) {
                        DeviceConfiguration.MOBILE_PORTRAIT,
                        DeviceConfiguration.TABLE_PORTRAIT -> {
                            Spacer(
                                Modifier
                                    .statusBarsPadding()
                                    .height(72.dp)
                            )
                        }

                        DeviceConfiguration.MOBILE_LANDSCAPE,
                        DeviceConfiguration.TABLE_LANDSCAPE,
                        DeviceConfiguration.DESKTOP -> {
                            Spacer(
                                Modifier
                                    .height(42.dp)
                            )
                        }
                    }
                }


                when {
                    recommends.itemCount == 0 -> {
                        items(20) {
                            RecommendationPlaceholder(isSingle)
                        }
                    }

                    else -> {
                        items(
                            recommends.itemCount,
                            key = { recommends[it]?.bvid ?: Uuid.random().toString() }
                        ) { index ->
                            recommends[index]?.let {
                                VerticalVideoCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(shape),
                                    coverShape = shape,
                                    bvid = it.bvid,
                                    coverUrl = it.pic,
                                    title = remember(it.bvid) { it.title },
                                    ownerFaceUrl = it.owner?.face ?: "",
                                    ownerName = it.owner?.name ?: "",
                                    viewCount = it.stat?.view?.toViewString() ?: "",
                                    publishDate = it.pubDate.toTimeAgoString(false),
                                    duration = it.duration.toTimeString(false),
                                    trailingIcon = Icons.Outlined.MoreVert,
                                    onClick = {
                                        sharedViewModel.setPlayParam(
                                            MediaPlayConfig.BasicVideoConfig(
                                                aid = it.id,
                                                bvid = it.bvid,
                                                cid = it.cid,
                                            )
                                        )
                                        navigateToAppRoute(AppRoute.Play)
                                    },
                                    onTrailingClick = {
                                        onHomeAction(
                                            HomeAction.MenuSheetUIAction(
                                                true,
                                                it.id,
                                                it.bvid,
                                                "【${it.owner?.name}】${it.title} ${it.uri}"
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                item(
                    span = { GridItemSpan(fixedCount) },
                    key = "Recommend-no-more-data-placeholder"
                ) {
                    LoadingStatePlaceholder(loadState = recommends.loadState.append)
                }
                item(span = { GridItemSpan(fixedCount) }, key = "Recommend-bottom-padding") {
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
        }
    }

}
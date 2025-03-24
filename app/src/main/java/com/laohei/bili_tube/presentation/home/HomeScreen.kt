package com.laohei.bili_tube.presentation.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.paging.compose.collectAsLazyPagingItems
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.component.appbar.LogoTopAppBar
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.presentation.home.anime.AnimeScreen
import com.laohei.bili_tube.presentation.home.hot.HotScreen
import com.laohei.bili_tube.presentation.home.recommend.RecommendScreen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs

private const val TAG = "HomeScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = koinViewModel(),
    navigateToRoute: (Route) -> Unit
) {
    val scope = rememberCoroutineScope()
    val tabs = homeViewModel.tabs
    val pagerState = homeViewModel.pagerState
    val density = LocalDensity.current

    val minHeight = 80.dp
    val maxHeight = 120.dp

    val minHeightPx = with(density) {
        minHeight.toPx()
    }

    val maxHeightPx = with(density) {
        maxHeight.toPx()
    }

    var rawAlpha by remember { mutableFloatStateOf(1f) }
    val alpha by animateFloatAsState(targetValue = rawAlpha, label = "alpha")
    var rawLogoHeight by remember { mutableIntStateOf(with(density) { 0.dp.toPx() }.toInt()) }
    val logoHeight by animateIntAsState(targetValue = rawLogoHeight, label = "logoHeight")
    var topHeightPx by remember { mutableFloatStateOf(maxHeightPx) }

    val recommendVideos = homeViewModel.randomVideos.collectAsLazyPagingItems()
    val hotVideos = homeViewModel.hotVideos.collectAsLazyPagingItems()
    val timelines = homeViewModel.timeline.collectAsLazyPagingItems()

    val connection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source == NestedScrollSource.UserInput) {  // 判断是滑动事件
                    if (available.y < 0) { // 向上滑动
                        val dH = minHeightPx - topHeightPx  // 向上滑动过程中，还差多少达到最小高度
                        rawAlpha = abs(dH) / (maxHeightPx - minHeightPx)
                        rawLogoHeight =
                            with(density) { lerp((-40).dp, 0.dp, rawAlpha).toPx().toInt() }
                        topHeightPx += if (available.y > dH) {  // 如果当前可用的滑动距离全部消费都不足以达到最小高度，就将当前可用距离全部消费掉
                            available.y
                        } else {  // 如果当前可用的滑动距离足够达到最小高度，就只消费掉需要的距离。剩余的给到子组件。
                            dH
                        }
                    } else { // 下滑
                        val dH = maxHeightPx - topHeightPx  // 向下滑动过程中，还差多少达到最大高度
                        rawAlpha = 1f - abs(dH) / (maxHeightPx - minHeightPx)
                        rawLogoHeight =
                            with(density) { lerp((-40).dp, 0.dp, rawAlpha).toPx().toInt() }
                        topHeightPx += if (available.y < dH) {  // 如果当前可用的滑动距离全部消费都不足以达到最大高度，就将当前可用距离全部消费掉
                            available.y
                        } else {  // 如果当前可用的滑动距离足够达到最大高度，就只消费掉需要的距离。剩余的给到子组件。
                            dH
                        }
                    }
                }
                return Offset.Zero

            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                return super.onPreFling(available)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                return super.onPostFling(consumed, available)
            }
        }
    }


    LaunchedEffect(Unit) {
        EventBus.events.collect { event ->
            when (event) {
                is Event.NotificationChildRefresh -> {
                    val currentState = homeViewModel.gridStates[pagerState.currentPage]
                    scope.launch {
                        currentState.scrollToItem(0)
                        when (pagerState.currentPage) {
                            0 -> recommendVideos.refresh()
                            1 -> hotVideos.refresh()
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(connection)
            .navigationBarsPadding()
    ) { padding ->
        Box {
            HorizontalPager(
                modifier = Modifier.offset {
                    IntOffset(0, logoHeight + with(density) { 40.dp.toPx().toInt() })
                },
                state = pagerState,
                beyondViewportPageCount = 2
            ) { index ->
                when (index) {
                    0 -> RecommendScreen(
                        randomVideos = recommendVideos,
                        gridState = homeViewModel.gridStates[index],
                        navigateToRoute = navigateToRoute
                    )

                    1 -> HotScreen(
                        hotVideos = hotVideos,
                        gridState = homeViewModel.gridStates[index],
                        navigateToRoute = navigateToRoute
                    )

                    2 -> AnimeScreen(
                        timelines = timelines
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset {
                        IntOffset(0, logoHeight)
                    }
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.989f)
                    )
            ) {
                LogoTopAppBar(alpha = alpha)

                PrimaryTabRow(
                    modifier = Modifier
                        .height(IntrinsicSize.Min),
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = Color.Transparent,
                ) {
                    tabs.fastForEachIndexed { index, tab ->
                        Tab(
                            selected = index == pagerState.currentPage,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                        ) {
                            Text(
                                text = tab,
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 4.dp)
                            )
                        }
                    }
                }
            }

        }

    }
}
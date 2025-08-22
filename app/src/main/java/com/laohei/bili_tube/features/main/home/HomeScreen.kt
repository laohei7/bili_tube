package com.laohei.bili_tube.features.main.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.paging.compose.collectAsLazyPagingItems
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.features.main.component.VideoMenuSheet
import com.laohei.bili_tube.features.main.home.anime.AnimationScreen
import com.laohei.bili_tube.features.main.home.anime.BangumiScreen
import com.laohei.bili_tube.features.main.home.component.HomeTopBar
import com.laohei.bili_tube.features.main.home.hot.HotScreen
import com.laohei.bili_tube.features.main.home.recommend.RecommendScreen
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.component.dialog.CreatedFolderDialog
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.component.layout.DeviceConfiguration
import com.laohei.bili_tube.ui.component.sheet.FolderSheet
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs

private const val TAG = "HomeScreen"
private const val DBG = true

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = koinViewModel(),
    navigateToAppRoute: (AppRoute) -> Unit
) {
    val density = LocalDensity.current

    val homeState by homeViewModel.homeState.collectAsState()
    val scope = rememberCoroutineScope()
    val pager = homeState.pager

    // top bar nested scroll calculate
    val minHeight = 80.dp
    val maxHeight = 120.dp
    val minHeightPx = with(density) { minHeight.toPx() }
    val maxHeightPx = with(density) { maxHeight.toPx() }
    var rawAlpha by remember { mutableFloatStateOf(1f) }
    val alpha by animateFloatAsState(targetValue = rawAlpha, label = "alpha")
    var rawLogoHeight by remember { mutableIntStateOf(with(density) { 0.dp.toPx() }.toInt()) }
    val logoHeight by animateIntAsState(targetValue = rawLogoHeight, label = "logoHeight")
    var topHeightPx by remember { mutableFloatStateOf(maxHeightPx) }

    val recommendVideos = homeViewModel.recommends.collectAsLazyPagingItems()
    val hotVideos = homeViewModel.hots.collectAsLazyPagingItems()
    val bangumis = homeViewModel.bangumis.collectAsLazyPagingItems()
    val animations = homeViewModel.animations.collectAsLazyPagingItems()

    val connection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source == NestedScrollSource.UserInput) {  // Judgment is a sliding event
//                    Log.d(TAG, "onPreScroll: ${available.x} ${available.y}")
                    if (available.y < 0 && available.x == 0f) { // Swipe up
                        val dH =
                            minHeightPx - topHeightPx  // There is still a little bit of reaching the minimum height
                        rawAlpha = abs(dH) / (maxHeightPx - minHeightPx)
                        rawLogoHeight =
                            with(density) { lerp((-40).dp, 0.dp, rawAlpha).toPx().toInt() }
                        topHeightPx += if (available.y > dH) {
                            available.y
                        } else {
                            dH
                        }
                    } else if (available.y >= 0 && available.x == 0f) { // decline
                        val dH =
                            maxHeightPx - topHeightPx  // It's still a little short of reaching the maximum height
                        rawAlpha = 1f - abs(dH) / (maxHeightPx - minHeightPx)
                        rawLogoHeight =
                            with(density) { lerp((-40).dp, 0.dp, rawAlpha).toPx().toInt() }
                        topHeightPx += if (available.y < dH) {
                            available.y
                        } else {
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
                    val currentState = homeState.tabGridStates[pager.currentPage]
                    scope.launch {
                        currentState.scrollToItem(0)
                        rawLogoHeight = 0
                        rawAlpha = 1f
                        when (pager.currentPage) {
                            0 -> recommendVideos.refresh()
                            1 -> hotVideos.refresh()
                            2 -> bangumis.refresh()
                            3 -> animations.refresh()
                        }
                    }
                }
            }
        }
    }

    AdaptiveLayout(
        modifier = Modifier
            .nestedScroll(connection)
            .background(MaterialTheme.colorScheme.background)
    ) { uiType, _, _ ->
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize(),
            state = pager,
        ) { index ->
            when (index) {
                0 -> RecommendScreen(
                    recommends = recommendVideos,
                    gridState = homeState.tabGridStates[index],
                    navigateToAppRoute = navigateToAppRoute,
                    onHomeAction = homeViewModel::onHomeAction
                )

                1 -> HotScreen(
                    hotVideos = hotVideos,
                    gridState = homeState.tabGridStates[index],
                    navigateToAppRoute = navigateToAppRoute,
                    onHomeAction = homeViewModel::onHomeAction
                )

                2 -> BangumiScreen(
                    gridState = homeState.tabGridStates[index],
                    bangumis = bangumis,
                    bangumiFilterModel = homeState.bangumiFilter,
                    onHomeAction = homeViewModel::onHomeAction,
                    navigateToAppRoute = navigateToAppRoute
                )

                3 -> AnimationScreen(
                    gridState = homeState.tabGridStates[index],
                    animations = animations,
                    animationFilterModel = homeState.animeFilter,
                    onHomeAction = homeViewModel::onHomeAction,
                    navigateToAppRoute = navigateToAppRoute
                )
            }
        }

        HomeTopBar(
            isOnlyTabs = when (uiType) {
                DeviceConfiguration.MOBILE_PORTRAIT,
                DeviceConfiguration.TABLE_PORTRAIT -> false

                DeviceConfiguration.MOBILE_LANDSCAPE,
                DeviceConfiguration.TABLE_LANDSCAPE,
                DeviceConfiguration.DESKTOP -> true
            },
            tabs = HomeTabs,
            offset = when (uiType) {
                DeviceConfiguration.MOBILE_PORTRAIT,
                DeviceConfiguration.TABLE_PORTRAIT -> IntOffset(0, logoHeight)

                DeviceConfiguration.MOBILE_LANDSCAPE,
                DeviceConfiguration.TABLE_LANDSCAPE,
                DeviceConfiguration.DESKTOP -> IntOffset(0, 0)
            },
            alpha = when (uiType) {
                DeviceConfiguration.MOBILE_PORTRAIT,
                DeviceConfiguration.TABLE_PORTRAIT -> alpha

                DeviceConfiguration.MOBILE_LANDSCAPE,
                DeviceConfiguration.TABLE_LANDSCAPE,
                DeviceConfiguration.DESKTOP -> 1f
            },
            selectedTabIndex = pager.currentPage,
            onTabClick = {
                scope.launch {
                    pager.animateScrollToPage(it)
                }
            },
            navigateToAppRoute = { navigateToAppRoute(AppRoute.Search) }
        )



        VideoMenuSheet(
            isShowSheet = homeState.showMenuSheet,
            onDismiss = {
                homeViewModel.onHomeAction(HomeAction.MenuSheetUIAction(false))
            }
        ) {
            val action = when (it) {
                R.string.str_save_playlist -> {
                    HomeAction.FolderSheetUIAction(true, aid = homeState.selectedAid)
                }

                R.string.str_save_watch_later -> {
                    HomeAction.AddToViewAction(
                        aid = homeState.selectedAid!!,
                        bvid = homeState.selectedBvid!!
                    )
                }

                else -> HomeAction.NoneAction
            }
            homeViewModel.onHomeAction(action)
        }

        FolderSheet(
            folders = homeState.folders,
            isShowSheet = homeState.showFolderSheet,
            onDismiss = {
                homeViewModel.onHomeAction(HomeAction.FolderSheetUIAction(false))
            },
            onCreateFolder = {
                homeViewModel.onHomeAction(HomeAction.FolderCreatedUIAction(true))
            },
            onAddToFolder = { addAids, delAids ->
                if (homeState.selectedAid == null) {
                    return@FolderSheet
                }
                homeViewModel.onHomeAction(
                    HomeAction.AddToFoldersAction(
                        addAids = addAids,
                        delAids = delAids,
                        aid = homeState.selectedAid!!
                    )
                )
                homeViewModel.onHomeAction(HomeAction.FolderSheetUIAction(false))
            }
        )

        CreatedFolderDialog(
            isShowDialog = homeState.showAddFolder,
            value = homeState.newFolderName,
            onValueChange = homeViewModel::onFolderNameChanged,
            onSubmit = homeViewModel::addNewFolder,
            checked = homeState.isPrivateFolder,
            onCheckedChange = homeViewModel::onPrivateChanged,
            onDismiss = {
                homeViewModel.onFolderNameChanged("")
                homeViewModel.onHomeAction(HomeAction.FolderCreatedUIAction(false))
            }
        )
    }
}
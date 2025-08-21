package com.laohei.bili_tube.features.search

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.paging.compose.collectAsLazyPagingItems
import com.laohei.bili_tube.di.appModule
import com.laohei.bili_tube.di.dataModule
import com.laohei.bili_tube.di.roomModule
import com.laohei.bili_tube.di.viewModelModule
import com.laohei.bili_tube.features.search.component.SearchResultList
import com.laohei.bili_tube.features.search.component.SearchTopBar
import com.laohei.bili_tube.nav.AppRoute
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplication

private const val TAG = "SearchScreen"
private const val DBG = true

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel<SearchViewModel>(),
    navigateToAppRoute: (AppRoute) -> Unit,
    upPress: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val isRefreshing = uiState.isSearching

    val pagerState = rememberPagerState { uiState.tabs.size }

    val results = uiState.results.collectAsLazyPagingItems()
    val videos = uiState.videos.collectAsLazyPagingItems()
    val bangumis = uiState.bangumis.collectAsLazyPagingItems()
    val fts = uiState.fts.collectAsLazyPagingItems()
    val histories = uiState.searchHistories.collectAsLazyPagingItems()

    val refreshState = rememberPullToRefreshState()

    BackHandler(enabled = uiState.expanded) {
        viewModel.onExpandedChange(false)
    }

    fun onTabClick(index: Int) {
        scope.launch {
            Log.d(TAG, "onTabClick: scroll to $index")
            pagerState.animateScrollToPage(index)
        }
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                histories = histories,
                tabs = uiState.tabs,
                selectedTabIndex = pagerState.currentPage,
                expanded = uiState.expanded,
                keyword = uiState.keyword,
                onValueChange = viewModel::onKeywordChange,
                onExpandedChange = viewModel::onExpandedChange,
                upPress = upPress,
                onSearch = viewModel::onSearch,
                onTabClick = ::onTabClick
            )
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState
        ) { index ->
            val data = when (index) {
                1 -> videos
                2 -> bangumis
                3 -> fts
                else -> results
            }
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                state = refreshState,
                onRefresh = { viewModel.onSearch() },
                indicator = {
                    Indicator(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(innerPadding),
                        isRefreshing = isRefreshing,
                        state = refreshState,
                    )
                }
            ) {
                SearchResultList(
                    paddingValues = innerPadding,
                    list = data,
                    showHeader = index == 0,
                    navigateToAppRoute = navigateToAppRoute,
                    scrollToPage = {
                        scope.launch {
                            val target = uiState.tabs.indexOf(it).coerceAtLeast(0)
                            pagerState.animateScrollToPage(target)
                        }
                    }
                )
            }
        }

    }
}


@androidx.annotation.OptIn(UnstableApi::class)
@Preview
@Composable
private fun SearchScreenPreview() {
    KoinApplication(
        application = {
            modules(appModule, roomModule, dataModule, viewModelModule)
        }
    ) {
        SearchScreen(
            navigateToAppRoute = {}
        )
    }
}

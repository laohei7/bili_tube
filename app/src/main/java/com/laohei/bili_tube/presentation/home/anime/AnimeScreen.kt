package com.laohei.bili_tube.presentation.home.anime

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_sdk.module_v2.bangumi.BangumiItem
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.component.placeholder.NoMoreData
import com.laohei.bili_tube.model.BangumiFilterModel
import com.laohei.bili_tube.presentation.home.state.HomePageAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeScreen(
    bangumiFilterModel: BangumiFilterModel,
    bangumis: LazyPagingItems<BangumiItem>,
    homePageActionClick: (HomePageAction) -> Unit,
    navigateToRoute: (Route) -> Unit
) {
    val refreshState = rememberPullToRefreshState()
    val isRefreshing = bangumis.loadState.refresh is LoadState.Loading

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
            onRefresh = { bangumis.refresh() },
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
                        bangumiFilterModel = bangumiFilterModel
                    ) { key, value ->
                        homePageActionClick.invoke(HomePageAction.AnimeFilterAction(key, value))
                    }
                }
                items(bangumis.itemCount) { index ->
                    bangumis[index]?.let {
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
                    NoMoreData(bangumis.loadState.append)
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

@Composable
private fun BangumiWidget(
    modifier: Modifier = Modifier,
    cover: String,
    title: String,
    label: String,
) {
    val context = LocalContext.current
    val coverRequest = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(cover)
            .crossfade(false)
            .placeholder(R.drawable.icon_loading)
            .error(R.drawable.icon_loading)
            .build()
    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = coverRequest,
            contentDescription = "",
            modifier = Modifier
                .aspectRatio(3 / 4f)
                .clip(RoundedCornerShape(8.dp))
        )
        Text(
            text = title,
            maxLines = 2,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall,
            color = Color.LightGray
        )
    }
}

@Composable
private fun FilterWidget(
    modifier: Modifier = Modifier,
    bangumiFilterModel: BangumiFilterModel,
    onClick: (String, String) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BangumiFilters.forEach { (key, value) ->
            FilterRow(
                title = key, filters = value, selected = bangumiFilterModel.getValue(key),
                onClick = onClick
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FilterRow(
    title: String,
    filters: List<Pair<String, String>>,
    selected: String,
    onClick: (String, String) -> Unit
) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        stickyHeader {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        items(filters) {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onClick.invoke(title, it.second)
                    },
                contentColor = when {
                    selected == it.second -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.onBackground
                },
            ) {
                Text(
                    text = it.first,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilterWidgetPreview() {
    var bangumiFilterModel by remember {
        mutableStateOf(BangumiFilterModel())
    }
    FilterWidget(bangumiFilterModel = bangumiFilterModel) { key, value ->
        bangumiFilterModel = bangumiFilterModel.update(key, value)
    }
}
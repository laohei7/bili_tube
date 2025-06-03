package com.laohei.bili_tube.presentation.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import androidx.media3.common.util.UnstableApi
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_sdk.module_v2.search.SearchResultItemType
import com.laohei.bili_sdk.search.SearchRequest
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.PlayParam
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.app.SharedViewModel
import com.laohei.bili_tube.component.input.BasicInput
import com.laohei.bili_tube.component.placeholder.NoMoreData
import com.laohei.bili_tube.component.text.RichText
import com.laohei.bili_tube.component.video.HorizontalVideoItem
import com.laohei.bili_tube.model.UIModel
import com.laohei.bili_tube.repository.BiliSearchRepository
import com.laohei.bili_tube.ui.theme.Pink
import com.laohei.bili_tube.utill.HttpClientFactory
import com.laohei.bili_tube.utill.completeUrl
import com.laohei.bili_tube.utill.formatDateToYearString
import com.laohei.bili_tube.utill.toTimeAgoString
import com.laohei.bili_tube.utill.toViewString
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

private const val TAG = "SearchScreen"
private const val DBG = true

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel<SearchViewModel>(),
    navigateToRoute: (Route) -> Unit,
    upPress: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val tabs = remember { listOf("综合", "视频", "番剧", "影视") }
    val state by viewModel.state.collectAsState()
    val results = state.results.collectAsLazyPagingItems()
    val videos = state.videos.collectAsLazyPagingItems()
    val bangumis = state.bangumis.collectAsLazyPagingItems()
    val fts = state.fts.collectAsLazyPagingItems()
    val isRefreshing = state.isSearching
    val pagerState = rememberPagerState { tabs.size }

    LaunchedEffect(
        results.itemCount, results.loadState.refresh,
        videos.itemCount, videos.loadState.refresh,
        bangumis.itemCount, bangumis.loadState.refresh,
        fts.itemCount, fts.loadState.refresh,
    ) {
        val data = when (pagerState.currentPage) {
            1 -> videos
            2 -> bangumis
            3 -> fts
            else -> results
        }
        when {
            data.itemCount != 0 -> {
                viewModel.updateState(
                    state.copy(isSearching = data.loadState.refresh is LoadState.Loading)
                )
            }
        }
    }

    BackHandler(enabled = state.expanded) {
        viewModel.onExpandedChanged(false)
    }
    Scaffold(
        topBar = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                SearchTopBar(
                    expanded = state.expanded,
                    value = state.keyword,
                    onValueChanged = viewModel::onKeywordChanged,
                    onExpandedChanged = viewModel::onExpandedChanged,
                    upPress = upPress,
                    onSearch = viewModel::onSearch
                )
                PrimaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    divider = {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.surfaceContainer
                        )
                    }
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
                            Text(text = tab, modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
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
            SearchResultList(
                paddingValues = innerPadding,
                isRefreshing = isRefreshing,
                list = data,
                showHeader = index == 0,
                navigateToRoute = navigateToRoute,
                scrollToPage = {
                    scope.launch {
                        val target = tabs.indexOf(it).coerceAtLeast(0)
                        pagerState.animateScrollToPage(target)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchResultList(
    paddingValues: PaddingValues,
    isRefreshing: Boolean,
    list: LazyPagingItems<UIModel<out Any?>>,
    showHeader: Boolean,
    navigateToRoute: (Route) -> Unit,
    scrollToPage: (String) -> Unit,
) {
    val refreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        state = refreshState,
        onRefresh = { list.refresh() },
        indicator = {
            Indicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 60.dp),
                isRefreshing = isRefreshing,
                state = refreshState,
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(list.itemCount) { index ->
                list[index]?.let { item ->
                    when (item) {
                        is UIModel.Header<*> -> {
                            if (showHeader.not()) {
                                return@let
                            }
                            GetHeader(
                                type = item.header,
                                onClick = { scrollToPage.invoke(it) }
                            )
                        }

                        is UIModel.Item<*> -> {
                            item.item?.let {
                                GetSearchItem(
                                    item = it as SearchResultItemType,
                                    navigateToRoute = navigateToRoute
                                )
                            }
                        }
                    }
                }
            }
            item { NoMoreData(list.loadState.append) }
            item {
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }


}

@Composable
private fun GetHeader(
    type: Any?,
    onClick: (String) -> Unit
) {
    val title = when (type) {
        SearchResultItemType.TYPE_VIDEO -> {
            Pair(stringResource(R.string.str_video), R.drawable.bili_emoji1)
        }

        SearchResultItemType.TYPE_MEDIA_FT -> {
            Pair(stringResource(R.string.str_moive), R.drawable.bili_emoji4)
        }

        SearchResultItemType.TYPE_MEDIA_BANGUMI -> {
            Pair(
                stringResource(R.string.str_bangumi),
                R.drawable.bili_emoji5
            )
        }

        else -> null
    }
    title?.let {
        ListItem(
            leadingContent = {
                Image(
                    painter = painterResource(it.second),
                    contentDescription = "icon",
                    modifier = Modifier.size(36.dp)
                )
            },
            headlineContent = {
                Text(
                    text = it.first,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Pink
                )
            },
            trailingContent = {
                TextButton(onClick = { onClick.invoke(it.first) }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.str_see_more),
                            style = MaterialTheme.typography.labelMedium
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                            contentDescription = Icons.AutoMirrored.Outlined.KeyboardArrowRight.name,
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun GetSearchItem(
    item: SearchResultItemType,
    navigateToRoute: (Route) -> Unit
) {
    val sharedViewModel = koinInject<SharedViewModel>()
    when (item) {
        is SearchResultItemType.MediaBangumiItem -> {
            BangumiItem(
                title = item.title,
                cover = item.cover,
                areas = item.areas,
                date = item.pubTime.formatDateToYearString(false),
                styles = item.styles,
                score = item.mediaScore.score,
                userCount = item.mediaScore.userCount.toViewString(),
                episodes = item.eps?.fastMap { it.title },
                onClick = {
                    sharedViewModel.setPlayParam(
                        PlayParam.Bangumi(
                            mediaId = item.mediaId,
                            seasonId = item.seasonId,
                            epId = item.eps?.first()?.id,
                            aid = -1, cid = -1, bvid = ""
                        )
                    )
                    navigateToRoute(Route.Play)
                }
            )
        }

        is SearchResultItemType.MediaFTItem -> {
            BangumiItem(
                title = item.title,
                cover = item.cover,
                areas = item.areas,
                date = item.pubTime.formatDateToYearString(false),
                styles = item.styles,
                score = item.mediaScore.score,
                userCount = item.mediaScore.userCount.toViewString(),
                episodes = item.eps?.fastMap { it.title },
                onClick = {
                    sharedViewModel.setPlayParam(
                        PlayParam.Bangumi(
                            mediaId = item.mediaId,
                            seasonId = item.seasonId,
                            epId = item.eps?.first()?.id,
                            aid = -1, cid = -1, bvid = ""
                        )
                    )
                    navigateToRoute(Route.Play)
                }
            )
        }

        is SearchResultItemType.VideoItem -> {
            HorizontalVideoItem(
                cover = item.pic.completeUrl(),
                title = item.title,
                ownerName = item.author,
                rcmdReason = "",
                duration = item.duration,
                view = item.play.toViewString(),
                publishDate = item.pubDate.toTimeAgoString(),
                onClick = {
                    sharedViewModel.setPlayParam(
                        PlayParam.Video(
                            aid = item.aid,
                            bvid = item.bvid,
                            cid = -1L,
                        )
                    )
                    navigateToRoute(Route.Play)
                },
                trailingOnClick = {

                },
                leadingIcon = null
            )
        }

        SearchResultItemType.UnknownItem -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    expanded: Boolean,
    value: String,
    placeholder: String = "",
    onValueChanged: (String) -> Unit,
    onExpandedChanged: (Boolean) -> Unit,
    upPress: () -> Unit,
    onSearch: () -> Unit,
) {
    SearchBar(
        expanded = expanded,
        onExpandedChange = onExpandedChanged,
        inputField = {
            val textStyle = MaterialTheme.typography.bodySmall
            BasicInput(
                value = value,
                onValueChanged = onValueChanged,
                borderStroke = BorderStroke(1.dp, Color.LightGray),
                inputIconColor = MaterialTheme.colorScheme.primary,
                textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onBackground),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                singleLine = true,
                navigationIcon = {
                    IconButton(onClick = { upPress.invoke() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = Icons.AutoMirrored.Outlined.ArrowBack.name,
                        )
                    }
                },
                trailingIcon = {
                    TextButton(onClick = { onSearch.invoke() }) {
                        Text(text = stringResource(R.string.str_search))
                    }
                },
                placeholder = {
                    Text(
                        text = placeholder.ifBlank { stringResource(R.string.str_search_hint) },
                        style = textStyle, color = Color.Gray
                    )
                }
            )
        },
        colors = SearchBarDefaults.colors(
            dividerColor = Color.Transparent,
            containerColor = Color.Transparent
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        LazyColumn {
            items(20) {
                SearchHistoryItem(history = "History $it")
            }
        }
    }
}

@Composable
private fun BangumiItem(
    title: String,
    cover: String,
    areas: String,
    date: String,
    styles: String,
    score: Float,
    userCount: String,
    episodes: List<String>?,
    onClick: () -> Unit
) {
    val coverRequest = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(cover)
            .crossfade(false)
            .placeholder(R.drawable.icon_loading)
            .error(R.drawable.bg)
            .build()
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(MaterialTheme.colorScheme.background)
            .clickable { onClick.invoke() }
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = coverRequest,
                contentDescription = title,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(3f / 4f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier.weight(2.5f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RichText(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    collapsedMaxLine = 2,
                    emote = emptyMap(),
                    enabledExpanded = false,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.str_date_and_area, date, areas),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    color = Color.LightGray
                )
                Text(
                    text = styles,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    color = Color.LightGray
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(score.toString())
                        }
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(stringResource(R.string.str_score))
                        }
                        append(" ")
                        withStyle(
                            SpanStyle(
                                fontSize = MaterialTheme.typography.labelSmall.fontSize
                            )
                        ) {
                            append(stringResource(R.string.str_vote, userCount))
                        }
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                )
            }
            Surface(
                onClick = {},
                shape = CircleShape,
                color = Pink,
                contentColor = Color.White
            ) {
                Text(
                    text = "立即观看",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        episodes?.let { list ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when {
                    list.size > 6 -> {
                        list.take(2).fastForEach {
                            EpisodeWidget(title = it, modifier = Modifier.weight(1f))
                        }
                        EpisodeWidget(title = "...", modifier = Modifier.weight(1f))
                        list.takeLast(3).fastForEach {
                            EpisodeWidget(title = it, modifier = Modifier.weight(1f))
                        }
                    }

                    else -> {
                        list.fastForEach {
                            EpisodeWidget(title = it, modifier = Modifier.weight(1f))
                        }
                        repeat(6 - list.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EpisodeWidget(
    title: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        onClick = {},
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .wrapContentSize()
                .padding(8.dp)
        )
    }
}

@Composable
private fun SearchHistoryItem(
    history: String
) {
    ListItem(
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = Icons.Outlined.History.name,
            )
        },
        headlineContent = {
            Text(text = history, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Outlined.ArrowOutward,
                contentDescription = Icons.Outlined.ArrowOutward.name,
            )
        }
    )
}

@androidx.annotation.OptIn(UnstableApi::class)
@Preview
@Composable
private fun SearchScreenPreview() {
    SearchScreen(
        viewModel = SearchViewModel(
            BiliSearchRepository(
                context = LocalContext.current,
                searchRequest = SearchRequest(HttpClientFactory.client)
            )
        ),
        navigateToRoute = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BangumiItemPreview() {
    BangumiItem(
        title = "Fate/Zero 第一季Fate/Zero 第一季Fate/Zero 第一季Fate/Zero 第一季",
        cover = "",
        areas = "日本",
        date = (1317398400).formatDateToYearString(isMill = false),
        styles = "时泪/奇幻/战斗/热血",
        score = 9.6f,
        userCount = (27386).toViewString(),
        episodes = List(8) { "$it" },
        onClick = {}
    )
}

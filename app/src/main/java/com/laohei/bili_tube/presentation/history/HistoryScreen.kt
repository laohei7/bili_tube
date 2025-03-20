package com.laohei.bili_tube.presentation.history

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Cast
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.laohei.bili_sdk.module_v2.history.HistoryItem
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.component.placeholder.NoMoreData
import com.laohei.bili_tube.model.UIModel
import com.laohei.bili_tube.utill.formatDateTimeToString
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.toTimeAgoString2
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel = koinViewModel(),
    navigateToRoute: (Route) -> Unit,
) {
    val histories = historyViewModel.histories.collectAsLazyPagingItems()
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val isLoading = histories.loadState.refresh is LoadState.Loading
    val refreshState = rememberPullToRefreshState()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HistoryTopBar(scrollBehavior = scrollBehavior)
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            val fixedCount = when {
                maxWidth < 500.dp -> 1
                maxWidth >= 500.dp && maxWidth < 800.dp -> 2
                maxWidth >= 800.dp && maxWidth < 1280.dp -> 3
                else -> 4
            }
            PullToRefreshBox(
                isRefreshing = isLoading,
                state = refreshState,
                onRefresh = { histories.refresh() },
                indicator = {
                    Indicator(
                        modifier = Modifier
                            .align(Alignment.TopCenter),
                        isRefreshing = isLoading,
                        state = refreshState,
                    )
                }
            ) {
                val isEmpty = histories.itemCount == 0
                LazyVerticalGrid(
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.background
                    ),
                    columns = GridCells.Fixed(fixedCount),
                    contentPadding = PaddingValues(horizontal = if (fixedCount == 1) 0.dp else 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                    verticalArrangement = Arrangement.spacedBy(
                        if (isEmpty) 16.dp else 0.dp
                    )
                ) {
                    items(histories.itemCount) { index ->
                        histories[index]?.let { item ->
                            when (item) {
                                is UIModel.Header<*> -> {
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = item.header as String,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    )
                                }

                                is UIModel.Item<*> -> {
                                    val it = item.item as HistoryItem
                                    HistoryItem(
                                        cover = it.cover,
                                        title = it.title,
                                        ownerName = it.authorName,
                                        duration = it.duration.formatTimeString(false),
                                        progress = it.progress.toFloat() / it.duration,
                                        viewAt = buildString {
                                            append(it.viewAt.toTimeAgoString2(false))
                                            append(" ")
                                            append(it.viewAt.formatDateTimeToString(false))
                                        },
                                        onClick = {
                                            navigateToRoute.invoke(
                                                Route.Play(
                                                    aid = it.history.oid,
                                                    bvid = it.history.bvid,
                                                    cid = it.history.cid
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                    item(span = { GridItemSpan(fixedCount) }) {
                        NoMoreData(histories.loadState.append)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryTopBar(
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    MediumTopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBackIosNew,
                    contentDescription = Icons.Outlined.ArrowBackIosNew.name,
                )
            }
        },
        title = { Text(text = stringResource(R.string.str_history)) },
        actions = {
            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Outlined.Cast,
                    contentDescription = Icons.Outlined.Cast.name,
                )
            }
            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = Icons.Outlined.Search.name,
                )
            }
            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = Icons.Outlined.MoreVert.name,
                )
            }
        }
    )
}

@Composable
private fun HistoryItem(
    cover: String,
    title: String,
    ownerName: String,
    duration: String,
    progress: Float,
    viewAt: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                onClick.invoke()
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val shape = remember { RoundedCornerShape(12.dp) }
        val coverModifier = Modifier
            .weight(1f)
            .aspectRatio(16 / 9f)
            .clip(shape)

        Box(
            modifier = coverModifier
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(cover)
                    .crossfade(true)
                    .build(),
                contentDescription = title,
                modifier = Modifier
                    .width(180.dp)
                    .aspectRatio(16 / 9f)
                    .clip(shape),
                contentScale = ContentScale.Crop,
            )
            LinearProgressIndicator(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(),
                progress = { progress },
                trackColor = Color.White.copy(alpha = 0.2f),
                color = Color.Red
            )
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 8.dp, end = 8.dp),
                color = Color.Black.copy(alpha = 0.5f),
                contentColor = Color.White,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = duration,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(3.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(1.2f),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(end = 22.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon_up),
                        contentDescription = "up",
                        modifier = Modifier.height(12.dp),
                        colorFilter = ColorFilter.tint(Color.Gray)
                    )

                    Text(
                        text = ownerName,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall
                            .copy(fontSize = 10.sp),
                        color = Color.Gray
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = Icons.Outlined.AccessTime.name,
                        modifier = Modifier
                            .size(16.dp),
                        tint = Color.Gray
                    )

                    Text(
                        text = viewAt,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall
                            .copy(fontSize = 10.sp),
                        color = Color.Gray
                    )
                }
            }

            IconButton(
                onClick = {},
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset {
                        IntOffset(60, -30)
                    }
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = Icons.Outlined.MoreVert.name,
                    modifier = Modifier
                        .size(16.dp)
                )
            }
        }
    }
}
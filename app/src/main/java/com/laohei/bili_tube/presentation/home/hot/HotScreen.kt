package com.laohei.bili_tube.presentation.home.hot

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.laohei.bili_sdk.model.BiliHotVideoItem
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.component.placeholder.NoMoreData
import com.laohei.bili_tube.component.video.VideoMenuSheet
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.toTimeAgoString
import com.laohei.bili_tube.utill.toViewString

private const val TAG = "HotScreen"

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotScreen(
    hotVideos: LazyPagingItems<BiliHotVideoItem>,
    gridState: LazyGridState = rememberLazyGridState(),
    navigateToRoute: (Route) -> Unit
) {
    val refreshState = rememberPullToRefreshState()
    var isShowMenuSheet by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val fixedCount = when {
            maxWidth < 500.dp -> 1
            maxWidth >= 500.dp && maxWidth < 1280.dp -> 2
            else -> 4
        }
        PullToRefreshBox(
            isRefreshing = hotVideos.loadState.refresh is LoadState.Loading,
            state = refreshState,
            onRefresh = { hotVideos.refresh() },
            indicator = {
                Indicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding(),
                    isRefreshing = hotVideos.loadState.refresh is LoadState.Loading,
                    state = refreshState,
                )
            }
        ) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(fixedCount),
                contentPadding = PaddingValues(horizontal = if (fixedCount == 1) 0.dp else 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(span = { GridItemSpan(fixedCount) }) {
                    Spacer(
                        modifier = Modifier
                            .statusBarsPadding()
                            .height(40.dp)
                    )
                }
                items(hotVideos.itemCount) { index ->
                    hotVideos[index]?.let {
                        VideoItem2(
                            key = it.bvid,
                            title = it.title,
                            face = it.owner.face,
                            ownerName = it.owner.name,
                            rcmdReason = it.rcmdReason.content ?: "",
                            duration = it.duration.formatTimeString(false),
                            view = it.stat.view.toViewString(),
                            publishDate = it.pubdate.toTimeAgoString(),
                            onClick = {
                                navigateToRoute(
                                    Route.Play(
                                        aid = it.aid,
                                        bvid = it.bvid,
                                        cid = it.cid,
                                        width = it.dimension.width,
                                        height = it.dimension.height
                                    )
                                )
                            },
                            onMenuClick = {
                                isShowMenuSheet = true
                            }
                        )
                    }
                }
                item(span = { GridItemSpan(fixedCount) }) {
                    NoMoreData(hotVideos.loadState.append)
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

        VideoMenuSheet(
            isShowSheet = isShowMenuSheet,
            onDismiss = { isShowMenuSheet = false }
        )

    }

}

@Composable
private fun VideoItem2(
    key: String,
    title: String,
    face: String,
    ownerName: String,
    rcmdReason: String = "",
    duration: String,
    view: String,
    publishDate: String,
    onClick: () -> Unit,
    onMenuClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.padding(top = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .clickable { onClick.invoke() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(face)
                        .crossfade(true)
                        .build(),
                    contentDescription = key,
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(16 / 9f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Image(
                            painter = painterResource(R.drawable.icon_loading),
                            contentDescription = "loading img",
                        )
                    }
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
                        text = duration, style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(3.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                )

                Surface(
                    color = Color.Transparent,
                    contentColor = if (rcmdReason.isNotBlank()) Color.Red else Color.Transparent,
                    shape = RoundedCornerShape(3.dp),
                    border = BorderStroke(
                        width = 0.5.dp,
                        color = if (rcmdReason.isNotBlank()) Color.Red else Color.Transparent,
                    )
                ) {
                    Text(
                        text = rcmdReason,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.icon_up),
                            contentDescription = "up",
                            modifier = Modifier.height(14.dp),
                            colorFilter = ColorFilter.tint(Color.Red, BlendMode.SrcIn)
                        )

                        Text(
                            text = ownerName,
                            maxLines = 1,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {

                            Icon(
                                imageVector = Icons.Outlined.PlayCircleOutline,
                                contentDescription = Icons.Outlined.PlayCircleOutline.name,
                                modifier = Modifier
                                    .size(16.dp),
                                tint = Color.Red.copy(alpha = 0.5f)
                            )

                            Text(
                                text = "${view}观看 · $publishDate",
                                maxLines = 1,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = Icons.Default.MoreVert.name,
                            modifier = Modifier
                                .size(16.dp)
                                .background(MaterialTheme.colorScheme.background)
                                .clip(CircleShape)
                                .clickable { onMenuClick?.invoke() },
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
        HorizontalDivider(
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier
                .padding(start = 8.dp)
                .padding(top = 4.dp)
        )
    }
}

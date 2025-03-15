package com.laohei.bili_tube.presentation.home.recommend

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.laohei.bili_sdk.model.BiliRandomVideoItem
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.component.placeholder.RecommendPlaceholder
import com.laohei.bili_tube.component.video.VideoItem
import com.laohei.bili_tube.component.video.VideoMenuSheet
import com.laohei.bili_tube.ui.theme.Bili_tubeTheme
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.toTimeAgoString
import com.laohei.bili_tube.utill.toViewString

private const val TAG = "RecommendScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendScreen(
    randomVideos: LazyPagingItems<BiliRandomVideoItem>,
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
            maxWidth >= 500.dp && maxWidth < 800.dp -> 2
            maxWidth >= 800.dp && maxWidth < 1280.dp -> 3
            else -> 4
        }
        PullToRefreshBox(
            isRefreshing = randomVideos.loadState.refresh is LoadState.Loading,
            state = refreshState,
            onRefresh = { randomVideos.refresh() },
            indicator = {
                Indicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding(),
                    isRefreshing = randomVideos.loadState.refresh is LoadState.Loading,
                    state = refreshState,
                )
            }
        ) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(fixedCount),
                contentPadding = PaddingValues(horizontal = if (fixedCount == 1) 0.dp else 8.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                item(span = { GridItemSpan(fixedCount) }) {
                    Spacer(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(top = 12.dp)
                    )
                }
                if (randomVideos.itemCount == 0) {
                    items(20) {
                        RecommendPlaceholder(isSingleLayout = fixedCount == 1)
                    }
                } else {
                    items(randomVideos.itemCount) { index ->
                        randomVideos[index]?.let {
                            VideoItem(
                                isSingleLayout = fixedCount == 1,
                                key = it.bvid,
                                cover = it.pic,
                                title = it.title,
                                face = it.owner.face,
                                ownerName = it.owner.name,
                                view = it.stat.view.toViewString(),
                                date = it.pubdate.toTimeAgoString(),
                                duration = it.duration.formatTimeString(false),
                                onClick = {
                                    navigateToRoute(
                                        Route.Play(
                                            aid = it.id,
                                            bvid = it.bvid,
                                            cid = it.cid,
                                        )
                                    )
                                },
                                onMenuClick = {
                                    isShowMenuSheet = true
                                }
                            )
                        }
                    }
                }
                item(span = { GridItemSpan(fixedCount) }) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        when (randomVideos.loadState.append) {
                            is LoadState.Loading -> CircularProgressIndicator()
                            is LoadState.Error -> Text("加载失败，点击重试")
                            else -> {}
                        }
                    }
                }
                item(span = { GridItemSpan(fixedCount) }) {
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
        }

        VideoMenuSheet(
            isShowSheet = isShowMenuSheet,
            onDismiss = { isShowMenuSheet = false }
        )
    }

}


@SuppressLint("UnusedBoxWithConstraintsScope")
@PreviewScreenSizes
@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun ListGridPreview() {
    Bili_tubeTheme {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val fixedCount = when {
                maxWidth < 500.dp -> 1
                maxWidth >= 500.dp && maxWidth < 800.dp -> 2
                maxWidth >= 800.dp && maxWidth < 1280.dp -> 3
                else -> 4
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(fixedCount),
                contentPadding = PaddingValues(horizontal = if (fixedCount == 1) 0.dp else 8.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                item(span = { GridItemSpan(fixedCount) }) {
                    Text(
                        text = "$maxWidth x $maxHeight",
                        modifier = Modifier.statusBarsPadding()
                    )
                }
                items(20) {
                    VideoItemPreview(
                        isSingleLayout = fixedCount == 1,
                        key = "SSS",
                        cover = R.drawable.bg,
                        title = "红警用一个矿车吸引火力！直接先断他电厂就带走了！",
                        face = R.drawable.bg,
                        ownerName = "红警HBK08\n"
                    )
                }
                item(span = { GridItemSpan(fixedCount) }) {
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
        }
    }

}

@Composable
private fun VideoItemPreview(
    isSingleLayout: Boolean = false,
    key: String,
    @DrawableRes cover: Int,
    title: String,
    @DrawableRes face: Int,
    ownerName: String
) {
    Card(
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Image(
            painter = painterResource(cover),
            contentDescription = key,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
                .clip(RoundedCornerShape(if (isSingleLayout) 0.dp else 12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(start = 12.dp, end = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(face),
                contentDescription = ownerName,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(42.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$ownerName · 5.2万观看 · 1小时前",
                    maxLines = 1,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = Icons.Default.MoreVert.name
                )
            }
        }
    }
}
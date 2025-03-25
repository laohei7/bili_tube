package com.laohei.bili_tube.presentation.home.recommend

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.laohei.bili_sdk.module_v2.folder.FolderSimpleItem
import com.laohei.bili_sdk.module_v2.recomment.RecommendItem
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.component.placeholder.RecommendPlaceholder
import com.laohei.bili_tube.component.video.FolderSheet
import com.laohei.bili_tube.component.video.VideoAction
import com.laohei.bili_tube.component.video.VideoItem
import com.laohei.bili_tube.component.video.VideoMenuSheet
import com.laohei.bili_tube.repository.BiliPlaylistRepository
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.toTimeAgoString
import com.laohei.bili_tube.utill.toViewString
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private const val TAG = "RecommendScreen"

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendScreen(
    randomVideos: LazyPagingItems<RecommendItem>,
    gridState: LazyGridState = rememberLazyGridState(),
    navigateToRoute: (Route) -> Unit,
    onVideoMenuAction:(VideoAction.VideoMenuAction)->Unit
) {
    val scope = rememberCoroutineScope()
    val refreshState = rememberPullToRefreshState()
    var isShowMenuSheet by remember { mutableStateOf(false) }
    var isShowFolderSheet by remember { mutableStateOf(false) }
    var folders by remember { mutableStateOf<List<FolderSimpleItem>>(emptyList()) }
    var currentAid by remember { mutableLongStateOf(0) }
    val playlistRepository = koinInject<BiliPlaylistRepository>()

    fun handleVideoSheetAction(action: VideoAction.VideoSheetAction) {
        when (action) {
            VideoAction.VideoSheetAction.PlaylistAction -> {
                scope.launch {
                    playlistRepository.getFolderSimpleList(currentAid)?.let {
                        folders = it.list
                        isShowMenuSheet = false
                        isShowFolderSheet = true
                    }
                }
            }
        }
    }

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
                verticalArrangement = Arrangement.spacedBy(24.dp),
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
                                face = it.owner?.face ?: "",
                                ownerName = it.owner?.name ?: "",
                                view = it.stat?.view?.toViewString() ?: "",
                                date = it.pubDate.toTimeAgoString(),
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
                                    currentAid = it.id
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
            onDismiss = { isShowMenuSheet = false },
            onClick = ::handleVideoSheetAction
        )

        FolderSheet(
            folders = folders,
            isShowSheet = isShowFolderSheet,
            onDismiss = { isShowFolderSheet = false },
            onClick = {
                val action =it as VideoAction.VideoMenuAction.CollectAction
                onVideoMenuAction.invoke(
                    VideoAction.VideoMenuAction.CollectActionByAid(
                        addAids = action.addAids,
                        delAids = action.delAids,
                        aid = currentAid
                    )
                )
                isShowFolderSheet = false
            }
        )
    }

}
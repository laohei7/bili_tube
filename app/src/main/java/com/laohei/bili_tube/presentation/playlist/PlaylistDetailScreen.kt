package com.laohei.bili_tube.presentation.playlist

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.palette.graphics.Palette
import coil3.asDrawable
import coil3.compose.AsyncImage
import com.laohei.bili_sdk.apis.impl.FolderApiImpl
import com.laohei.bili_sdk.apis.impl.HistoryApiImpl
import com.laohei.bili_sdk.folder.GetFolder
import com.laohei.bili_sdk.history.GetWatchLater
import com.laohei.bili_sdk.module_v2.folder.FolderMediaItem
import com.laohei.bili_sdk.module_v2.video.VideoView
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.PlayParam
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.app.SharedViewModel
import com.laohei.bili_tube.component.appbar.BackTopAppBar
import com.laohei.bili_tube.component.placeholder.NoMoreData
import com.laohei.bili_tube.component.video.HorizontalVideoItem
import com.laohei.bili_tube.core.USERNAME_KEY
import com.laohei.bili_tube.core.util.getValue
import com.laohei.bili_tube.repository.BiliPlaylistRepository
import com.laohei.bili_tube.utill.HttpClientFactory
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.toNonHardwareBitmap
import com.laohei.bili_tube.utill.toTimeAgoString
import com.laohei.bili_tube.utill.toViewString
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun injectPreviewViewModel(param: Route.PlaylistDetail): PlaylistDetailViewModel {
    val context = LocalContext.current
    return PlaylistDetailViewModel(
        BiliPlaylistRepository(
            context = context,
            getFolder = GetFolder(HttpClientFactory.client),
            getWatchLater = GetWatchLater(HttpClientFactory.client),
            historyApi = HistoryApiImpl(HttpClientFactory.client),
            folderApi = FolderApiImpl(HttpClientFactory.client)
        ),
        param = param
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistDetailScreen(
    isPreview: Boolean = false,
    param: Route.PlaylistDetail,
    upPress: () -> Unit = {},
    navigateToRoute: (Route) -> Unit = {}
) {
    val viewModel = if (isPreview) {
        injectPreviewViewModel(param)
    } else {
        koinViewModel<PlaylistDetailViewModel> {
            parametersOf(param)
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            BackTopAppBar(
                backup = upPress,
                search = {
                    navigateToRoute.invoke(Route.Search)
                }
            )
        }
    ) { innerPadding ->
        when {
            param.isToView -> {
                ToViewList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    param = param,
                    toViews = state.toViewList,
                    reorderItem = viewModel::reorderItem,
                    navigateToRoute = navigateToRoute
                )
            }

            else -> {
                val folderResources = state.folderResources.collectAsLazyPagingItems()
                FolderResourceList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    param = param,
                    resources = folderResources,
                    navigateToRoute = navigateToRoute
                )
            }
        }
    }
}

@Composable
private fun FolderResourceList(
    modifier: Modifier = Modifier,
    param: Route.PlaylistDetail,
    resources: LazyPagingItems<FolderMediaItem>,
    navigateToRoute: (Route) -> Unit
) {
    val sharedViewModel = koinInject<SharedViewModel>()
    LazyColumn(
        modifier = modifier,
    ) {
        item(key = "header_info") {
            PlaylistInfoCard(param)
        }
        items(resources.itemCount, { resources[it]!!.bvid }) { index ->
            resources[index]?.let {
                HorizontalVideoItem(
                    cover = it.cover,
                    title = it.title,
                    ownerName = it.upper.name,
                    duration = it.duration.formatTimeString(false),
                    view = it.cntInfo.play.toViewString(),
                    publishDate = it.pubtime.toTimeAgoString(),
                    leadingIcon = null,
                    onClick = {
                        sharedViewModel.setPlayParam(
                            PlayParam.MediaList(
                                mediaKeys = buildList {
                                    repeat(resources.itemCount) { index ->
                                        val item = resources[index]
                                        if (item != null) {
                                            add(Triple(item.id, item.bvid, -1))
                                        }
                                    }
                                },
                                aid = it.id,
                                bvid = it.bvid,
                                cid = -1,
                                title = param.title,
                                count = param.count,
                                isToView = false,
                                fid = param.fid
                            )
                        )
                        navigateToRoute(Route.Play)
                    }
                )
            }
        }
        item(key = "no_more_data") { NoMoreData(resources.loadState.append) }
    }
}

@Composable
private fun ToViewList(
    modifier: Modifier = Modifier,
    param: Route.PlaylistDetail,
    toViews: List<VideoView>,
    reorderItem: (Int, Int) -> Unit,
    navigateToRoute: (Route) -> Unit
) {
    val sharedViewModel = koinInject<SharedViewModel>()
    val reorderableState = rememberReorderableLazyListState(
        onMove = { from, to ->
            reorderItem(from.index - 1, to.index - 1)
        }
    )
    LazyColumn(
        modifier = modifier
            .reorderable(reorderableState)
            .detectReorderAfterLongPress(reorderableState),
        state = reorderableState.listState,
    ) {
        item(key = "header_info") {
            PlaylistInfoCard(param)
        }
        items(toViews, key = { it.bvid }) { item ->
            ReorderableItem(reorderableState, key = item.bvid) { isDragging ->
                val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
                HorizontalVideoItem(
                    modifier = Modifier.shadow(elevation.value),
                    cover = item.pic,
                    title = item.title,
                    ownerName = item.owner.name,
                    duration = item.duration.formatTimeString(false),
                    view = item.stat.view.toViewString(),
                    publishDate = item.pubdate.toTimeAgoString(),
                    onClick = {
                        sharedViewModel.setPlayParam(
                            PlayParam.MediaList(
                                mediaKeys = toViews.fastMap {
                                    Triple(it.aid, it.bvid, it.cid)
                                },
                                aid = item.aid,
                                bvid = item.bvid,
                                cid = item.cid,
                                title = param.title,
                                count = param.count
                            )
                        )
                        navigateToRoute(Route.Play)
                    }
                )
            }
        }
    }
}

@Composable
private fun PlaylistInfoCard(
    param: Route.PlaylistDetail
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var dominantColor by remember { mutableStateOf(Color.LightGray) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .background(dominantColor)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .blur(20.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 64.dp)
                .align(Alignment.Center)
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.background.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            AsyncImage(
                model = param.cover,
                contentDescription = "cover",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                onSuccess = {
                    val drawable = it.result.image.asDrawable(context.resources)
                    scope.launch {
                        drawable.toBitmapOrNull()?.toNonHardwareBitmap()?.let { bitmap ->
                            Palette.from(bitmap).generate { palette ->
                                dominantColor =
                                    palette?.getLightVibrantColor(Color.LightGray.toArgb())?.run {
                                        Color(this)
                                    } ?: Color.LightGray
                            }
                        }
                    }
                },
                placeholder = painterResource(R.drawable.icon_loading),
                error = painterResource(R.drawable.icon_loading),
                contentScale = ContentScale.Crop
            )
            Text(
                text = param.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = context.getValue(USERNAME_KEY.name, stringResource(R.string.str_unknown)),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Row {
                Text(
                    text = stringResource(R.string.str_video_count, param.count),
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = Icons.Outlined.Lock.name,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = when {
                        param.isPrivate -> stringResource(R.string.str_private)
                        else -> stringResource(R.string.str_public)
                    },
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlaylistDetailScreenPreview() {
    PlaylistDetailScreen(
        isPreview = true,
        Route.PlaylistDetail(
            cover = "",
            title = "稍后观看",
            count = 12,
            isPrivate = true
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PlaylistDetailScreenPreview2() {
    PlaylistDetailScreen(
        isPreview = true,
        Route.PlaylistDetail(
            cover = "",
            title = "默认收藏夹",
            count = 90,
            isPrivate = false,
            isToView = false
        )
    )
}
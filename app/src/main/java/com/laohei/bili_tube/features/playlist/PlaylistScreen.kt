package com.laohei.bili_tube.features.playlist

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Cast
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.palette.graphics.Palette
import coil3.asDrawable
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_sdk.module_v2.folder.FolderModel
import com.laohei.bili_tube.R
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.utill.toNonHardwareBitmap
import com.laohei.bili_tube.utill.toViewString
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    viewModel: PlaylistViewModel = koinViewModel(),
    navigateToAppRoute: (AppRoute) -> Unit,
    upPress: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val refreshState = rememberPullToRefreshState()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PlaylistTopBar(
                scrollBehavior = scrollBehavior,
                upPress = upPress
            )
        }
    ) { innerPadding ->
        AdaptiveLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) { uiType, width, height ->
            val fixedCount = when {
                maxWidth < 500.dp -> 1
                maxWidth >= 500.dp &&  maxWidth < 1280.dp -> 2
                else -> 3
            }
            PullToRefreshBox(
                modifier = Modifier.fillMaxWidth(),
                isRefreshing = state.isLoading,
                state = refreshState,
                onRefresh = { scope.launch { viewModel.refresh() } },
                indicator = {
                    Indicator(
                        modifier = Modifier
                            .align(Alignment.TopCenter),
                        isRefreshing = state.isLoading,
                        state = refreshState,
                    )
                }
            ) {
                val isEmpty = state.folderList.isEmpty()
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
                    getPlaylistWidget(
                        folderList = state.folderList,
                        watchLaterCover = state.watchLaterCover,
                        navigateToAppRoute = navigateToAppRoute
                    )
                }
            }
        }
    }
}


private fun LazyGridScope.getPlaylistWidget(
    folderList: List<FolderModel>,
    watchLaterCover: String,
    navigateToAppRoute: (AppRoute) -> Unit,
) {
    folderList.fastForEach { folder ->
        when (folder.id) {
            2 -> {
                item {
                    PlaylistItem(
                        cover = watchLaterCover,
                        title = folder.name,
                        label = stringResource(R.string.str_private),
                        onClick = {
                            navigateToAppRoute(
                                AppRoute.PlaylistDetail(
                                    cover = watchLaterCover,
                                    title = folder.name,
                                    count = folder.mediaListResponse.count,
                                    isPrivate = true
                                )
                            )
                        },
                        icon = {
                            Column(
                                modifier = Modifier
                                    .width(180.dp)
                                    .aspectRatio(16 / 9f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        color = Color.Black.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                verticalArrangement = Arrangement.spacedBy(
                                    4.dp,
                                    Alignment.CenterVertically
                                ),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.WatchLater,
                                    contentDescription = Icons.Outlined.WatchLater.name,
                                    tint = Color.White
                                )

                                Text(
                                    text = folder.mediaListResponse.count.toViewString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
                    )
                }
            }

            1 -> {
                folder.mediaListResponse.list?.let {
                    items(it) { item ->
                        PlaylistItem(
                            cover = item.cover,
                            title = item.title,
                            label = if (item.attr == 23) {
                                stringResource(R.string.str_private)
                            } else {
                                stringResource(R.string.str_public)
                            },
                            onClick = {
                                navigateToAppRoute(
                                    AppRoute.PlaylistDetail(
                                        cover = item.cover,
                                        title = item.title,
                                        count = item.mediaCount,
                                        isPrivate = item.attr == 23,
                                        isToView = false,
                                        fid = item.id
                                    )
                                )
                            },
                            icon = {
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(end = 12.dp, bottom = 12.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            color = Color.Black.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(vertical = 3.dp, horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.PlaylistPlay,
                                        contentDescription = Icons.AutoMirrored.Outlined.PlaylistPlay.name,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = item.mediaCount.toViewString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistTopBar(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    upPress:()-> Unit
) {
    MediumTopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(
                onClick = upPress
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = Icons.AutoMirrored.Outlined.ArrowBack.name,
                )
            }
        },
        title = { Text(text = stringResource(R.string.str_playlist)) },
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
private fun PlaylistItem(
    isDark: Boolean = isSystemInDarkTheme(),
    cover: String,
    title: String,
    label: String,
    onClick: () -> Unit,
    icon: @Composable (BoxScope.() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var dominantColor by remember { mutableStateOf(Color.LightGray) }
    val coverRequest = remember(cover) {
        ImageRequest.Builder(context)
            .data(cover)
            .crossfade(true)
            .size(1280, 720)
            .placeholder(R.drawable.icon_loading_16_9)
            .error(R.drawable.icon_loading_16_9)
            .build()
    }
    Row(
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                onClick.invoke()
            }
            .padding(horizontal = 16.dp)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box {
            val shape = remember { RoundedCornerShape(12.dp) }
            val coverModifier = Modifier
                .width(180.dp)
                .aspectRatio(16 / 9f)
                .clip(shape)
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(0, -30)
                    }
                    .graphicsLayer {
                        scaleY = 0.9f
                        scaleX = 0.9f
                    }
                    .then(coverModifier)
                    .background(
                        color = dominantColor,
                        shape = shape
                    ),
            )

            AsyncImage(
                model = coverRequest,
                contentDescription = "",
                onSuccess = {
                    val drawable = it.result.image.asDrawable(context.resources)
                    scope.launch {
                        drawable.toBitmapOrNull()?.toNonHardwareBitmap()?.let { bitmap ->
                            Palette.from(bitmap).generate { palette ->
                                dominantColor = if (isDark) {
                                    palette?.getLightMutedColor(Color.LightGray.toArgb())?.run {
                                        Color(this)
                                    } ?: Color.LightGray
                                } else {
                                    palette?.getDominantColor(Color.LightGray.toArgb())?.run {
                                        Color(this)
                                    } ?: Color.LightGray
                                }
                            }
                        }
                    }
                },
                modifier = coverModifier
                    .background(
                        color = Color.LightGray
                    )
                    .border(
                        border = BorderStroke(
                            color = Color.White,
                            width = 1.dp
                        ),
                        shape = shape
                    ),
                contentScale = ContentScale.Crop
            )
            icon?.invoke(this)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = {},
                modifier = Modifier.offset {
                    IntOffset(60, -30)
                }) {
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
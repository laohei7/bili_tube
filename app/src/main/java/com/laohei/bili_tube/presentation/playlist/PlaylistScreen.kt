package com.laohei.bili_tube.presentation.playlist

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Cast
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.laohei.bili_tube.R
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.random.Random

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    viewModel: PlaylistViewModel = koinViewModel()
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
                scrollBehavior = scrollBehavior
            )
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
                    state.folderList.fastForEach { folder ->
                        when (folder.id) {
                            2 -> {
                                item {
                                    PlaylistItem(
                                        cover = state.watchLaterCover,
                                        title = folder.name,
                                        label = stringResource(R.string.str_private)
                                    )
                                }
                            }

                            1 -> {
                                folder.mediaListResponse.list?.let {
                                    items(it) {
                                        PlaylistItem(
                                            cover = it.cover,
                                            title = it.title,
                                            label = stringResource(R.string.str_public),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistTopBar(
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
    cover: String,
    title: String,
    label: String,
    icon: @Composable (BoxScope.() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .background(MaterialTheme.colorScheme.background)
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
                        color = Color(Random.nextInt(80,220),Random.nextInt(80,220),Random.nextInt(80,220)),
                        shape = shape
                    ),
            )

            AsyncImage(
                model = if (cover.isBlank()) R.drawable.icon_loading else cover,
                contentDescription = "",
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
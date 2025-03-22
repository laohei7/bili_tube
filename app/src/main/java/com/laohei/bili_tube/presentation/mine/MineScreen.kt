package com.laohei.bili_tube.presentation.mine

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Cast
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.palette.graphics.Palette
import coil3.asDrawable
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.laohei.bili_sdk.module_v2.folder.FolderItem
import com.laohei.bili_sdk.module_v2.history.HistoryItem
import com.laohei.bili_sdk.module_v2.video.VideoView
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.core.FACE_URL_KEY
import com.laohei.bili_tube.core.USERNAME_KEY
import com.laohei.bili_tube.core.util.getValue
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.toNonHardwareBitmap
import com.laohei.bili_tube.utill.toViewString
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun MineScreen(
    viewModel: MineViewModel = koinViewModel(),
    navigateToRoute: (Route) -> Unit = {}
) {
    val state by viewModel.mineState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            MineTopBar()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(
                    state = rememberScrollState()
                )
        ) {
            AvatarWidget()

            UserDataWidget(
                dynamicCount = state.dynamicCount,
                follower = state.follower,
                following = state.following
            )
            HistoryWidget(
                histories = state.historyList,
                navigateToRoute = navigateToRoute
            )
            Spacer(Modifier.height(12.dp))
            PlaylistWidget(
                watchLaterList = state.watchLaterList,
                watchLaterCount = state.watchLaterCount,
                folderList = state.folderList,
                navigateToRoute = navigateToRoute
            )
            Spacer(Modifier.height(12.dp))
            OtherWidget()

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ColumnScope.AvatarWidget() {
    val context = LocalContext.current
    var avatar by remember { mutableStateOf(context.getValue(FACE_URL_KEY.name, "")) }
    var username by remember { mutableStateOf(context.getValue(USERNAME_KEY.name, "")) }
    ListItem(
        leadingContent = {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(avatar)
                    .crossfade(true)
                    .build(),
                contentDescription = "avatar",
                modifier = Modifier
                    .size(88.dp)
                    .aspectRatio(1f)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = {
                    Image(
                        painter = painterResource(R.drawable.icon_loading),
                        contentDescription = "loading img",
                    )
                }
            )
        },
        headlineContent = {
            Text(
                text = username, style = MaterialTheme.typography.titleLarge
                    .copy(
                        fontSize = 26.sp
                    ),
                fontWeight = FontWeight.Bold
            )
        },
        supportingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = Color.Transparent,
                    contentColor = Color.Red,
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color.Red,
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "正式会员",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Box(
                    Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(Color.Red, CircleShape)
                )
                Text(
                    text = "查看频道 >",
                    style = MaterialTheme.typography.labelSmall,
                )

            }
        }
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .horizontalScroll(
                state = rememberScrollState()
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AssistChip(
            onClick = {},
            shape = CircleShape,
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            border = AssistChipDefaults.assistChipBorder(
                enabled = true,
                borderWidth = 0.dp,
                borderColor = Color.Transparent
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = Icons.Outlined.AccountCircle.name,
                )
            },
            label = {
                Text(text = "切换账号")

            }
        )

        AssistChip(
            onClick = {},
            shape = CircleShape,
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            border = AssistChipDefaults.assistChipBorder(
                enabled = true,
                borderWidth = 0.dp,
                borderColor = Color.Transparent
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = Icons.Outlined.Share.name,
                )
            },
            label = {
                Text(text = "分享频道")

            }
        )

    }
}

@Composable
private fun ColumnScope.UserDataWidget(
    following: Int,
    follower: Int,
    dynamicCount: Int
) {
    ListItem(
        headlineContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = dynamicCount.toViewString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(R.string.str_dynamic),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = following.toViewString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(R.string.str_following),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = follower.toViewString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(R.string.str_follower),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
    )
}

@Composable
private fun ColumnScope.HistoryWidget(
    histories: List<HistoryItem>,
    navigateToRoute: (Route) -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = "历史记录",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        trailingContent = {
            TextButton(onClick = { navigateToRoute.invoke(Route.History) }) {
                Text(text = "查看全部")
            }
        }
    )
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(Modifier) }
        items(histories) {
            HistoryItem(
                cover = it.cover,
                title = it.title,
                ownerName = it.authorName,
                duration = it.duration.formatTimeString(false),
                progress = it.progress.toFloat() / it.duration,
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
        item { Spacer(Modifier) }
    }
}

@Composable
private fun ColumnScope.PlaylistWidget(
    watchLaterList: List<VideoView>,
    watchLaterCount: Int = 0,
    folderList: List<FolderItem>,
    navigateToRoute: (Route) -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = "播放列表",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        trailingContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = Icons.Outlined.Add.name,
                    )
                }
                TextButton(onClick = {
                    navigateToRoute.invoke(Route.Playlist)
                }) {
                    Text(text = "查看全部")
                }
            }
        }
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(Modifier) }
        item {
            PlaylistItem(
                cover = watchLaterList.firstOrNull()?.pic.orEmpty(),
                title = stringResource(R.string.str_watch_later),
                label = stringResource(R.string.str_private),
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
                            text = "$watchLaterCount",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            )
        }
        items(folderList) {
            PlaylistItem(
                cover = it.cover,
                title = it.title,
                label = stringResource(R.string.str_public),
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
                            text = it.mediaCount.toViewString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            )
        }
        item { Spacer(Modifier) }
    }
}

@Composable
private fun OtherWidget() {
    ListItem(
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.VideoLibrary,
                contentDescription = Icons.Outlined.VideoLibrary.name,
            )
        },
        headlineContent = {
            Text(text = stringResource(R.string.str_manuscript_management))
        }
    )
    HorizontalDivider(color = Color.LightGray)
    ListItem(
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.Download,
                contentDescription = Icons.Outlined.Download.name,
            )
        },
        headlineContent = {
            Text(text = stringResource(R.string.str_downlaod_management))
        }
    )
    HorizontalDivider(color = Color.LightGray)
    ListItem(
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.Feedback,
                contentDescription = Icons.Outlined.Feedback.name,
            )
        },
        headlineContent = {
            Text(text = stringResource(R.string.str_feedback))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MineTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(40.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Outlined.Cast,
                contentDescription = Icons.Outlined.Cast.name,
            )
        }
        IconButton(onClick = {}) {
            BadgedBox(
                badge = {
                    Badge(
                        modifier = Modifier.offset {
                            IntOffset(-8, 0)
                        }
                    ) {
                        Text(text = "9+")
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = Icons.Outlined.Notifications.name,
                )
            }
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = Icons.Outlined.Search.name,
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = Icons.Outlined.Settings.name,
            )
        }
    }
}

@Composable
private fun PlaylistItem(
    cover: String,
    title: String,
    label: String,
    icon: @Composable (BoxScope.() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var dominantColor by remember { mutableStateOf(Color.LightGray) }
    Column(
        modifier = Modifier.width(IntrinsicSize.Min),
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                model = if (cover.isBlank()) R.drawable.icon_loading else cover,
                contentDescription = "",
                onSuccess = {
                    val drawable = it.result.image.asDrawable(context.resources)
                    scope.launch {
                        drawable.toBitmapOrNull()?.toNonHardwareBitmap()?.let {
                            Palette.from(it).generate { palette ->
                                dominantColor =
                                    palette?.getDominantColor(Color.LightGray.toArgb())?.run {
                                        Color(this)
                                    } ?: Color.LightGray
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

@Composable
private fun HistoryItem(
    cover: String,
    title: String,
    ownerName: String,
    duration: String,
    progress: Float,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .clickable { onClick.invoke() },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val shape = remember { RoundedCornerShape(12.dp) }
        val coverModifier = Modifier
            .width(180.dp)
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
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(end = 22.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = ownerName,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            IconButton(
                onClick = {},
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset {
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
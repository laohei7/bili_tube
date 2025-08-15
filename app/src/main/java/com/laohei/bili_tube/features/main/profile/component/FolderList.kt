package com.laohei.bili_tube.features.main.profile.component

import android.util.Log
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.palette.graphics.Palette
import coil3.asDrawable
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_sdk.module_v2.folder.FolderItem
import com.laohei.bili_sdk.module_v2.video.VideoView
import com.laohei.bili_tube.R
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.utill.toNonHardwareBitmap
import com.laohei.bili_tube.utill.toViewString
import kotlinx.coroutines.launch


@Composable
fun FolderList(
    watchLaterList: List<VideoView>,
    watchLaterCount: Int = 0,
    folderList: List<FolderItem>,
    navigateToAppRoute: (AppRoute) -> Unit,
    showCreatedFolder: () -> Unit
) {
    val context = LocalContext.current
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.str_playlist),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        trailingContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showCreatedFolder.invoke() }) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = Icons.Outlined.Add.name,
                    )
                }
                TextButton(onClick = {
                    navigateToAppRoute(AppRoute.Playlist)
                }) {
                    Text(text = stringResource(R.string.str_see_all))
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
        if (folderList.isNotEmpty()) {
            item {
                PlaylistItem(
                    cover = watchLaterList.firstOrNull()?.pic.orEmpty(),
                    title = stringResource(R.string.str_watch_later),
                    label = stringResource(R.string.str_private),
                    onClick = {
                        navigateToAppRoute(
                            AppRoute.PlaylistDetail(
                                cover = watchLaterList.firstOrNull()?.pic.orEmpty(),
                                title = context.getString(R.string.str_watch_later),
                                count = watchLaterCount,
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
                                text = "$watchLaterCount",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                )
            }
            items(folderList) {
                Log.d("TAG", "PlaylistWidget: $it")
                PlaylistItem(
                    cover = it.cover,
                    title = it.title,
                    label = if (it.attr == 23) {
                        stringResource(R.string.str_private)
                    } else {
                        stringResource(R.string.str_public)
                    },
                    onClick = {
                        navigateToAppRoute(
                            AppRoute.PlaylistDetail(
                                cover = it.cover,
                                title = it.title,
                                count = it.mediaCount,
                                isPrivate = false,
                                isToView = false,
                                fid = it.id
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
                                text = it.mediaCount.toViewString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                )
            }
        }
        item { Spacer(Modifier) }
    }
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
    Column(
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .clickable {
                onClick.invoke()
            },
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
                    .background(color = Color.LightGray)
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

package com.laohei.bili_tube.features.main.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.WatchLater
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.palette.graphics.Palette
import coil3.asDrawable
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_sdk.model_v2.folder.FolderItem
import com.laohei.bili_sdk.model_v2.video.VideoView
import com.laohei.bili_tube.R
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.component.chip.IconWithTextTag
import com.laohei.bili_tube.ui.component.widget.HeaderWithMenu
import com.laohei.bili_tube.ui.theme.CornerRadiusMd
import com.laohei.bili_tube.ui.theme.CornerRadiusNone
import com.laohei.bili_tube.ui.theme.CornerRadiusSm
import com.laohei.bili_tube.ui.theme.CornerRadiusXs
import com.laohei.bili_tube.ui.theme.PaddingLg
import com.laohei.bili_tube.ui.theme.PaddingMd
import com.laohei.bili_tube.ui.theme.PaddingSm
import com.laohei.bili_tube.ui.theme.PaddingXs
import com.laohei.bili_tube.ui.util.toNonHardwareBitmap
import com.laohei.bili_tube.util.toViewString
import kotlinx.coroutines.launch


@Composable
fun FolderList(
    watchLaterList: List<VideoView>,
    watchLaterCount: Int = 0,
    folderList: List<FolderItem>,
    navigateToAppRoute: (AppRoute) -> Unit,
    showCreatedFolder: () -> Unit,
    onMoreClick: (Long?) -> Unit,
) {
    if (folderList.isEmpty() && watchLaterList.isEmpty()) return
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
                horizontalArrangement = Arrangement.spacedBy(PaddingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showCreatedFolder.invoke() }) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = Icons.Rounded.Add.name,
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
        horizontalArrangement = Arrangement.spacedBy(PaddingLg)
    ) {
        item { Spacer(Modifier) }
        item {
            FolderCard(
                modifier = Modifier
                    .width(180.dp)
                    .clip(RoundedCornerShape(CornerRadiusMd)),
                cover = watchLaterList.firstOrNull()?.pic.orEmpty(),
                title = stringResource(R.string.str_watch_later),
                label = stringResource(R.string.str_private),
                isTrailingVisible = false,
                onClick = {
                    navigateToAppRoute(
                        AppRoute.PlaylistContent(
                            cover = watchLaterList.firstOrNull()?.pic.orEmpty(),
                            title = context.getString(R.string.str_watch_later),
                            count = watchLaterCount,
                            isPrivate = true
                        )
                    )
                },
                icon = {
                    StatCard(
                        modifier = Modifier
                            .padding(top = PaddingMd)
                            .width(180.dp)
                            .aspectRatio(16 / 9f)
                            .clip(RoundedCornerShape(CornerRadiusMd))
                            .background(
                                color = Color.Black.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(CornerRadiusMd)
                            ),
                        label = watchLaterCount.toViewString(),
                        icon = Icons.Rounded.WatchLater
                    )
                },
                onMoreClick = { onMoreClick(null) }
            )
        }
        items(folderList) {
            FolderCard(
                modifier = Modifier
                    .width(180.dp)
                    .clip(RoundedCornerShape(CornerRadiusMd)),
                cover = it.cover,
                title = it.title,
                label = if (it.attr == 23) {
                    stringResource(R.string.str_private)
                } else {
                    stringResource(R.string.str_public)
                },
                onClick = {
                    navigateToAppRoute(
                        AppRoute.PlaylistContent(
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
                    IconWithTextTag(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(PaddingMd)
                            .clip(RoundedCornerShape(CornerRadiusXs))
                            .background(
                                color = Color.Black.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(CornerRadiusXs)
                            )
                            .padding(vertical = PaddingXs, horizontal = PaddingSm),
                        icon = Icons.AutoMirrored.Rounded.PlaylistPlay,
                        label = it.mediaCount.toViewString(),
                    )
                },
                onMoreClick = { onMoreClick(it.id) }
            )
        }
        item { Spacer(Modifier) }
    }
}


@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    contentColor: Color = Color.White
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(PaddingXs, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = icon.name,
            tint = contentColor
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}

@Composable
private fun FolderCard(
    modifier: Modifier = Modifier,
    cover: String,
    title: String,
    label: String,
    isTrailingVisible: Boolean = true,
    onClick: () -> Unit,
    icon: @Composable (BoxScope.() -> Unit)? = null,
    onMoreClick: () -> Unit,
) {
    val isDark = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var dominantColor by remember { mutableStateOf(Color.LightGray) }
    val coverRequest = remember(cover) {
        ImageRequest.Builder(context)
            .data(cover)
            .crossfade(true)
            .placeholder(R.drawable.icon_loading_375_211)
            .error(R.drawable.icon_loading_375_211)
            .build()
    }
    Column(
        modifier = modifier
            .clickable { onClick.invoke() },
        verticalArrangement = Arrangement.spacedBy(PaddingSm)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleY = 0.9f
                        scaleX = 0.9f
                    }
                    .then(modifier)
                    .aspectRatio(16 / 9f)
                    .background(color = dominantColor),
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = PaddingXs * 3 / 2)
                        .clip(RoundedCornerShape(CornerRadiusNone)),
                    color = MaterialTheme.colorScheme.background,
                    thickness = PaddingXs
                )
            }

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
                modifier = Modifier
                    .padding(top = PaddingMd)
                    .then(modifier)
                    .aspectRatio(16 / 9f)
                    .background(color = Color.LightGray),
                contentScale = ContentScale.Crop
            )
            icon?.invoke(this)
        }
        HeaderWithMenu(
            title = title,
            subtitle = label,
            isTrailingVisible = isTrailingVisible,
            onMoreClick = onMoreClick
        )
    }

}

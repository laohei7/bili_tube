package com.laohei.bili_tube.features.main.profile.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.laohei.bili_sdk.module_v2.history.HistoryItem
import com.laohei.bili_tube.model.play.PlayParam
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.viewmodel.SharedViewModel
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.util.toTimeString
import org.koin.compose.koinInject


@Composable
fun ShortHistoryList(
    histories: List<HistoryItem>,
    navigateToAppRoute: (AppRoute) -> Unit
) {
    val sharedViewModel = koinInject<SharedViewModel>()
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.str_history),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        trailingContent = {
            TextButton(onClick = { navigateToAppRoute(AppRoute.History) }) {
                Text(text = stringResource(R.string.str_see_all))
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
            val progress = when {
                it.duration > 0L -> (it.progress.toFloat() / it.duration)
                    .coerceIn(0f, 1f)

                else -> 0f
            }
            HistoryItem(
                cover = it.cover,
                title = it.title,
                ownerName = it.authorName,
                duration = it.duration.toTimeString(false),
                progress = progress,
                onClick = {
                    sharedViewModel.setPlayParam(
                        PlayParam.VideoParam(
                            aid = it.history.oid,
                            bvid = it.history.bvid,
                            cid = it.history.cid
                        )
                    )
                    navigateToAppRoute(AppRoute.Play)
                }
            )
        }
        item { Spacer(Modifier) }
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
        val context = LocalContext.current
        val shape = remember { RoundedCornerShape(12.dp) }
        val coverModifier = Modifier
            .width(180.dp)
            .aspectRatio(16 / 9f)
            .clip(shape)
        val coverRequest = remember(cover) {
            ImageRequest.Builder(context)
                .data(cover)
                .crossfade(true)
                .size(1280,720)
                .build()
        }
        Box(
            modifier = coverModifier
        ) {
            AsyncImage(
                model = coverRequest,
                contentDescription = title,
                modifier = Modifier
                    .width(180.dp)
                    .aspectRatio(16 / 9f)
                    .clip(shape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.icon_loading_16_9),
                error = painterResource(R.drawable.icon_loading_16_9)
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
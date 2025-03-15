package com.laohei.bili_tube.presentation.player.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.laohei.bili_sdk.module_v2.video.VideoView
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.utill.formatTimeString

@Composable
internal fun RelatedHorizontalList(
    modifier: Modifier = Modifier,
    related: List<VideoView>,
    onClick: (Route.Play) -> Unit
) {
    LazyRow(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item { Spacer(Modifier) }
        items(related) {
            HorizontalRelatedItem(
                cover = it.pic,
                title = it.title,
                duration = it.duration.formatTimeString(false),
                author = it.owner.name,
                onClick = {
                    onClick.invoke(
                        Route.Play(
                            aid = it.aid,
                            bvid = it.bvid,
                            cid = it.cid,
                            width = it.dimension.width,
                            height = it.dimension.height
                        )
                    )
                }
            )
        }
        item { Spacer(Modifier) }
    }
}

@Composable
private fun HorizontalRelatedItem(
    cover: String,
    duration: String,
    title: String,
    author: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .clickable {
                onClick.invoke()
            }
    ) {
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(cover)
                    .crossfade(true)
                    .build(),
                contentDescription = title,
                modifier = Modifier
                    .width(200.dp)
                    .aspectRatio(16 / 9f)
                    .paint(
                        painter = painterResource(R.drawable.icon_loading),
                        contentScale = ContentScale.Crop
                    )
                    .border(
                        1.dp,
                        Color.LightGray,
                        RoundedCornerShape(12.dp)
                    )
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
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

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            color = Color.White,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = author,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            color = Color.LightGray,
            overflow = TextOverflow.Ellipsis
        )

    }
}
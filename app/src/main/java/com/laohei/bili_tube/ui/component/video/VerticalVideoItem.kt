package com.laohei.bili_tube.ui.component.video

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.theme.ExtremeSmallPadding
import com.laohei.bili_tube.ui.theme.LargePadding
import com.laohei.bili_tube.ui.theme.MediumPadding
import com.laohei.bili_tube.ui.theme.SmallPadding

@Composable
fun VerticalVideoItem(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    bvid: String,
    cover: String,
    coverAspectRatio: Float = 16f / 9f,
    coverShape: Shape = RoundedCornerShape(0.dp),
    title: String,
    ownerFace: String,
    ownerName: String,
    view: String,
    pubDate: String,
    duration: String,
    trailingIcon: ImageVector? = null,
    onClick: () -> Unit,
    onTrailingClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
    ) {
        Box {
            val coverPainter = rememberAsyncImagePainter(
                ImageRequest.Builder(context)
                    .data(cover)
                    .crossfade(true)
                    .size(1280, 720)
                    .placeholder(R.drawable.icon_loading_16_9)
                    .error(R.drawable.icon_loading_16_9)
                    .build()
            )

            Image(
                painter = coverPainter,
                contentDescription = bvid,
                modifier = modifier
                    .fillMaxWidth()
                    .aspectRatio(coverAspectRatio)
                    .clip(coverShape),
                contentScale = ContentScale.Crop,
            )

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = SmallPadding)
                    .padding(end = SmallPadding),
                color = Color.Black.copy(alpha = 0.5f),
                contentColor = Color.White,
                shape = MaterialTheme.shapes.extraSmall
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = SmallPadding)
                .padding(start = LargePadding)
                .padding(end = MediumPadding),
        ) {
            val facePainter = rememberAsyncImagePainter(
                ImageRequest.Builder(context)
                    .data(ownerFace)
                    .crossfade(true)
                    .placeholder(R.drawable.icon_loading_1_1)
                    .error(R.drawable.icon_loading_1_1)
                    .build()
            )
            Image(
                painter = facePainter,
                contentDescription = ownerName,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = SmallPadding)
            ) {
                Text(
                    text = title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.str_author_view_date, ownerName, view, pubDate),
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )

            }
            trailingIcon?.let {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable { onTrailingClick() }
                        .background(MaterialTheme.colorScheme.background)
                        .padding(ExtremeSmallPadding),
                    imageVector = it,
                    contentDescription = it.name,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VerticalVideoItemPreview() {
    VerticalVideoItem(
        bvid = "",
        cover = "",
        title = "不要抢走我的整活啊！2025年1月新番完结吐槽！【泛式】",
        ownerFace = "",
        ownerName = "泛式",
        duration = "05:20",
        view = "144.03万",
        pubDate = "23小时前",
        onClick = {},
        onTrailingClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun VerticalVideoItemTrailingPreview() {
    VerticalVideoItem(
        bvid = "",
        cover = "",
        title = "不要抢走我的整活啊！2025年1月新番完结吐槽！【泛式】",
        ownerFace = "",
        ownerName = "泛式",
        duration = "05:20",
        view = "144.03万",
        pubDate = "23小时前",
        trailingIcon = Icons.Outlined.MoreVert,
        onClick = {},
        onTrailingClick = {}
    )
}
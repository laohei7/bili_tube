package com.laohei.bili_tube.ui.component.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.component.widget.VideoCoverOverlay
import com.laohei.bili_tube.ui.preview.FakeVerticalVideoCardData
import com.laohei.bili_tube.ui.theme.CornerRadiusNone
import com.laohei.bili_tube.ui.theme.PaddingLg
import com.laohei.bili_tube.ui.theme.PaddingMd
import com.laohei.bili_tube.ui.theme.PaddingSm
import com.laohei.bili_tube.ui.theme.PaddingXs

@Composable
fun VerticalVideoCard(
    modifier: Modifier = Modifier,
    bvid: String,
    coverUrl: String,
    title: String,
    ownerFaceUrl: String,
    ownerName: String,
    viewCount: String,
    publishDate: String,
    duration: String,
    coverAspectRatio: Float = 16f / 9f,
    coverShape: Shape = RoundedCornerShape(CornerRadiusNone),
    trailingIcon: ImageVector? = null,
    onClick: () -> Unit,
    onTrailingClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
    ) {
        VideoCoverOverlay(
            modifier = Modifier
                .fillMaxWidth(),
            contentDescription = bvid,
            coverAspectRatio = coverAspectRatio,
            coverShape = coverShape,
            coverUrl = coverUrl,
            duration = duration
        )
        VideoMetadataRow(
            title = title,
            ownerName = ownerName,
            ownerFaceUrl = ownerFaceUrl,
            viewCount = viewCount,
            publishDate = publishDate,
            trailingIcon = trailingIcon,
            onTrailingClick = onTrailingClick
        )
    }
}

@Composable
private fun VideoMetadataRow(
    modifier: Modifier = Modifier,
    title: String,
    ownerName: String,
    ownerFaceUrl: String,
    viewCount: String,
    publishDate: String,
    trailingIcon: ImageVector? = null,
    onTrailingClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = PaddingSm)
            .padding(start = PaddingLg)
            .padding(end = PaddingMd),
    ) {
        val facePainter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(ownerFaceUrl)
                .crossfade(true)
                .placeholder(R.drawable.icon_loading_84)
                .error(R.drawable.icon_loading_84)
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
                .padding(start = PaddingSm)
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
                text = stringResource(
                    R.string.str_author_view_date,
                    ownerName,
                    viewCount,
                    publishDate
                ),
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
                    .padding(PaddingXs),
                imageVector = it,
                contentDescription = it.name,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun VerticalVideoCardPreview(
    @PreviewParameter(FakeVerticalVideoCardData::class) icon: ImageVector?
) {
    VerticalVideoCard(
        bvid = "",
        coverUrl = "",
        title = "不要抢走我的整活啊！2025年1月新番完结吐槽！【泛式】",
        ownerFaceUrl = "",
        ownerName = "泛式",
        duration = "05:20",
        viewCount = "144.03万",
        publishDate = "23小时前",
        trailingIcon = icon,
        onClick = {},
        onTrailingClick = {}
    )
}

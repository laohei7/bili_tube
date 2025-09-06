package com.laohei.bili_tube.ui.component.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.component.animation.lottie.AnimatedPlayingIcon
import com.laohei.bili_tube.ui.component.chip.RecommendationTag
import com.laohei.bili_tube.ui.component.chip.UpTag
import com.laohei.bili_tube.ui.component.chip.VideoDurationTag
import com.laohei.bili_tube.ui.component.widget.IconWithText
import com.laohei.bili_tube.ui.component.widget.ViewAndDateLabel
import com.laohei.bili_tube.ui.component.widget.ViewAtLabel
import com.laohei.bili_tube.ui.theme.CornerRadiusMd
import com.laohei.bili_tube.ui.theme.CornerRadiusXs
import com.laohei.bili_tube.ui.theme.PaddingMd
import com.laohei.bili_tube.ui.theme.PaddingNone
import com.laohei.bili_tube.ui.theme.PaddingSm
import com.laohei.bili_tube.ui.theme.PaddingXs
import com.laohei.bili_tube.util.toViewString


@Composable
fun HorizontalVideoCard(
    modifier: Modifier = Modifier,
    coverUrl: String,
    title: String,
    ownerName: String,
    progress: Float? = null,
    isPlaying: Boolean = false,
    duration: String? = null,
    viewAt: String? = null,
    recommendation: String? = null,
    viewCount: String? = null,
    publishDate: String? = null,
    onClick: (() -> Unit)? = null,
    onMoreClick: (() -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val coverRequest = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(coverUrl)
            .crossfade(true)
            .placeholder(R.drawable.icon_loading_375_211)
            .error(R.drawable.icon_loading_375_211)
            .build()
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick.invoke() }
                } else {
                    Modifier
                }
            )
            .padding(end = PaddingSm)
            .padding(start = if (leadingIcon == null) PaddingSm else PaddingNone),
    ) {
        leadingIcon?.let {
            Box(Modifier.align(Alignment.CenterVertically)) { it.invoke() }
            Spacer(Modifier.width(4.dp))
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(16 / 9f)
                .clip(RoundedCornerShape(PaddingSm))
        ) {
            Image(
                painter = coverRequest,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentScale = ContentScale.Crop,
            )
            progress?.let {
                LinearProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(),
                    progress = { it },
                    trackColor = Color.White.copy(alpha = 0.2f),
                    color = Color.Red
                )
            }
            duration?.let {
                VideoDurationTag(
                    duration = duration,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 8.dp, end = 8.dp)
                )
            }
            if (isPlaying) {
                AnimatedPlayingIcon(Modifier.align(Alignment.Center))
            }
        }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1.2f),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(end = 22.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    minLines = 2,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                recommendation?.let { RecommendationTag(it) }
                if (ownerName.isNotBlank()) {
                    UpTag(ownerName)
                }
                viewAt?.let { ViewAtLabel(it) }
                if (viewCount != null && publishDate != null) {
                    ViewAndDateLabel(view = viewCount, publishDate = publishDate)
                }
            }

            onMoreClick?.let {
                IconButton(
                    onClick = { it.invoke() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset {
                            IntOffset(60, -30)
                        },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
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
}


@Composable
fun HorizontalVideoCompactCard(
    modifier: Modifier = Modifier,
    coverUrl: String,
    title: String,
    viewCount: String,
    duration: String,
    progress: Float,
    publishDate: String,
    isPlaying: Boolean,
    onClick: () -> Unit,
) {
    val coverRequest = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(coverUrl)
            .crossfade(true)
            .error(R.drawable.icon_loading_375_211)
            .placeholder(R.drawable.icon_loading_375_211)
            .build()
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                onClick.invoke()
            }
            .padding(horizontal = PaddingSm, vertical = PaddingXs),
        horizontalArrangement = Arrangement.spacedBy(PaddingMd)
    ) {
        val shape = RoundedCornerShape(CornerRadiusMd)
        val coverModifier = Modifier
            .weight(1f)
            .aspectRatio(16 / 9f)
            .clip(shape)
        Box(
            modifier = coverModifier
        ) {
            Image(
                painter = coverRequest,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
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
                    .padding(bottom = PaddingSm, end = PaddingSm),
                color = Color.Black.copy(alpha = 0.5f),
                contentColor = Color.White,
                shape = RoundedCornerShape(CornerRadiusXs)
            ) {
                Text(
                    text = duration,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(PaddingXs)
                )
            }

            if (isPlaying) {
                AnimatedPlayingIcon(Modifier.align(Alignment.Center))
            }
        }
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(PaddingXs)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
            )

            IconWithText(
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Update,
                        contentDescription = Icons.Rounded.Update.name,
                        modifier = Modifier
                            .size(16.dp),
                        tint = Color.Gray
                    )
                },
                label = publishDate
            )

            IconWithText(
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.PlayCircleOutline,
                        contentDescription = Icons.Rounded.PlayCircleOutline.name,
                        modifier = Modifier
                            .size(16.dp),
                        tint = Color.Gray
                    )
                },
                label = stringResource(R.string.str_view_count, viewCount)
            )
        }
    }
}


@Preview
@Composable
private fun HotVideoItemPreview() {
    HorizontalVideoCard(
        coverUrl = "",
        title = "【预告片】《三体2：黑暗森林（中篇）》（个人自制）",
        ownerName = "六时许_liujun",
        recommendation = "7万点赞",
        duration = "07:29",
        viewCount = "56.7万",
        publishDate = "4小时前",
        leadingIcon = null
    )
}


@Preview
@Composable
private fun HistoryVideoItemPreview() {
    HorizontalVideoCard(
        coverUrl = "",
        title = "连升两台纯血鸿蒙，我悟了...",
        ownerName = "大宽大宽",
        viewAt = "今天 19:05",
        duration = "04:19",
        progress = 0.5f,
        leadingIcon = null
    )
}

@Preview
@Composable
private fun ToViewVideoItemPreview() {
    HorizontalVideoCard(
        coverUrl = "",
        title = "不要抢走我的整活啊！2025年1月新番完结吐槽！【泛式】",
        ownerName = "泛式",
        duration = "05:20",
        viewCount = "144.03万",
        publishDate = "23小时前",
    )
}


@Preview
@Composable
private fun ToViewVideoItem2Preview() {
    HorizontalVideoCompactCard(
        coverUrl = "",
        title = "不要抢走我的整活啊！2025年1月新番完结吐槽！【泛式】",
        viewCount = 100000.toViewString(),
        duration = "05:20",
        progress = 0f,
        publishDate = "23小时前",
        isPlaying = true,
        onClick = {}
    )
}
package com.laohei.bili_tube.component.video

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.laohei.bili_tube.component.text.RichText


@Composable
fun VideoItem(
    isSingleLayout: Boolean = false,
    key: String,
    cover: String,
    title: String,
    face: String,
    ownerName: String,
    view: String,
    date: String,
    duration: String,
    @DrawableRes placeholder: Int = R.drawable.icon_loading,
    @DrawableRes error: Int = R.drawable.icon_loading,
    @DrawableRes infoPlaceholder: Int = R.drawable.icon_loading_1_1,
    @DrawableRes infoError: Int = R.drawable.icon_loading_1_1,
    onClick: () -> Unit,
    onMenuClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coverRequest = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(cover)
            .crossfade(false)
            .placeholder(placeholder)
            .error(error)
            .build()
    )
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .clickable { onClick.invoke() },
    ) {
        Box {
            Image(
                painter = coverRequest,
                contentDescription = key,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(if (isSingleLayout) 0.dp else 12.dp)),
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
                    text = duration, style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(3.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        VideoSimpleInfoBar(
            face = face,
            title = title,
            ownerName = ownerName,
            view = view,
            pubDate = date,
            placeholder = infoPlaceholder,
            error = infoError,
            trailingOnClick = { onMenuClick?.invoke() },
        )
    }
}

@Preview
@Composable
private fun VideoItemPreview() {
    VideoItem(
        isSingleLayout = true,
        key = "",
        cover = "",
        title = "不要抢走我的整活啊！2025年1月新番完结吐槽！【泛式】",
        face = "",
        ownerName = "泛式",
        duration = "05:20",
        view = "144.03万",
        date = "23小时前",
        infoError = R.drawable.bg,
        onClick = {

        },
        onMenuClick = {

        }
    )
}

@Composable
fun HorizontalVideoItem(
    cover: String,
    title: String,
    ownerName: String,
    progress: Float? = null,
    duration: String? = null,
    viewAt: String? = null,
    rcmdReason: String? = null,
    view: String? = null,
    publishDate: String? = null,
    onClick: () -> Unit = {},
    trailingOnClick: () -> Unit = {},
    leadingIcon: (@Composable () -> Unit)? = { VideoItemDefaults.DefaultLeadingIcon() }
) {
    val coverRequest = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(cover)
            .crossfade(false)
            .placeholder(R.drawable.icon_loading)
            .error(R.drawable.icon_loading)
            .build()
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                onClick.invoke()
            }
            .padding(vertical = 8.dp)
            .padding(end = 8.dp)
            .padding(start = if (leadingIcon == null) 8.dp else 0.dp),
    ) {
        leadingIcon?.let {
            Box(Modifier.align(Alignment.CenterVertically)) { it.invoke() }
            Spacer(Modifier.width(4.dp))
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(16 / 9f)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Image(
                painter = coverRequest,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxSize(),
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
                VideoDurationWidget(
                    duration = duration,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 8.dp, end = 8.dp)
                )
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
                RichText(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    collapsedMaxLine = 2,
                    minLines = 2,
                    emote = emptyMap(),
                    enabledExpanded = false,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                rcmdReason?.let { RcmdWidget(it) }
                UpWidget(ownerName)
                viewAt?.let { ViewAtWidget(it) }
                if (view != null && publishDate != null) {
                    ViewAndPubDateWidget(view, publishDate)
                }
            }

            IconButton(
                onClick = { trailingOnClick.invoke() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset {
                        IntOffset(60, -30)
                    }
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


@Preview
@Composable
private fun HotVideoItemPreview() {
    HorizontalVideoItem(
        cover = "",
        title = "【预告片】《三体2：黑暗森林（中篇）》（个人自制）",
        ownerName = "六时许_liujun",
        rcmdReason = "7万点赞",
        duration = "07:29",
        view = "56.7万",
        publishDate = "4小时前",
        leadingIcon = null
    )
}


@Preview
@Composable
private fun HistoryVideoItemPreview() {
    HorizontalVideoItem(
        cover = "",
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
    HorizontalVideoItem(
        cover = "",
        title = "不要抢走我的整活啊！2025年1月新番完结吐槽！【泛式】",
        ownerName = "泛式",
        duration = "05:20",
        view = "144.03万",
        publishDate = "23小时前",
    )
}
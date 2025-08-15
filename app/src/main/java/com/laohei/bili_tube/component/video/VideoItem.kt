package com.laohei.bili_tube.component.video

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_tube.R


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
    @DrawableRes placeholder: Int = R.drawable.icon_loading_16_9,
    @DrawableRes error: Int = R.drawable.icon_loading_16_9,
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
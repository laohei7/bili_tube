package com.laohei.bili_tube.component.video

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_tube.R

@Composable
fun VideoSimpleInfoBar(
    face: String,
    title: String,
    ownerName: String? = null,
    view: String? = null,
    pubDate: String,
    @DrawableRes placeholder: Int = R.drawable.icon_loading_1_1,
    @DrawableRes error: Int = R.drawable.icon_loading_1_1,
    trailingOnClick: () -> Unit
) {
    val context = LocalContext.current
    val faceRequest = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(face)
            .crossfade(true)
            .placeholder(placeholder)
            .error(error)
            .build()
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 12.dp, bottom = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = faceRequest,
            contentDescription = ownerName,
            modifier = Modifier
                .padding(top = 4.dp, end = 18.dp)
                .size(42.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier.weight(1f)
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
                text = when {
                    ownerName != null && view != null -> {
                        stringResource(R.string.str_author_view_date, ownerName, view, pubDate)
                    }

                    else -> pubDate
                },
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

        }

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.background,
            onClick = { trailingOnClick.invoke() }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = Icons.Default.MoreVert.name,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Preview
@Composable
private fun VideoItemSimpleInfoBarPreview() {
    VideoSimpleInfoBar(
        face = "",
        title = "日本女生看四版孙悟空：看图选大师兄？第一次看到这么多的猴哥！最可爱！最大圣！最痴情...怎么还有鬼畜呀w",
        ownerName = "椎明希爱",
        view = "4.2万",
        pubDate = "24 小时前",
        error = R.drawable.bg,
        trailingOnClick = {}
    )
}

@Preview
@Composable
private fun DynamicItemSimpleInfoBarPreview() {
    VideoSimpleInfoBar(
        face = "",
        title = "红警HBK08",
        pubDate = "1 天前",
        error = R.drawable.bg,
        trailingOnClick = {}
    )
}
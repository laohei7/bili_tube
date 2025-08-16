package com.laohei.bili_tube.ui.component.video

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import coil3.size.Size
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.component.text.RichText
import com.laohei.bili_tube.ui.theme.NonePadding
import com.laohei.bili_tube.ui.theme.SmallPadding


@OptIn(ExperimentalLayoutApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ArticleItem(
    face: String,
    ownerName: String,
    date: String,
    desc: String,
    images: List<String>?,
    shape: Shape = RoundedCornerShape(NonePadding),
    @DrawableRes infoPlaceholder: Int = R.drawable.icon_loading_1_1,
    @DrawableRes infoError: Int = R.drawable.icon_loading_1_1,
    onTrailingClick: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {

        ArticleUserBar(
            face = face,
            username = ownerName,
            pubDate = date,
            onTrailingClick = { onTrailingClick.invoke() }
        )

        RichText(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(top = 18.dp)
                .padding(bottom = 12.dp),
            text = desc, style = MaterialTheme.typography.bodyMedium,
            emote = emptyMap(),
            color = MaterialTheme.colorScheme.onBackground
        )



        images?.let { list ->
            val fixedCount = list.size.coerceIn(1, 3)
            val excess = list.size % fixedCount
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = if (list.size >= 3) 3 else list.size.coerceAtLeast(1),
                horizontalArrangement = Arrangement.spacedBy(
                    SmallPadding,
                    Alignment.CenterHorizontally
                ),
                verticalArrangement = Arrangement.spacedBy(SmallPadding)
            ) {
                list.fastForEach {
                    val imageRequest = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .data(it)
                            .crossfade(true)
                            .size(
                                if (list.size == 1) Size.ORIGINAL
                                else Size(1280, 720)
                            )
                            .placeholder(infoPlaceholder)
                            .error(infoPlaceholder)
                            .build()
                    )
                    Image(
                        painter = imageRequest,
                        contentDescription = it,
                        modifier = Modifier
                            .fillMaxWidth(1f / fixedCount - 0.05f)
                            .then(
                                if (list.size > 1) {
                                    Modifier
                                        .aspectRatio(1f)
                                } else {
                                    Modifier.wrapContentHeight()
                                }
                            )
                            .clip(shape),
                        contentScale = when {
                            list.size == 1 -> ContentScale.FillWidth
                            else -> ContentScale.Crop
                        },
                    )
                }
                if (excess > 0) {
                    repeat(fixedCount - excess) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(1f / fixedCount - 0.05f)
                                .then(
                                    if (list.size > 1) {
                                        Modifier
                                            .aspectRatio(1f)
                                    } else {
                                        Modifier.wrapContentHeight()
                                    }
                                )
                                .clip(shape),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArticleUserBar(
    context: Context = LocalContext.current,
    face: String,
    username: String,
    pubDate: String,
    @DrawableRes infoPlaceholder: Int = R.drawable.icon_loading_small,
    @DrawableRes infoError: Int = R.drawable.icon_loading_small,
    onTrailingClick: () -> Unit
) {
    val facePainter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(face)
            .crossfade(true)
            .placeholder(infoPlaceholder)
            .error(infoError)
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
            painter = facePainter,
            contentDescription = username,
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
                text = username,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = pubDate,
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

        }

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.background,
            onClick = { onTrailingClick.invoke() }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = Icons.Default.MoreVert.name,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun DRAWItem1() {
    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            ArticleItem(
                face = "",
                ownerName = "动漫作业本",
                date = "11 小时前",
                desc = "Hello World!!!",
                images = null,
                infoError = R.drawable.bg
            ) {}
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun DRAWItem2() {
    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            ArticleItem(
                face = "",
                ownerName = "动漫作业本",
                date = "11 小时前",
                desc = "Hello World!!!",
                images = listOf(""),
                infoError = R.drawable.bg
            ) {}
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun DRAWItem3() {
    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            ArticleItem(
                face = "",
                ownerName = "动漫作业本",
                date = "11 小时前",
                desc = "Hello World!!!",
                images = listOf("", "", ""),
                infoError = R.drawable.bg,
            ) {}
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun DRAWItem4() {
    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            ArticleItem(
                face = "",
                ownerName = "动漫作业本",
                date = "11 小时前",
                desc = "Hello World!!!",
                images = listOf("", ""),
                infoError = R.drawable.bg
            ) {}
        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun DRAWItem5() {
    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            ArticleItem(
                face = "",
                ownerName = "动漫作业本",
                date = "11 小时前",
                desc = "Hello World!!!",
                images = listOf("", "", "", "", "", "", "", "", ""),
                infoError = R.drawable.bg
            ) {}
        }
    }
}
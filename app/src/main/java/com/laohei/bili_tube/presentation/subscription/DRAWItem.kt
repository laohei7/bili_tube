package com.laohei.bili_tube.presentation.subscription

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_tube.R
import com.laohei.bili_tube.component.text.RichText
import com.laohei.bili_tube.component.video.VideoSimpleInfoBar


@OptIn(ExperimentalLayoutApi::class, ExperimentalSharedTransitionApi::class)
@Composable
internal fun DRAWItem(
    face: String,
    ownerName: String,
    date: String,
    desc: String,
    images: List<String>?,
    @DrawableRes infoPlaceholder: Int = R.drawable.icon_loading_1_1,
    @DrawableRes infoError: Int = R.drawable.icon_loading_1_1,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onMenuClick: (() -> Unit)? = null,
    onPreviewClick: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.background(
            MaterialTheme.colorScheme.background
        )
    ) {
        VideoSimpleInfoBar(
            face = face,
            title = ownerName,
            pubDate = date,
            placeholder = infoPlaceholder,
            error = infoError,
            trailingOnClick = { onMenuClick?.invoke() }
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
            with(sharedTransitionScope) {
                FlowRow(
                    maxItemsInEachRow = if (list.size >= 3) 3 else list.size.coerceAtLeast(1),
                ) {
                    list.fastForEach {
                        val imageRequest = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(it)
                                .crossfade(true)
                                .placeholder(R.drawable.icon_loading_1_1)
                                .error(R.drawable.icon_loading_1_1)
                                .build()
                        )
                        Image(
                            painter = imageRequest,
                            contentDescription = it,
                            modifier = Modifier
                                .fillMaxWidth(1f / images.size.coerceIn(1, 3))
                                .sharedElement(
                                    state = rememberSharedContentState(it),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                .clickable { onPreviewClick.invoke(list.indexOf(it)) }
                                .then(
                                    if (images.size > 1) {
                                        Modifier
                                            .aspectRatio(1f)
                                            .padding(4.dp)
                                    } else {
                                        Modifier.wrapContentHeight()
                                    }
                                ),
                            contentScale = when {
                                images.size == 1 -> ContentScale.FillWidth
                                else -> ContentScale.Crop
                            },
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun DRAWItem1() {
    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            DRAWItem(
                face = "",
                ownerName = "动漫作业本",
                date = "11 小时前",
                desc = "Hello World!!!",
                images = null,
                sharedTransitionScope = this@SharedTransitionLayout,
                animatedVisibilityScope = this,
                infoError = R.drawable.bg
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun DRAWItem2() {
    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            DRAWItem(
                face = "",
                ownerName = "动漫作业本",
                date = "11 小时前",
                desc = "Hello World!!!",
                images = listOf(""),
                sharedTransitionScope = this@SharedTransitionLayout,
                animatedVisibilityScope = this,
                infoError = R.drawable.bg
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun DRAWItem3() {
    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            DRAWItem(
                face = "",
                ownerName = "动漫作业本",
                date = "11 小时前",
                desc = "Hello World!!!",
                images = listOf("", "", ""),
                infoError = R.drawable.bg,
                sharedTransitionScope = this@SharedTransitionLayout,
                animatedVisibilityScope = this,
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun DRAWItem4() {
    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            DRAWItem(
                face = "",
                ownerName = "动漫作业本",
                date = "11 小时前",
                desc = "Hello World!!!",
                images = listOf("", ""),
                sharedTransitionScope = this@SharedTransitionLayout,
                animatedVisibilityScope = this,
                infoError = R.drawable.bg
            )
        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun DRAWItem5() {
    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            DRAWItem(
                face = "",
                ownerName = "动漫作业本",
                date = "11 小时前",
                desc = "Hello World!!!",
                images = listOf("", "", "", "", "", "", "", "", ""),
                sharedTransitionScope = this@SharedTransitionLayout,
                animatedVisibilityScope = this,
                infoError = R.drawable.bg
            )
        }
    }
}
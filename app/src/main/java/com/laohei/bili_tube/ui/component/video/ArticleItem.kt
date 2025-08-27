package com.laohei.bili_tube.ui.component.video

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import coil3.size.Size
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.component.NineGridLayout
import com.laohei.bili_tube.ui.component.text.RichText
import com.laohei.bili_tube.ui.theme.NonePadding
import com.laohei.bili_tube.ui.theme.SmallPadding


@OptIn(ExperimentalLayoutApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ArticleItem(
    articleKey: String,
    face: String,
    ownerName: String,
    date: String,
    desc: String,
    images: List<String>?,
    shape: Shape = RoundedCornerShape(NonePadding),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onTrailingClick: () -> Unit,
    onImageClick: ((Int, List<Pair<String, String>>) -> Unit)? = null
) {
    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        ArticleUserBar(
            face = face,
            username = ownerName,
            pubDate = date,
            onTrailingClick = { onTrailingClick.invoke() }
        )

        if (desc.isNotBlank()) {
            RichText(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 18.dp)
                    .padding(bottom = 12.dp),
                text = desc, style = MaterialTheme.typography.bodyMedium,
                emote = emptyMap(),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        images?.let { list ->
            fun onImageClick(index: Int) {
                onImageClick?.invoke(
                    index,
                    list.mapIndexed { itemIndex, url -> "img-$articleKey-$itemIndex" to url })
            }

            NineGridLayout(
                rowSpacing = SmallPadding,
                columnSpacing = SmallPadding
            ) {
                with(sharedTransitionScope) {
                    list.fastForEachIndexed { index, url ->
                        if (index == 8 && list.size > 9) {
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                            ) {
                                ImageItem(
                                    url = url,
                                    modifier = Modifier
                                        .sharedElement(
                                            state = rememberSharedContentState("img-$articleKey-$index"),
                                            animatedVisibilityScope = animatedVisibilityScope
                                        )
                                        .fillMaxSize()
                                )
                                BadgeMore(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    onImageClick(index)
                                }
                            }
                        } else {
                            ImageItem(
                                url = url,
                                modifier = Modifier
                                    .sharedElement(
                                        state = rememberSharedContentState("img-$articleKey-$index"),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                    .aspectRatio(1f)
                                    .clickable { onImageClick(index) }
                            )
                        }
                    }
                }
            }


        }

    }
}


@Composable
private fun ImageItem(
    modifier: Modifier = Modifier,
    url: String,
) {
    val context = LocalContext.current
    val imageRequest = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .size(Size(1280, 720))
            .placeholder(R.drawable.icon_loading_1_1)
            .error(R.drawable.icon_loading_1_1)
            .build()
    )
    Image(
        painter = imageRequest,
        contentDescription = url,
        modifier = modifier,
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun BadgeMore(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.5f),
        contentColor = Color.White
    ) {
        Text(
            text = "9+", style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.wrapContentSize(),
            textAlign = TextAlign.Center
        )
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
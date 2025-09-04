package com.laohei.bili_tube.ui.component.widget

import android.content.Context
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
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
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.component.layout.NineGridLayout
import com.laohei.bili_tube.ui.component.text.rich_text.ExpandableRichText
import com.laohei.bili_tube.ui.theme.PaddingNone
import com.laohei.bili_tube.ui.theme.PaddingSm
import com.laohei.bili_tube.ui.theme.PaddingXs


@OptIn(ExperimentalLayoutApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ArticleCard(
    modifier: Modifier = Modifier,
    articleId: String,
    avatarUrl: String,
    authorName: String,
    publishDate: String,
    description: String,
    images: List<String>?,
    shape: Shape = RoundedCornerShape(PaddingNone),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onMenuClick: () -> Unit,
    onImagePreview: ((Int, List<Pair<String, String>>) -> Unit)? = null
) {
    Column(
        modifier = modifier
    ) {
        ArticleHeader(
            avatarUrl = avatarUrl,
            username = authorName,
            date = publishDate,
            onMenuClick = { onMenuClick.invoke() }
        )

        if (description.isNotBlank()) {
            ExpandableRichText(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 18.dp)
                    .padding(bottom = 12.dp),
                text = description, style = MaterialTheme.typography.bodyMedium,
                emote = emptyMap(),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        images?.let {
            ArticleImageGrid(
                articleId = articleId,
                images = it,
                shape = shape,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                onImagePreview = onImagePreview
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ArticleImageGrid(
    articleId: String,
    images: List<String>,
    shape: Shape,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onImagePreview: ((Int, List<Pair<String, String>>) -> Unit)? = null
) {
    fun triggerPreview(index: Int) {
        onImagePreview?.invoke(
            index,
            images.mapIndexed { i, url -> "img-$articleId-$i" to url }
        )
    }

    NineGridLayout(
        modifier = Modifier.fillMaxWidth(),
        rowSpacing = PaddingSm,
        columnSpacing = PaddingSm
    ) {
        with(sharedTransitionScope) {
            val placeholder = when {
                images.size == 1 -> R.drawable.icon_loading_142_80
                images.size > 4 || images.size == 3 -> R.drawable.icon_loading_240
                else -> R.drawable.icon_loading_375
            }
            images.fastForEachIndexed { index, url ->
                if (index == 8 && images.size > 9) {
                    Box(modifier = Modifier.aspectRatio(1f)) {
                        ArticleImage(
                            url = url,
                            modifier = Modifier
                                .sharedElement(
                                    state = rememberSharedContentState("img-$articleId-$index"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                .fillMaxSize()
                                .clip(shape),
                            placeholder = placeholder
                        )
                        ImageOverflowBadge(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(shape)
                        ) { triggerPreview(index) }
                    }
                } else {
                    ArticleImage(
                        url = url,
                        modifier = Modifier
                            .sharedElement(
                                state = rememberSharedContentState("img-$articleId-$index"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                            .then(
                                if (images.size == 1) Modifier
                                    .heightIn(max = 280.dp)
                                    .fillMaxWidth()
                                else Modifier.aspectRatio(1f)
                            )
                            .clip(shape)
                            .clickable { triggerPreview(index) },
                        placeholder = placeholder,
                        contentScale = if (images.size == 1) ContentScale.FillWidth else ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun ArticleImage(
    modifier: Modifier = Modifier,
    url: String,
    placeholder: Int,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .placeholder(placeholder)
            .error(placeholder)
            .build()
    )
    Image(
        painter = painter,
        contentDescription = url,
        modifier = modifier,
        contentScale = contentScale
    )
}

@Composable
private fun ImageOverflowBadge(
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
            text = "9+",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.wrapContentSize(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ArticleHeader(
    context: Context = LocalContext.current,
    avatarUrl: String,
    username: String,
    date: String,
    onMenuClick: () -> Unit
) {
    val facePainter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(avatarUrl)
            .crossfade(true)
            .placeholder(R.drawable.icon_loading_84)
            .error(R.drawable.icon_loading_84)
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
                .padding(end = 18.dp)
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
                text = date,
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

        }

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.background,
            onClick = { onMenuClick.invoke() }
        ) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = Icons.Rounded.MoreVert.name,
                modifier = Modifier
                    .size(24.dp)
                    .padding(PaddingXs)
            )
        }
    }
}
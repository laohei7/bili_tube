package com.laohei.bili_tube.presentation.player.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.laohei.bili_tube.R


@Composable
internal fun MoreVideoButton(
    images: List<String>,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.clickable {
            onClick.invoke()
        }
    ) {
        images.fastForEachIndexed { index, url ->
            AsyncImage(
                model = url,
                contentDescription = "",
                modifier = Modifier
                    .zIndex(images.size - index.toFloat())
                    .offset {
                        IntOffset(
                            0,
                            -10 * index
                        )
                    }
                    .graphicsLayer {
                        alpha = 1f - 0.15f * index
                        scaleX = 1f - 0.05f * index
                        scaleY = 1f - 0.05f * index
                    }
                    .width(60.dp)
                    .border(
                        1.dp, Color.White,
                        RoundedCornerShape(4.dp)
                    )
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.icon_loading),
                error = painterResource(R.drawable.icon_loading)
            )
        }
    }
}
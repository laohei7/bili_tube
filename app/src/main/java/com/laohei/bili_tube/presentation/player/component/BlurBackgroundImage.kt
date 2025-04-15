package com.laohei.bili_tube.presentation.player.component

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.presentation.player.notPortraitGradient
import com.laohei.bili_tube.presentation.player.portraitAndFullscreenGradient
import com.laohei.bili_tube.presentation.player.portraitAndNotFullscreenGradient
import com.laohei.bili_tube.utill.isOrientationPortrait

@Composable
internal fun BlurBackgroundImage(
    bitmap: Bitmap? = null,
    isDrag: Boolean,
    isFullscreen: Boolean,
) {
    AnimatedVisibility(
        visible = isDrag.not() && bitmap != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        AnimatedContent(
            targetState = bitmap!!,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(durationMillis = 5000)
                ).togetherWith(
                    fadeOut(
                        animationSpec = tween(durationMillis = 5000)
                    )
                )
            }
        ) { targetState ->
            Image(
                painter = BitmapPainter(targetState.asImageBitmap()),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(
                        80.dp,
                        edgeTreatment = BlurredEdgeTreatment.Unbounded
                    ),
                contentScale = ContentScale.FillBounds,
            )
        }

        // Mask Color
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = when {
                        isOrientationPortrait() -> {
                            Brush.verticalGradient(
                                colors = when {
                                    isFullscreen -> portraitAndFullscreenGradient
                                    else -> portraitAndNotFullscreenGradient
                                }
                            )
                        }

                        else -> {
                            Brush.horizontalGradient(colors = notPortraitGradient)
                        }
                    }
                )
        )
    }
}

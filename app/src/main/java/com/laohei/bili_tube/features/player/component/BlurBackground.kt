package com.laohei.bili_tube.features.player.component

import android.graphics.Bitmap
import android.os.Build
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.laohei.bili_tube.features.player.notPortraitGradient
import com.laohei.bili_tube.features.player.portraitAndFullscreenGradient
import com.laohei.bili_tube.features.player.portraitAndNotFullscreenGradient
import com.laohei.bili_tube.opengl.view.GaussianBlurGLSurfaceView
import com.laohei.bili_tube.ui.util.isOrientationPortrait

@Composable
internal fun BlurBackground(
    bitmap: Bitmap? = null,
    isDrag: Boolean,
    isFullscreen: Boolean,
) {
    AnimatedVisibility(
        visible = isDrag.not() && bitmap != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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
                        .blur(60.dp),
                    contentScale = ContentScale.FillBounds,
                )
            }
        } else {
            OpenGLBlurBackground(bitmap)
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

// NOTE: This is only for blur processing on API 31 and below.
// Animation optimizations are not considered for switching.
// The project prioritizes API 34 and above.
@Composable
internal fun OpenGLBlurBackground(bitmap: Bitmap?) {
    val currentBitmap by rememberUpdatedState(bitmap)
    AndroidView(
        factory = {
            GaussianBlurGLSurfaceView(it).apply {
                setBlurSize(8f)
                setBlurRadius(32)
            }
        },
        update = { view -> view.setBitmap(currentBitmap) },
        modifier = Modifier
            .fillMaxSize()
    )
}


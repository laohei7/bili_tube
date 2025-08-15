package com.laohei.bili_tube.ui.component.animation

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

@Composable
fun SpriteWidget(
    @DrawableRes id: Int,
    frameCount: Int = 24, // Total frames (how many frames your sprite has)
    frameWidth: Float = 187f,  // Single frame width (adjusts to the size of your image)
    frameHeight: Float = 300f, // Single frame height
    animationDuration: Int = 1500 // Play the full animation in 1.5 seconds
) {
    val density = LocalDensity.current
    val infiniteTransition = rememberInfiniteTransition()

    // Calculates the current animation frame
    val frame by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = frameCount.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val apngImage = ImageBitmap.imageResource(id = id)
    Canvas(
        modifier = Modifier
            .size(
                width = with(density) { frameWidth.toDp() },
                height = with(density) { frameHeight.toDp() }
            ) // Only one frame size is displayed
    ) {
        val currentFrame = frame.toInt() % frameCount
        val offsetX = currentFrame * frameWidth

        drawImage(
            image = apngImage,
            srcOffset = IntOffset(offsetX.toInt(), 0),
            srcSize = IntSize(frameWidth.toInt(), frameHeight.toInt())
        )
    }
}
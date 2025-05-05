package com.laohei.bili_tube.component.animation

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
fun SpriteSheetWidget(
    @DrawableRes id: Int,
    frameCount: Int = 24, // 总帧数（你的精灵图有多少帧）
    frameWidth: Float = 187f,  // 单帧宽度（根据你的图片尺寸调整）
    frameHeight: Float = 300f, // 单帧高度
    animationDuration: Int = 1500 // 1.5秒播放完整动画
) {
    val density = LocalDensity.current
    val infiniteTransition = rememberInfiniteTransition()

    // 计算当前动画帧
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
            ) // 只显示一帧大小
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
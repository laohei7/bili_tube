package com.laohei.bili_tube.presentation.player.component.control

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun VideoProgressIndicator(
    isShowThumb: Boolean = true,
    progress: Float = 0.5f,
    bufferProgress: Float = 0f,
    onProgressChanged: ((Float) -> Unit)? = null
) {
    var isDrag by remember { mutableStateOf(false) }
    var thumbX by remember { mutableFloatStateOf(0f) }
    var barWidth by remember { mutableFloatStateOf(1f) }
    val thumbScale by animateFloatAsState(
        targetValue = when {
            isDrag -> 1.3f
            isShowThumb -> 1f
            else -> 0f
        },
        animationSpec = tween(durationMillis = 500)
    )
    Layout(
        modifier = Modifier
            .draggable(
                state = rememberDraggableState {
                    if (isDrag) {
                        thumbX = (thumbX + it).coerceIn(0f, barWidth)
                        val newProgress = thumbX / barWidth
                        onProgressChanged?.invoke(newProgress)
                    }
                },
                orientation = Orientation.Horizontal,
                onDragStarted = {
                    isDrag = true
                },
                onDragStopped = {
                    isDrag = false
                }
            ),
        content = {
            // 轨道
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.White.copy(alpha = 0.2f))
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(bufferProgress) // 按比例填充
                    .height(2.dp)
                    .background(Color.White.copy(alpha = 0.4f))
            )
            // 进度条
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress) // 按比例填充
                    .height(2.dp)
                    .background(Color.Red)
            )

            // 滑块
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .aspectRatio(1f)
                    .graphicsLayer {
                        scaleX = thumbScale
                        scaleY = thumbScale
                    }
                    .clip(CircleShape)
                    .background(Color.Red, CircleShape)

            )
        },
        measurePolicy = { measurables, constraints ->
            val track = measurables[0].measure(constraints) // 轨道
            val bufferProgressBar = measurables[1].measure(constraints) // 进度条
            val progressBar = measurables[2].measure(constraints) // 进度条
            val thumb = measurables[3].measure(constraints) // 滑块

            barWidth = constraints.maxWidth.toFloat()
            thumbX = when {
                isDrag -> thumbX
                else -> progress * barWidth
            }
            val thumbHeight = thumb.height

            layout(constraints.maxWidth, thumb.height) {
                track.place(0, thumbHeight - track.height) // 轨道居中
                bufferProgressBar.place(0, thumbHeight - bufferProgressBar.height) // 进度条
                progressBar.place(0, thumbHeight - progressBar.height) // 进度条
                thumb.place(
                    thumbX.toInt() - thumb.width / 2,
                    thumbHeight / 2 - progressBar.height / 2
                ) // 滑块居中
            }
        }
    )
}
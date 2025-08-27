package com.laohei.bili_tube.ui.component

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import coil3.size.Size
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.theme.ExtremeSmallPadding
import com.laohei.bili_tube.ui.theme.LargePadding
import com.laohei.bili_tube.ui.theme.SmallPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RemoteImageGallery(
    images: List<Pair<String, String>>,
    initialIndex: Int = 0,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val pager = rememberPagerState(initialPage = initialIndex) { images.size }
    val imageIndexLabel by remember {
        derivedStateOf { "${pager.currentPage + 1}/${images.size}" }
    }
    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pager,
            ) { index ->
                val scale = remember { mutableFloatStateOf(1f) }
                val offsetX = remember { mutableFloatStateOf(0f) }
                val offsetY = remember { mutableFloatStateOf(0f) }
                val animatedScale = remember { Animatable(1f) }
                val animatedOffsetX = remember { Animatable(0f) }
                val animatedOffsetY = remember { Animatable(0f) }
                val item = images[index]
                val imageRequest = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(item.second)
                        .crossfade(true)
                        .size(Size.ORIGINAL)
                        .placeholder(R.drawable.icon_loading_1_1)
                        .error(R.drawable.icon_loading_1_1)
                        .build()
                )
                Image(
                    painter = imageRequest,
                    contentDescription = "picture-${item.first}",
                    modifier = Modifier
                        .then(
                            if (pager.currentPage == index) {
                                Modifier.sharedElement(
                                    state = rememberSharedContentState(item.first),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            } else {
                                Modifier
                            }
                        )
                        .fillMaxWidth()
                        .graphicsLayer {
                            translationX = animatedOffsetX.value
                            translationY = animatedOffsetY.value
                            scaleX = animatedScale.value
                            scaleY = animatedScale.value
                        }
                        .doubleTapZoom(
                            scale = scale,
                            offsetX = offsetX,
                            offsetY = offsetY,
                            animatedScale = animatedScale,
                            animatedOffsetX = animatedOffsetX,
                            animatedOffsetY = animatedOffsetY,
                            coroutineScope = coroutineScope,
                            zoomFactor = 2f
                        )
                        .pinchAndDrag(
                            scale = scale,
                            offsetX = offsetX,
                            offsetY = offsetY,
                            animatedScale = animatedScale,
                            animatedOffsetX = animatedOffsetX,
                            animatedOffsetY = animatedOffsetY,
                            coroutineScope = coroutineScope,
                            minScale = 1f,
                            maxScale = 5f
                        ),
                    contentScale = ContentScale.Fit,
                )
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding(),
                contentColor = Color.White,
                color = Color.Gray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(ExtremeSmallPadding)
            ) {
                Text(
                    text = imageIndexLabel,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(vertical = SmallPadding, horizontal = LargePadding),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


/**
 * 双击缩放 Modifier
 *
 * @param scale 当前缩放值
 * @param offsetX 当前 X 偏移量
 * @param offsetY 当前 Y 偏移量
 * @param animatedScale Animatable 控制平滑动画
 * @param coroutineScope 协程作用域，一般用 rememberCoroutineScope()
 * @param zoomFactor 双击放大倍数，默认 2f
 */
fun Modifier.doubleTapZoom(
    scale: MutableState<Float>,
    offsetX: MutableState<Float>,
    offsetY: MutableState<Float>,
    animatedScale: Animatable<Float, *>,
    animatedOffsetX: Animatable<Float, *>,
    animatedOffsetY: Animatable<Float, *>,
    coroutineScope: CoroutineScope,
    minScale: Float = 1f,
    zoomFactor: Float = 2f,
    animationSpec: AnimationSpec<Float> = tween(300)
): Modifier = this.then(
    Modifier.pointerInput(Unit) {
        detectTapGestures(
            onDoubleTap = { offset ->
                coroutineScope.launch {
                    if (scale.value > 1f) {
                        scale.value = minScale
                        offsetX.value = 0f
                        offsetY.value = 0f
                        launch {
                            animatedScale.animateTo(minScale, animationSpec)
                        }
                        launch {
                            animatedOffsetX.animateTo(0f,animationSpec)
                        }
                        launch {
                            animatedOffsetY.animateTo(0f,animationSpec)
                        }
                    } else {
                        scale.value = zoomFactor
                        val centerX = (size.width / 2f - offset.x) * (zoomFactor - 1f)
                        val centerY = (size.height / 2f - offset.y) * (zoomFactor - 1f)
                        offsetX.value = centerX
                        offsetY.value = centerY

                        animatedOffsetX.snapTo(centerX)
                        animatedOffsetY.snapTo(centerY)
                        launch {
                            animatedScale.animateTo(zoomFactor, animationSpec)
                        }
                        launch {
                            animatedOffsetX.animateTo(centerX,animationSpec)
                        }
                        launch {
                            animatedOffsetY.animateTo(centerY,animationSpec)
                        }
                    }
                }
            }
        )
    }
)

private fun Modifier.pinchAndDrag(
    scale: MutableState<Float>,
    offsetX: MutableState<Float>,
    offsetY: MutableState<Float>,
    animatedScale: Animatable<Float, *>,
    animatedOffsetX: Animatable<Float, *>,
    animatedOffsetY: Animatable<Float, *>,
    coroutineScope: CoroutineScope,
    minScale: Float = 1f,
    maxScale: Float = 5f,
    animationSpec: AnimationSpec<Float> = tween(200)
): Modifier = this.then(
    Modifier.pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false)

            var pastTouchSlop = false
            val touchSlop = viewConfiguration.touchSlop

            do {
                val event = awaitPointerEvent()
                val zoomChange = event.calculateZoom()
                val panChange = event.calculatePan()

                if (!pastTouchSlop) {
                    val centroidSize =
                        event.calculateCentroidSize(useCurrent = false)
                    val zoomMotion = abs(1 - zoomChange) * centroidSize
                    val panMotion = panChange.getDistance()
                    pastTouchSlop =
                        zoomMotion > touchSlop || panMotion > touchSlop
                }

                val willHandle =
                    pastTouchSlop && (zoomChange != 1f || scale.value > 1f)
                if (willHandle) {
                    event.changes.forEach { it.consume() }
                }

                val newScale = (scale.value * zoomChange).coerceIn(minScale, maxScale)
                scale.value = newScale
                coroutineScope.launch { animatedScale.snapTo(newScale) }

                if (scale.value > 1f || zoomChange != 1f) {
                    offsetX.value += panChange.x
                    offsetY.value += panChange.y
                    coroutineScope.launch {
                        animatedOffsetX.snapTo(offsetX.value)
                        animatedOffsetY.snapTo(offsetY.value)
                    }
                } else {
                    offsetX.value = 0f
                    offsetY.value = 0f
                    coroutineScope.launch {
                        animatedOffsetX.animateTo(0f, animationSpec)
                        animatedOffsetY.animateTo(0f, animationSpec)
                    }
                }
            } while (event.changes.any { it.pressed })
        }
    }
)

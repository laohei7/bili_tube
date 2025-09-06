package com.laohei.compose.uicore

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cbrt
import kotlin.math.roundToInt
import kotlin.math.sign

private val PaddingSm = 8.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActionButtonListItem(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    actionHorizontalSpace: Dp = PaddingSm,
    actionVerticalSpace: Dp = PaddingSm,
    actionAlignment: Alignment.Horizontal = Alignment.End,
    thresholdFraction: Float = 0.15f,
    onOpenChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    content: @Composable @UiComposable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var maxOffsetX by remember { mutableFloatStateOf(0f) }

    val offsetX = remember { Animatable(0f) }

    val itemOffsets = remember { mutableStateListOf<Animatable<Float, AnimationVector1D>>() }
//    var isOpen by remember { mutableStateOf(false) }
    val isItemOpen by rememberUpdatedState(isOpen)
    LaunchedEffect(isItemOpen) {
        if (isItemOpen) return@LaunchedEffect
        offsetX.animateTo(
            0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }
    Layout(
        content = content,
        modifier = modifier
            .pointerInput(isItemOpen) {
                detectTapGestures {
                    if (isItemOpen) {
                        scope.launch {
                            offsetX.animateTo(
                                0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                        onOpenChange(false)
                    }else{
                        onClick()
                    }
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = offsetX.value + dragAmount
                        val clamped = when {
                            newOffset > maxOffsetX -> applyDamping(
                                newOffset,
                                maxOffsetX,
                                actionAlignment
                            )

                            newOffset < -maxOffsetX -> applyDamping(
                                newOffset,
                                maxOffsetX,
                                actionAlignment
                            )

                            else -> newOffset
                        }
                        scope.launch {
                            when (actionAlignment) {
                                Alignment.Start -> {
                                    if (clamped > 0 && newOffset > maxOffsetX) {
                                        offsetX.animateTo(clamped)
                                    } else if (clamped > 0) {
                                        offsetX.snapTo(clamped)
                                    }
                                }

                                else -> {
                                    if (clamped < 0 && newOffset < -maxOffsetX) {
                                        offsetX.animateTo(clamped)
                                    } else if (clamped < 0) {
                                        offsetX.snapTo(clamped)
                                    }
                                }
                            }
                        }
                    },
                    onDragEnd = {
                        val threshold = maxOffsetX * thresholdFraction
                        scope.launch {
                            val openThreshold =
                                if (actionAlignment == Alignment.Start) threshold else -threshold
                            val closeThreshold =
                                if (actionAlignment == Alignment.Start) maxOffsetX - threshold else -maxOffsetX + threshold
                            val openValue =
                                if (actionAlignment == Alignment.Start) maxOffsetX else -maxOffsetX
                            val closeValue = 0f
                            val isOverOpenThreshold = !isItemOpen && (
                                    (actionAlignment == Alignment.Start && offsetX.value > openThreshold) ||
                                            (actionAlignment != Alignment.Start && offsetX.value < openThreshold)
                                    )

                            val isOverCloseThreshold = isItemOpen && (
                                    (actionAlignment == Alignment.Start && offsetX.value < closeThreshold) ||
                                            (actionAlignment != Alignment.Start && offsetX.value > closeThreshold)
                                    )
                            val target = when {
                                isOverOpenThreshold -> {
                                    onOpenChange(true)
                                    openValue
                                }

                                isOverCloseThreshold -> {
                                    onOpenChange(false)
                                    closeValue
                                }

                                else -> if (isItemOpen) openValue else closeValue
                            }
                            offsetX.animateTo(
                                target,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                    }
                )
            }
    ) { measurables, constraints ->
        val listItemPlaceable = measurables.first().measure(constraints)
        val layoutWidth = listItemPlaceable.width
        val layoutHeight = listItemPlaceable.height
        val actionHorizontalSpacePx = actionHorizontalSpace.roundToPx()
        val actionVerticalSpacePx = actionVerticalSpace.roundToPx()

        val actionPlaceables =
            measurables.subList(1, measurables.size).map {
                it.measure(
                    constraints.copy(
                        minWidth = layoutHeight - actionVerticalSpacePx,
                        maxWidth = layoutHeight - actionVerticalSpacePx
                    )
                )
            }
        val actionTotalWidth =
            actionPlaceables.sumOf { it.width } + actionHorizontalSpacePx * (actionPlaceables.size + 1)
        if (maxOffsetX != actionTotalWidth.toFloat()) {
            maxOffsetX = actionTotalWidth.toFloat()
        }
        if (itemOffsets.size != actionPlaceables.size) {
            itemOffsets.clear()
            actionPlaceables.forEach { _ ->
                if (actionAlignment == Alignment.Start) {
                    itemOffsets.add(Animatable(0f))
                } else {
                    itemOffsets.add(Animatable(layoutWidth.toFloat()))
                }
            }
        }
        layout(layoutWidth, layoutHeight) {
            listItemPlaceable.place(
                offsetX.value.roundToInt(),
                0,
                measurables.size.toFloat()
            )

            actionPlaceables.forEachIndexed { index, placeable ->
                val (minX, maxX) = if (actionAlignment == Alignment.Start) {
                    0 to (actionHorizontalSpacePx + placeable.width) * (index + 1)
                } else {
                    layoutWidth - (actionHorizontalSpacePx + placeable.width) * (index + 1) to layoutWidth
                }

                val progress = (abs(offsetX.value) / maxOffsetX).coerceAtLeast(0f)
                val xPosition = if (actionAlignment == Alignment.Start) {
                    (maxX - minX) * progress - placeable.width
                } else {
                    maxX - (maxX - minX) * progress
                }
                val animatable = itemOffsets[index]
                scope.launch {
                    animatable.animateTo(
                        xPosition,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }
                placeable.place(
                    animatable.value.roundToInt(),
                    layoutHeight / 2 - placeable.height / 2,
                    if (actionAlignment == Alignment.Start) {
                        index.toFloat()
                    } else {
                        actionPlaceables.size - index.toFloat()
                    }
                )
            }
        }
    }
}

private fun applyDamping(
    offset: Float,
    maxOffset: Float,
    actionAlignment: Alignment.Horizontal
): Float {
    val scale = 25f
    val (minBound, maxBound) = if (actionAlignment == Alignment.Start) {
        0f to maxOffset
    } else {
        -maxOffset to 0f
    }
    return when {
        offset < minBound -> {
            val over = offset - minBound
            minBound + sign(over) * cbrt(abs(over).toDouble()).toFloat() * scale
        }

        offset > maxBound -> {
            val over = offset - maxBound
            maxBound + sign(over) * cbrt(abs(over).toDouble()).toFloat() * scale
        }

        else -> offset
    }
}

@Preview(showBackground = true)
@Composable
private fun ActionButtonListItemPreview() {
    var isOpen by remember { mutableStateOf(false) }
    ActionButtonListItem(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray),
        isOpen = isOpen,
        onOpenChange = { isOpen = it },
        onClick = {}
    ) {
        ListItem(
            headlineContent = { Text(text = "Hello World") }
        )
        FilledIconButton(
            onClick = {},
            shape = CircleShape,
            modifier = Modifier
                .size(32.dp)
                .aspectRatio(1f)
        ) {
            Icon(
                imageVector = Icons.Rounded.Share,
                contentDescription = Icons.Rounded.Share.name
            )
        }
        FilledIconButton(
            onClick = {}, modifier = Modifier
                .size(32.dp)
                .aspectRatio(1f)
        ) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = Icons.Rounded.Delete.name
            )
        }
        FilledIconButton(
            onClick = {}, modifier = Modifier
                .size(32.dp)
                .aspectRatio(1f)
        ) {
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = Icons.Rounded.Favorite.name
            )
        }
    }
}
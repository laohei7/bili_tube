package com.laohei.bili_tube.ui.component.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.laohei.bili_tube.ui.preview.FakeNineGridLayoutData
import com.laohei.bili_tube.ui.theme.PaddingSm

@Composable
fun NineGridLayout(
    modifier: Modifier = Modifier,
    contentPadding: Dp = 0.dp,
    rowSpacing: Dp = 0.dp,
    columnSpacing: Dp = 0.dp,
    content: @Composable @UiComposable (() -> Unit)
) {
    Layout(modifier = modifier, content = content) { measurables, constraints ->

        when (measurables.size) {
            1 -> {
                val placeable = measurables.first().measure(constraints)
                layout(constraints.maxWidth, placeable.height) {
                    placeable.placeRelative(0, 0)
                }
            }

            else -> {
                val paddingPx = contentPadding.toPx().toInt()
                val rowSpacePx = rowSpacing.toPx().toInt()
                val colSpacePx = columnSpacing.toPx().toInt()
                val fixedCount = when (measurables.size) {
                    2, 4 -> 2
                    else -> 3
                }
                val rowCount = (measurables.take(9).size + fixedCount - 1) / fixedCount
                val itemWidth =
                    (constraints.maxWidth - 2 * paddingPx - (fixedCount - 1) * colSpacePx) / fixedCount
                val itemConstraints = constraints.copy(minWidth = itemWidth, maxWidth = itemWidth)
                val placeables = measurables.take(9).map { it.measure(itemConstraints) }
                val itemHeight = placeables.firstOrNull()?.height ?: 0
                val totalHeight =
                    rowCount * itemHeight + (rowCount - 1) * rowSpacePx + 2 * paddingPx
                layout(constraints.maxWidth, totalHeight) {
                    placeables.forEachIndexed { index, placeable ->
                        val row = index / fixedCount
                        val col = index % fixedCount

                        val x = paddingPx + col * (itemWidth + colSpacePx)
                        val y = paddingPx + row * (itemHeight + rowSpacePx)

                        placeable.placeRelative(x, y)
                    }
                }
            }
        }

    }
}

@Preview
@Composable
private fun NineGridLayoutPreview(
    @PreviewParameter(FakeNineGridLayoutData::class) colors: List<Color>
) {
    NineGridLayout(
        columnSpacing = PaddingSm,
        rowSpacing = PaddingSm
    ) {
        colors.fastForEachIndexed { index, it ->
            if (index == 8 && colors.size > 9) {
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(it)
                    )
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "9+")
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(it)
                )
            }
        }
    }
}

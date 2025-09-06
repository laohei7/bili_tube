package com.laohei.compose.uicore

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMapIndexed


/**
 * Note: fabeSize maintains the same height of modifier
 */
@Composable
fun NotchedBottomBar(
    modifier: Modifier = Modifier,
    icons: List<ImageVector>,
    selectedIndex: Int,
    fabIcon: ImageVector,
    fabIconSize: Dp = 28.dp,
    onIconClick: (index: Int) -> Unit,
    onFabClick: () -> Unit,
    fabSize: Dp,
    fabColor: Color = FloatingActionButtonDefaults.containerColor,
    fabContainerColor: Color = contentColorFor(fabColor),
    fabElevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    iconColor: Color = NavigationBarItemDefaults.colors().unselectedIconColor,
    selectedIconColor: Color = NavigationBarItemDefaults.colors().selectedIconColor,
    iconContainerColor: Color = NavigationBarItemDefaults.colors().selectedIndicatorColor,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    val density = LocalDensity.current
    SubcomposeLayout(
        modifier = modifier
    ) { constraints ->
        // measure fab
        val fabPlaceables = subcompose("fab") {
            FloatingActionButton(
                onClick = onFabClick,
                modifier = Modifier.size(fabSize),
                containerColor = fabContainerColor,
                contentColor = fabColor,
                elevation = fabElevation,
                shape = CircleShape
            ) {
                Icon(
                    modifier = Modifier.size(fabIconSize),
                    imageVector = fabIcon,
                    contentDescription = "fab",
                    tint = fabColor
                )
            }
        }.map { it.measure(constraints) }

        val fabWidth = fabPlaceables.maxOf { it.width }
        val fabHeight = fabPlaceables.maxOf { it.height }

        // measure icons
        val iconPlaceables = icons.fastMapIndexed { index, icon ->
            val measurable = subcompose("icon_$index") {
                val selected = selectedIndex == index
                Surface(
                    onClick = { onIconClick(index) },
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .aspectRatio(16 / 9f),
                    color = if (selected) iconContainerColor else Color.Transparent,
                    contentColor = if (selected) selectedIconColor else iconColor
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "$index-icon",
                        tint = if (selected) selectedIconColor else iconColor
                    )
                }
            }.first().measure(constraints)
            measurable
        }

        val canvasHeight = iconPlaceables.maxOf { it.height }

        // measure background
        val backgroundPlaceables = subcompose("background") {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { canvasHeight.toDp() })
            ) {
                val width = size.width
                val height = size.height
                val fabRadius = fabWidth / 2f
                val centerX = width / 2f
                val curveDepth = fabHeight * 0.6f
                val controlOffsetX = fabWidth.toFloat()

                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(centerX - fabRadius - controlOffsetX, 0f)
                    cubicTo(
                        centerX - fabRadius, 0f,
                        centerX - fabRadius, curveDepth,
                        centerX, curveDepth
                    )
                    cubicTo(
                        centerX + fabRadius, curveDepth,
                        centerX + fabRadius, 0f,
                        centerX + fabRadius + controlOffsetX, 0f
                    )
                    lineTo(width, 0f)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }
                drawPath(path, color = containerColor)
            }
        }.map { it.measure(constraints) }

        // calculate icon space
        val totalItemsWidth = iconPlaceables.sumOf { it.width } + fabWidth
        val spaceCount = icons.size + 1
        val spaceWidth = (constraints.maxWidth - totalItemsWidth).coerceAtLeast(0) / spaceCount

        layout(constraints.maxWidth, constraints.maxHeight) {
            backgroundPlaceables.forEach {
                it.place(0, constraints.maxHeight - it.height)
            }

            var x = spaceWidth
            iconPlaceables.fastForEachIndexed { index, it ->
                it.place(x, 0)
                x += it.width + if (index == (icons.size / 2 - 1)) {
                    spaceWidth + fabWidth // keep the space for fab
                } else {
                    spaceWidth
                }
            }

            fabPlaceables.forEach {
                it.place(
                    (constraints.maxWidth - it.width) / 2,
                    0 - fabHeight / 2
                )
            }
        }
    }
}


/**
 * Note: fabeSize maintains the same height of modifier
 */
@Composable
fun NotchedBottomBar(
    modifier: Modifier = Modifier,
    icons: List<Int>,
    selectedIndex: Int,
    @DrawableRes fabIcon: Int,
    fabIconSize: Dp = 28.dp,
    onIconClick: (index: Int) -> Unit,
    onFabClick: () -> Unit,
    fabSize: Dp,
    fabColor: Color = FloatingActionButtonDefaults.containerColor,
    fabContainerColor: Color = contentColorFor(fabColor),
    fabElevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    iconColor: Color = NavigationBarItemDefaults.colors().unselectedIconColor,
    selectedIconColor: Color = NavigationBarItemDefaults.colors().selectedIconColor,
    iconContainerColor: Color = NavigationBarItemDefaults.colors().selectedIndicatorColor,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    val density = LocalDensity.current
    SubcomposeLayout(
        modifier = modifier
    ) { constraints ->
        // measure fab
        val fabPlaceables = subcompose("fab") {
            FloatingActionButton(
                onClick = onFabClick,
                modifier = Modifier.size(fabSize),
                containerColor = fabContainerColor,
                contentColor = fabColor,
                elevation = fabElevation,
                shape = CircleShape
            ) {
                Image(
                    modifier = Modifier.size(fabIconSize),
                    painter = painterResource(fabIcon),
                    contentDescription = "fab",
                    colorFilter = ColorFilter.tint(fabColor)
                )
            }
        }.map { it.measure(constraints) }

        val fabWidth = fabPlaceables.maxOf { it.width }
        val fabHeight = fabPlaceables.maxOf { it.height }

        // measure icons
        val iconPlaceables = icons.fastMapIndexed { index, icon ->
            val measurable = subcompose("icon_$index") {
                val selected = selectedIndex == index
                Surface(
                    onClick = { onIconClick(index) },
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .aspectRatio(16 / 9f),
                    color = if (selected) iconContainerColor else Color.Transparent,
                    contentColor = if (selected) selectedIconColor else iconColor
                ) {
                    Image(
                        modifier= Modifier
                            .wrapContentSize().padding(vertical = 3.dp),
                        painter = painterResource(icon),
                        contentDescription = "$index-icon",
                        colorFilter = ColorFilter.tint(iconColor)
                    )
                }
            }.first().measure(constraints)
            measurable
        }

        val canvasHeight = iconPlaceables.maxOf { it.height }

        // measure background
        val backgroundPlaceables = subcompose("background") {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { canvasHeight.toDp() })
            ) {
                val width = size.width
                val height = size.height
                val fabRadius = fabWidth / 2f
                val centerX = width / 2f
                val curveDepth = fabHeight * 0.6f
                val controlOffsetX = fabWidth.toFloat()

                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(centerX - fabRadius - controlOffsetX, 0f)
                    cubicTo(
                        centerX - fabRadius, 0f,
                        centerX - fabRadius, curveDepth,
                        centerX, curveDepth
                    )
                    cubicTo(
                        centerX + fabRadius, curveDepth,
                        centerX + fabRadius, 0f,
                        centerX + fabRadius + controlOffsetX, 0f
                    )
                    lineTo(width, 0f)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }
                drawPath(path, color = containerColor)
            }
        }.map { it.measure(constraints) }

        // calculate icon space
        val totalItemsWidth = iconPlaceables.sumOf { it.width } + fabWidth
        val spaceCount = icons.size + 1
        val spaceWidth = (constraints.maxWidth - totalItemsWidth).coerceAtLeast(0) / spaceCount

        layout(constraints.maxWidth, constraints.maxHeight) {
            backgroundPlaceables.forEach {
                it.place(0, constraints.maxHeight - it.height)
            }

            var x = spaceWidth
            iconPlaceables.fastForEachIndexed { index, it ->
                it.place(x, 0)
                x += it.width + if (index == (icons.size / 2 - 1)) {
                    spaceWidth + fabWidth // keep the space for fab
                } else {
                    spaceWidth
                }
            }

            fabPlaceables.forEach {
                it.place(
                    (constraints.maxWidth - it.width) / 2,
                    0 - fabHeight / 2
                )
            }
        }
    }
}


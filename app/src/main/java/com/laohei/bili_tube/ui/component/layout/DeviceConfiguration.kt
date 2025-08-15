package com.laohei.bili_tube.ui.component.layout

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private object UIValues {
    const val COMPACT = "COMPACT"
    const val MEDIUM = "MEDIUM"
    const val EXPANDED = "EXPANDED"
}

private const val WIDTH_DP_MEDIUM_LOWER_BOUND = 600

private const val WIDTH_DP_EXPANDED_LOWER_BOUND = 840

private const val HEIGHT_DP_MEDIUM_LOWER_BOUND = 480

private const val HEIGHT_DP_EXPANDED_LOWER_BOUND = 900

private fun processWidthOnly(width: Dp): String {
    return when {
        width >= WIDTH_DP_EXPANDED_LOWER_BOUND.dp -> {
            UIValues.EXPANDED
        }

        width >= WIDTH_DP_MEDIUM_LOWER_BOUND.dp -> {
            UIValues.MEDIUM
        }

        else -> {
            UIValues.COMPACT
        }
    }
}

private fun processHeightOnly(height: Dp): String {
    return when {
        height >= HEIGHT_DP_EXPANDED_LOWER_BOUND.dp -> {
            UIValues.EXPANDED
        }

        height >= HEIGHT_DP_MEDIUM_LOWER_BOUND.dp -> {
            UIValues.MEDIUM
        }

        else -> {
            UIValues.COMPACT
        }
    }
}

enum class DeviceConfiguration {
    MOBILE_PORTRAIT, // PORTRAIT 为竖屏；LANDSCAPE 为横屏
    MOBILE_LANDSCAPE,
    TABLE_PORTRAIT,
    TABLE_LANDSCAPE,
    DESKTOP;

    companion object {
        fun fromWindowSize(width: Dp, height: Dp): DeviceConfiguration {
            val widthType = processWidthOnly(width)
            val heightType = processHeightOnly(height)

            return when {
                widthType == UIValues.COMPACT &&
                        heightType == UIValues.MEDIUM -> MOBILE_PORTRAIT

                widthType == UIValues.COMPACT &&
                        heightType == UIValues.EXPANDED -> MOBILE_PORTRAIT

                widthType == UIValues.EXPANDED &&
                        heightType == UIValues.COMPACT -> MOBILE_LANDSCAPE

                widthType == UIValues.MEDIUM &&
                        heightType == UIValues.EXPANDED -> TABLE_PORTRAIT

                widthType == UIValues.EXPANDED &&
                        heightType == UIValues.MEDIUM -> TABLE_LANDSCAPE

                else -> DESKTOP
            }
        }
    }
}
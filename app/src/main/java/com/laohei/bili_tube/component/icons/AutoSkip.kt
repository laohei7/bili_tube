package com.laohei.bili_tube.component.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val Icons.Outlined.AutoSkip: ImageVector
    get() {
        if (autoSkip != null) {
            return autoSkip!!
        }
        autoSkip = Builder(
            name = "AutoSkip", defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 1024f, viewportHeight = 1024f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
            ) {
                moveTo(485.781f, 170.667f)
                curveTo(218.475f, 170.667f, 108.949f, 446.784f, 108.949f, 446.784f)
                arcToRelative(32f, 32f, 0f, true, false, 59.435f, 23.765f)
                reflectiveCurveTo(263.04f, 234.667f, 485.76f, 234.667f)
                curveToRelative(160.363f, 0f, 265.024f, 122.944f, 311.253f, 192f)
                horizontalLineTo(693.333f)
                arcToRelative(32f, 32f, 0f, true, false, 0f, 64f)
                horizontalLineToRelative(192f)
                arcToRelative(32f, 32f, 0f, false, false, 32f, -32f)
                verticalLineToRelative(-192f)
                arcToRelative(32f, 32f, 0f, false, false, -32.491f, -32.448f)
                arcToRelative(32f, 32f, 0f, false, false, -31.509f, 34.448f)
                verticalLineToRelative(128.491f)
                curveToRelative(-51.456f, -78.933f, -172.587f, -224.491f, -367.552f, -224.491f)

                moveTo(160f, 597.333f)
                curveTo(119.147f, 597.333f, 85.333f, 631.147f, 85.333f, 672f)
                verticalLineToRelative(106.667f)
                curveToRelative(0f, 40.853f, 33.814f, 74.667f, 74.667f, 74.667f)
                horizontalLineToRelative(106.667f)
                curveToRelative(40.853f, 0f, 74.667f, -33.814f, 74.667f, -74.667f)
                verticalLineToRelative(-106.667f)
                curveToRelative(0f, -40.853f, -33.814f, -74.667f, -74.667f, -74.667f)
                horizontalLineTo(160f)

                moveTo(458.667f, 597.333f)
                curveTo(417.813f, 597.333f, 384f, 631.147f, 384f, 672f)
                verticalLineToRelative(106.667f)
                curveToRelative(0f, 40.853f, 33.814f, 74.667f, 74.667f, 74.667f)
                horizontalLineToRelative(106.667f)
                curveToRelative(40.853f, 0f, 74.667f, -33.814f, 74.667f, -74.667f)
                verticalLineToRelative(-106.667f)
                curveToRelative(0f, -40.853f, -33.814f, -74.667f, -74.667f, -74.667f)
                horizontalLineTo(458.667f)

                moveTo(757.333f, 597.333f)
                curveTo(716.48f, 597.333f, 682.667f, 631.147f, 682.667f, 672f)
                verticalLineToRelative(106.667f)
                curveToRelative(0f, 40.853f, 33.813f, 74.667f, 74.666f, 74.667f)
                horizontalLineToRelative(106.667f)
                curveToRelative(40.853f, 0f, 74.667f, -33.814f, 74.667f, -74.667f)
                verticalLineToRelative(-106.667f)
                curveToRelative(0f, -40.853f, -33.814f, -74.667f, -74.667f, -74.667f)
                horizontalLineTo(757.333f)

                // 细节内部框线
                moveTo(160f, 661.333f)
                horizontalLineToRelative(106.667f)
                curveToRelative(6.293f, 0f, 10.667f, 4.373f, 10.667f, 10.667f)
                verticalLineToRelative(106.667f)
                curveToRelative(0f, 6.293f, -4.374f, 10.667f, -10.667f, 10.667f)
                horizontalLineTo(160f)
                arcToRelative(10.197f, 10.197f, 0f, false, true, -10.667f, -10.667f)
                verticalLineTo(672f)
                curveToRelative(0f, -6.293f, 4.374f, -10.667f, 10.667f, -10.667f)

                moveTo(458.667f, 661.333f)
                horizontalLineToRelative(106.667f)
                curveToRelative(6.293f, 0f, 10.667f, 4.373f, 10.667f, 10.667f)
                verticalLineToRelative(106.667f)
                curveToRelative(0f, 6.293f, -4.374f, 10.667f, -10.667f, 10.667f)
                horizontalLineTo(458.667f)
                arcToRelative(10.197f, 10.197f, 0f, false, true, -10.667f, -10.667f)
                verticalLineTo(672f)
                curveToRelative(0f, -6.293f, 4.374f, -10.667f, 10.667f, -10.667f)

                moveTo(757.333f, 661.333f)
                horizontalLineToRelative(106.667f)
                curveToRelative(6.293f, 0f, 10.667f, 4.373f, 10.667f, 10.667f)
                verticalLineToRelative(106.667f)
                curveToRelative(0f, 6.293f, -4.374f, 10.667f, -10.667f, 10.667f)
                horizontalLineTo(757.333f)
                arcToRelative(10.197f, 10.197f, 0f, false, true, -10.667f, -10.667f)
                verticalLineTo(672f)
                curveToRelative(0f, -6.293f, 4.374f, -10.667f, 10.667f, -10.667f)
            }
        }.build()
        return autoSkip!!
    }

private var autoSkip: ImageVector? = null


package com.laohei.bili_tube.component.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val Icons.Outlined.Level0: ImageVector
    get() {
        if (level0 != null) {
            return level0!!
        }
        level0 = Builder(
            name = "Level0", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
            viewportWidth = 1024.0f, viewportHeight = 1024.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFBFBFBF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(825.6f, 406.4f)
                horizontalLineToRelative(-73.6f)
                curveToRelative(-9.6f, 0f, -19.2f, 9.6f, -19.2f, 19.2f)
                verticalLineToRelative(172.8f)
                curveToRelative(0f, 9.6f, 9.6f, 19.2f, 19.2f, 19.2f)
                horizontalLineToRelative(73.6f)
                curveToRelative(9.6f, 0f, 19.2f, -9.6f, 19.2f, -19.2f)
                verticalLineToRelative(-172.8f)
                curveToRelative(-3.2f, -9.6f, -9.6f, -19.2f, -19.2f, -19.2f)
                close()

                moveTo(934.4f, 291.2f)
                horizontalLineTo(643.2f)
                curveToRelative(-16.0f, 0f, -25.6f, 12.8f, -25.6f, 25.6f)
                verticalLineToRelative(19.2f)
                horizontalLineTo(89.6f)
                curveToRelative(-16.0f, 0f, -25.6f, 12.8f, -25.6f, 25.6f)
                verticalLineTo(704.0f)
                curveToRelative(0f, 16.0f, 12.8f, 25.6f, 25.6f, 25.6f)
                horizontalLineToRelative(841.6f)
                curveToRelative(16.0f, 0f, 25.6f, -12.8f, 25.6f, -25.6f)
                verticalLineTo(316.8f)
                curveToRelative(3.2f, -12.8f, -9.6f, -25.6f, -22.4f, -25.6f)
                close()

                moveTo(307.2f, 662.4f)
                curveToRelative(0f, 9.6f, -9.6f, 19.2f, -19.2f, 19.2f)
                horizontalLineTo(137.6f)
                curveToRelative(-9.6f, 0f, -19.2f, -9.6f, -19.2f, -19.2f)
                verticalLineTo(409.6f)
                curveToRelative(0f, -9.6f, 9.6f, -19.2f, 19.2f, -19.2f)
                horizontalLineToRelative(25.6f)
                curveToRelative(9.6f, 0f, 19.2f, 9.6f, 19.2f, 19.2f)
                verticalLineToRelative(208.0f)
                horizontalLineToRelative(108.8f)
                curveToRelative(9.6f, 0f, 19.2f, 9.6f, 19.2f, 19.2f)
                verticalLineToRelative(25.6f)
                close()

                moveTo(579.2f, 563.2f)
                verticalLineToRelative(6.4f)
                curveToRelative(0f, 6.4f, 0f, 12.8f, -3.2f, 16.0f)
                lineToRelative(-89.6f, 89.6f)
                curveToRelative(-6.4f, 6.4f, -22.4f, 6.4f, -22.4f, 6.4f)
                reflectiveCurveToRelative(-16.0f, 0f, -22.4f, -6.4f)
                lineTo(352.0f, 585.6f)
                curveToRelative(-6.4f, -3.2f, -6.4f, -12.8f, -3.2f, -19.2f)
                verticalLineTo(409.6f)
                curveToRelative(0f, -9.6f, 9.6f, -19.2f, 19.2f, -19.2f)
                horizontalLineToRelative(25.6f)
                curveToRelative(9.6f, 0f, 19.2f, 9.6f, 19.2f, 19.2f)
                verticalLineToRelative(147.2f)
                lineToRelative(54.4f, 54.4f)
                lineToRelative(54.4f, -54.4f)
                verticalLineToRelative(-144.0f)
                curveToRelative(0f, -9.6f, 9.6f, -19.2f, 19.2f, -19.2f)
                horizontalLineToRelative(25.6f)
                curveToRelative(9.6f, 0f, 19.2f, 9.6f, 19.2f, 19.2f)
                verticalLineToRelative(150.4f)
                close()

                moveTo(905.6f, 652.8f)
                curveToRelative(0f, 16.0f, -12.8f, 25.6f, -25.6f, 25.6f)
                horizontalLineTo(697.6f)
                curveToRelative(-16.0f, 0f, -25.6f, -12.8f, -25.6f, -25.6f)
                verticalLineTo(371.2f)
                curveToRelative(0f, -16.0f, 12.8f, -25.6f, 25.6f, -25.6f)
                horizontalLineToRelative(182.4f)
                curveToRelative(16.0f, 0f, 25.6f, 12.8f, 25.6f, 25.6f)
                verticalLineToRelative(281.6f)
                close()
            }
        }.build()
        return level0!!
    }

private var level0: ImageVector? = null
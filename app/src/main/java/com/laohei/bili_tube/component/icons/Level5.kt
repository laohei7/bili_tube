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

internal val Icons.Outlined.Level5: ImageVector
    get() {
        if (level5 != null) {
            return level5!!
        }
        level5 = Builder(
            name = "Level5", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
            viewportWidth = 1024.0f, viewportHeight = 1024.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFBFBFBF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(934.4f, 291.2f)
                horizontalLineTo(643.2f)
                curveToRelative(-16f, 0f, -25.6f, 12.8f, -25.6f, 25.6f)
                verticalLineToRelative(19.2f)
                horizontalLineTo(89.6f)
                curveToRelative(-16f, 0f, -25.6f, 12.8f, -25.6f, 25.6f)
                verticalLineTo(704f)
                curveToRelative(0f, 16f, 12.8f, 25.6f, 25.6f, 25.6f)
                horizontalLineToRelative(841.6f)
                curveToRelative(16f, 0f, 25.6f, -12.8f, 25.6f, -25.6f)
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
                verticalLineToRelative(208f)
                horizontalLineToRelative(108.8f)
                curveToRelative(9.6f, 0f, 19.2f, 9.6f, 19.2f, 19.2f)
                verticalLineToRelative(25.6f)
                close()

                moveTo(579.2f, 563.2f)
                verticalLineToRelative(6.4f)
                curveToRelative(0f, 6.4f, 0f, 12.8f, -3.2f, 16f)
                lineToRelative(-89.6f, 89.6f)
                curveToRelative(-6.4f, 6.4f, -22.4f, 6.4f, -22.4f, 6.4f)
                reflectiveCurveToRelative(-16f, 0f, -22.4f, -6.4f)
                lineTo(352f, 585.6f)
                curveToRelative(-6.4f, -3.2f, -6.4f, -12.8f, -3.2f, -19.2f)
                verticalLineTo(409.6f)
                curveToRelative(0f, -9.6f, 9.6f, -19.2f, 19.2f, -19.2f)
                horizontalLineToRelative(25.6f)
                curveToRelative(9.6f, 0f, 19.2f, 9.6f, 19.2f, 19.2f)
                verticalLineToRelative(147.2f)
                lineToRelative(54.4f, 54.4f)
                lineToRelative(54.4f, -54.4f)
                verticalLineToRelative(-144f)
                curveToRelative(0f, -9.6f, 9.6f, -19.2f, 19.2f, -19.2f)
                horizontalLineToRelative(25.6f)
                curveToRelative(9.6f, 0f, 19.2f, 9.6f, 19.2f, 19.2f)
                verticalLineToRelative(150.4f)
                close()

                moveTo(905.6f, 390.4f)
                curveToRelative(0f, 9.6f, -9.6f, 19.2f, -19.2f, 19.2f)
                horizontalLineTo(732.8f)
                verticalLineTo(480f)
                horizontalLineToRelative(153.6f)
                curveToRelative(9.6f, 0f, 19.2f, 9.6f, 19.2f, 19.2f)
                verticalLineToRelative(163.2f)
                curveToRelative(0f, 9.6f, -9.6f, 19.2f, -19.2f, 19.2f)
                horizontalLineTo(688f)
                curveToRelative(-9.6f, 0f, -19.2f, -9.6f, -19.2f, -19.2f)
                verticalLineToRelative(-25.6f)
                curveToRelative(0f, -9.6f, 9.6f, -19.2f, 19.2f, -19.2f)
                horizontalLineToRelative(153.6f)
                verticalLineTo(544f)
                horizontalLineTo(688f)
                curveToRelative(-9.6f, 0f, -19.2f, -9.6f, -19.2f, -19.2f)
                verticalLineTo(361.6f)
                curveToRelative(0f, -9.6f, 9.6f, -19.2f, 19.2f, -19.2f)
                horizontalLineToRelative(198.4f)
                curveToRelative(9.6f, 0f, 19.2f, 9.6f, 19.2f, 19.2f)
                verticalLineToRelative(28.8f)
                close()
            }
        }.build()
        return level5!!
    }

private var level5: ImageVector? = null
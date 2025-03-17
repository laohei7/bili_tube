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

internal val Icons.Outlined.SleepTimer: ImageVector
    get() {
        if (sleepTimer != null) {
            return sleepTimer!!
        }
        sleepTimer = Builder(name = "SleepTimer", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
            viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF1f1f1f)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero) {
                moveTo(600.0f, 320.0f)
                lineTo(480.0f, 200.0f)
                lineToRelative(120.0f, -120.0f)
                lineToRelative(120.0f, 120.0f)
                lineToRelative(-120.0f, 120.0f)
                close()
                moveTo(800.0f, 440.0f)
                lineTo(720.0f, 360.0f)
                lineTo(800.0f, 280.0f)
                lineTo(880.0f, 360.0f)
                lineTo(800.0f, 440.0f)
                close()
                moveTo(483.0f, 880.0f)
                quadToRelative(-84.0f, 0.0f, -157.5f, -32.0f)
                reflectiveQuadToRelative(-128.0f, -86.5f)
                quadTo(143.0f, 707.0f, 111.0f, 633.5f)
                reflectiveQuadTo(79.0f, 476.0f)
                quadToRelative(0.0f, -146.0f, 93.0f, -257.5f)
                reflectiveQuadTo(409.0f, 80.0f)
                quadToRelative(-18.0f, 99.0f, 11.0f, 193.5f)
                reflectiveQuadTo(520.0f, 439.0f)
                quadToRelative(71.0f, 71.0f, 165.5f, 100.0f)
                reflectiveQuadTo(879.0f, 550.0f)
                quadToRelative(-26.0f, 144.0f, -138.0f, 237.0f)
                reflectiveQuadTo(483.0f, 880.0f)
                close()
                moveTo(483.0f, 800.0f)
                quadToRelative(88.0f, 0.0f, 163.0f, -44.0f)
                reflectiveQuadToRelative(118.0f, -121.0f)
                quadToRelative(-86.0f, -8.0f, -163.0f, -43.5f)
                reflectiveQuadTo(463.0f, 495.0f)
                quadToRelative(-61.0f, -61.0f, -97.0f, -138.0f)
                reflectiveQuadToRelative(-43.0f, -163.0f)
                quadToRelative(-77.0f, 43.0f, -120.5f, 118.5f)
                reflectiveQuadTo(159.0f, 476.0f)
                quadToRelative(0.0f, 135.0f, 94.5f, 229.5f)
                reflectiveQuadTo(483.0f, 800.0f)
                close()
                moveTo(463.0f, 495.0f)
                close()
            }
        }
            .build()
        return sleepTimer!!
    }

private var sleepTimer: ImageVector? = null

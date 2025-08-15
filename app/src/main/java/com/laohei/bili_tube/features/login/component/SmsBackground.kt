package com.laohei.bili_tube.features.login.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Path
import androidx.constraintlayout.compose.layoutId


@Composable
internal fun SmsBackground(modifier: Modifier= Modifier) {
    val bgColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .layoutId("background")
            .then(modifier)
            .drawWithCache {
                onDrawBehind {
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width, size.height)
                        quadraticTo(
                            size.width * 0.45f, size.height,
                            0f, size.height * 0.55f
                        )
                        close()
                    }
                    drawPath(
                        path = path,
                        color = bgColor
                    )
                }
            }
    )
}
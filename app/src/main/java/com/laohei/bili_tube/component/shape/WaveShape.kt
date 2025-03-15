package com.laohei.bili_tube.component.shape

import androidx.compose.foundation.shape.GenericShape

internal val WaveShape = GenericShape { size, _ ->
    moveTo(0f, size.height * 0.2f)
    cubicTo(
        size.width * 0.25f, size.height * 0.35f,
        size.width * 0.75f, size.height * 0.15f,
        size.width, size.height * 0.1f
    )
    lineTo(size.width, size.height)
    cubicTo(
        size.width * 0.75f, size.height * 0.65f,
        size.width * 0.25f, size.height * 0.85f,
        0f, size.height * 0.85f
    )
    close()
}
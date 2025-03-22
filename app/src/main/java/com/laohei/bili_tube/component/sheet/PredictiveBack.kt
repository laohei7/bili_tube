package com.laohei.bili_tube.component.sheet

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

private val PredictiveBackEasing: Easing = CubicBezierEasing(0.1f, 0.1f, 0f, 1f)

internal object PredictiveBack {
    internal fun transform(progress: Float) = PredictiveBackEasing.transform(progress)
}

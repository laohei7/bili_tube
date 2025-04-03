package com.laohei.bili_tube.presentation.player

import androidx.compose.ui.graphics.Color

internal const val MAX_LENGTH = 20


internal val portraitAndNotFullscreenGradient = listOf(
    Color.Transparent,
    Color.Black,
    Color.Black,
    Color.Black,
    Color.Black,
    Color.Black,
    Color.Black,
    Color.Black,
    Color.Black,
    Color.Black,
    Color.Black,
    Color.Black,
    Color.Black,
    Color.Black,
    Color.Black,
    Color.Black,
)

internal val portraitAndFullscreenGradient = listOf(
    Color.Black.copy(alpha = 0.8f),
    Color.Transparent,
    Color.Black.copy(alpha = 0.8f),
)

internal val notPortraitGradient = listOf(
    Color.Transparent,
    Color.Transparent,
)
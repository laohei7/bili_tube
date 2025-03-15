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
    Color.Black,
    Color.Black.copy(alpha = 0.5f),
    Color.Transparent,
    Color.Black.copy(alpha = 0.5f),
    Color.Black,
)

internal val notPortraitGradient = listOf(
    Color.Transparent,
    Color.Transparent,
)
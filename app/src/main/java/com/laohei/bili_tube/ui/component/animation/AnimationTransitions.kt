package com.laohei.bili_tube.ui.component.animation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

fun slideFadeHorizontal(isReversed: Boolean): ContentTransform {
    return when {
        isReversed -> {
            slideInHorizontally { it } + fadeIn() togetherWith fadeOut()
        }

        else -> {
            fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
        }
    }
}
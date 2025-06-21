package com.laohei.bili_tube.component.layout

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class AdaptiveType {
    VERTICAL, HORIZONTAL
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AdaptiveLayout(
    modifier: Modifier = Modifier,
    content: @Composable (AdaptiveType) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
    ) {
        content.invoke(
            when {
                maxWidth > maxHeight -> AdaptiveType.HORIZONTAL
                else -> AdaptiveType.VERTICAL
            }
        )
    }
}
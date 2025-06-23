package com.laohei.common_ui.layout

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AdaptiveLayout(
    modifier: Modifier = Modifier,
    content: @Composable (DeviceConfiguration) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
    ) {
        content.invoke(
            DeviceConfiguration.fromWindowSize(maxWidth, maxHeight)
        )
    }
}
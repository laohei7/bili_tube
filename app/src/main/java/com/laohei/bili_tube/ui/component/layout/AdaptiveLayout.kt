package com.laohei.bili_tube.ui.component.layout

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.unit.Dp

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AdaptiveLayout(
    modifier: Modifier = Modifier,
    content: @Composable @UiComposable BoxWithConstraintsScope.(DeviceConfiguration, Dp, Dp) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
    ) {
        content(DeviceConfiguration.fromWindowSize(maxWidth, maxHeight), maxWidth, maxHeight)
    }
}
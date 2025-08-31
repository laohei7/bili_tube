package com.laohei.bili_tube.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun OnOrientationChanged(
    onChanged: (orientation: Int) -> Unit
) {
    val currentOnChanged by rememberUpdatedState(onChanged)
    val configuration = LocalConfiguration.current

    LaunchedEffect(configuration.orientation) {
        currentOnChanged(configuration.orientation)
    }
}

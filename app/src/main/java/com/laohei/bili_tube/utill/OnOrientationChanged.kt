package com.laohei.bili_tube.utill

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun OnOrientationChanged(
    onChanged: (orientation: Int) -> Unit
) {
    val configuration = LocalConfiguration.current

    LaunchedEffect(configuration) {
        snapshotFlow { configuration.orientation }
            .collect { newOrientation ->
                onChanged(newOrientation)
            }
    }
}

package com.laohei.bili_tube.utill

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp


@SuppressLint("DiscouragedApi", "InternalInsetResource")
@Composable
fun rememberStatusBarHeight(): Dp {
    val context = LocalContext.current
    val density = LocalDensity.current
    val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
    val heightPx = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else 0
    return remember { with(density) { heightPx.toDp() } }
}

@SuppressLint("DiscouragedApi", "InternalInsetResource")
@Composable
fun rememberNavigateBarHeight(): Dp {
    val context = LocalContext.current
    val density = LocalDensity.current
    val resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
    val heightPx = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else 0
    return remember { with(density) { heightPx.toDp() } }
}

@Composable
fun rememberHasDisplayCutout(): Boolean {
    val activity = LocalActivity.current
    return remember(Unit) {
        activity?.hasDisplayCutout() == true
    }
}

@Composable
fun isOrientationPortrait(): Boolean {
    val activity = LocalActivity.current
    return activity?.isOrientationPortrait() == true
}
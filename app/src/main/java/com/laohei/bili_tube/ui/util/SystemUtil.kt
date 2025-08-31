package com.laohei.bili_tube.ui.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

object SystemUtil {
    var statusBarHeight = 0
        private set

    var navigateBarHeight = 0
        private set

    const val MAX_ASPECT_RATIO: Float = 16 / 9f
    const val MIN_ASPECT_RATIO: Float = 9 / 16f

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    fun init(context: Context) {
        context.resources.getIdentifier("status_bar_height", "dimen", "android").apply {
            statusBarHeight = context.resources.getDimensionPixelSize(this)
        }
        context.resources.getIdentifier("navigation_bar_height", "dimen", "android").apply {
            navigateBarHeight = context.resources.getDimensionPixelSize(this)
        }
    }

    @Composable
    fun getStatusBarHeightDp(): Dp {
        val density = LocalDensity.current
        return with(density) { statusBarHeight.toDp() }
    }

    @Composable
    fun getNavigateBarHeightDp(): Dp {
        val density = LocalDensity.current
        return with(density) { navigateBarHeight.toDp() }
    }

    @Composable
    fun getSystemBarHeightDp(): Dp {
        return getStatusBarHeightDp() + getNavigateBarHeightDp()
    }
}
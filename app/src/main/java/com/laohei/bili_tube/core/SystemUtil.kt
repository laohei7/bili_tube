package com.laohei.bili_tube.core

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlin.properties.Delegates

object SystemUtil {
    var statusBarHeight by Delegates.notNull<Int>()
        private set

    var navigateBarHeight by Delegates.notNull<Int>()
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
    fun getStatusBarHeightDp(): Dp{
        val density= LocalDensity.current
        return with(density) { statusBarHeight.toDp() }
    }

    @Composable
    fun getNavigateBarHeightDp(): Dp{
        val density= LocalDensity.current
        return with(density) { navigateBarHeight.toDp() }
    }
}
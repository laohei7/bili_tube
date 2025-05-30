package com.laohei.bili_tube.presentation.player.state.screen

import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

internal interface ScreenManager {
    val screenState: StateFlow<ScreenState>

    val nestedScrollConnection: NestedScrollConnection

    fun updateState(other: ScreenState)

    fun onNewDelta(delta: Float): Float

    fun changeFullscreen(fullscreen: Boolean, newHeight: Dp, isOrientationPortrait: Boolean)

    fun handleScreenAction(
        action: ScreenAction,
        isOrientationPortrait: Boolean,
        scope: CoroutineScope? = null,
        lockScreenCallback:(()-> Unit)?=null
    )

    fun changeMaskAlpha(offset: Float)

    fun handleRelatedListDrag(offset: Float)

    fun adjustRelatedListOffset()

    fun calculateScreenSize(vW: Int, vH: Int)
}
package com.laohei.bili_tube.features.player.state.screen

import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

internal interface ScreenController {
    val screenState: StateFlow<ScreenState>

    val nestedScrollConnection: NestedScrollConnection

    fun updateState(state: ScreenState)

    fun onNewDelta(delta: Float): Float

    fun onFullscreenChange(fullscreen: Boolean, newHeight: Dp, isOrientationPortrait: Boolean)

    fun onScreenAction(
        action: ScreenAction,
        isOrientationPortrait: Boolean,
        scope: CoroutineScope? = null,
        onLockScreenCallback:(()-> Unit)?=null
    )

    fun onMaskAlphaChange(offset: Float)

    fun onRelatedListDrag(offset: Float)

    fun applyRelatedListOffset()

    fun computeScreenSize(videoWidth: Int, videoHeight: Int)
}
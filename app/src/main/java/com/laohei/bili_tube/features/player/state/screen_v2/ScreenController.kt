package com.laohei.bili_tube.features.player.state.screen_v2

import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.StateFlow

interface ScreenController {
    val screenState: StateFlow<ScreenState>

    val nestedScrollConnection: NestedScrollConnection

    fun calculateScreenSize(videoWidth: Int, videoHeight: Int)

    fun updateMaskAlpha(offset: Float)

    fun updateFullscreenState(isFullScreen: Boolean, newHeight: Dp, isPortraitMode: Boolean)

    fun handleScreenEvent(event: ScreenEvent, isPortraitMode: Boolean = false)

    fun setOnCallBackListener(listener: CallbackListener)

    interface CallbackListener {
        fun onEvent(event: ScreenEvent)
    }
}
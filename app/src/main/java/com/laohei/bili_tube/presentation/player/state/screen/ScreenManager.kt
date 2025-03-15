package com.laohei.bili_tube.presentation.player.state.screen

import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.Dp
import com.laohei.bili_tube.app.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

internal interface ScreenManager {
    val screenState: StateFlow<ScreenState>

    val nestedScrollConnection: NestedScrollConnection

    fun updateState(other: ScreenState)

    fun onNewDelta(delta: Float): Float

    fun fullscreenChanged(fullscreen: Boolean, newHeight: Dp, isOrientationPortrait: Boolean)

    fun screenActionHandle(
        action: ScreenAction,
        isOrientationPortrait: Boolean,
        scope: CoroutineScope? = null,
        updateParamsCallback: ((Route.Play) -> Unit)? = null
    )

    fun maskAlphaChanged(offset: Float)

    fun relatedListDragHandle(offset: Float)

    fun adjustRelatedListOffset()
}
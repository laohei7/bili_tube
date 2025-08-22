package com.laohei.bili_tube.features.player.state.screen

import android.content.Context
import android.provider.Settings
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.utill.areFloatsEqualCompareTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

internal class DefaultScreenController(
    private val density: Density,
    private val context: Context,
    screenHeight: Int,
    screenWidth: Int,
    videoWidth: Int = 1920,
    videoHeight: Int = 1080
) : ScreenController {

    companion object {
        private val TAG = DefaultScreenController::class.simpleName
    }

    private var _currentAspect: Float? = null
    private val _isAutoRotateEnabled = Settings.System.getInt(
        context.contentResolver, Settings.System.ACCELEROMETER_ROTATION
    ) == 1
    private val _screenState =
        MutableStateFlow(
            computeInitialScreenState(
                screenWidth,
                screenHeight,
                videoWidth,
                videoHeight
            ).copy(isAutoRotateEnabled = _isAutoRotateEnabled)
        )
    override val screenState: StateFlow<ScreenState> = _screenState

    override val nestedScrollConnection: NestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val lazyListState = _screenState.value.listState
            val vertical = available.y
            return when {
                lazyListState.firstVisibleItemIndex != 0 ||
                        lazyListState.firstVisibleItemScrollOffset != 0 -> {
                    Offset.Zero
                }

                else -> {
                    if (_screenState.value.isDrag.not()) {
                        _screenState.update { it.copy(isDrag = true) }
                    }
                    val weConsumed = onNewDelta(vertical)
                    val newVideoHeight =
                        _screenState.value.videoHeight + with(density) { (weConsumed).toDp() }
                    _screenState.update {
                        it.copy(
                            videoHeight = newVideoHeight.coerceIn(
                                it.minimumHeight,
                                _screenState.value.screenHeight.dp
                            )
                        )
                    }
                    Offset(x = 0f, y = weConsumed)
                }
            }
        }

        override suspend fun onPostFling(
            consumed: Velocity,
            available: Velocity
        ): Velocity {
            val lazyListState = _screenState.value.listState
            return when {
                lazyListState.firstVisibleItemIndex != 0
                        || lazyListState.firstVisibleItemScrollOffset != 0 -> {
                    super.onPostFling(consumed, available)
                }

                else -> {
                    // current >= screen -> screen
                    // src >= max and current >= max --> max
                    // src >= max and current < max --> current
                    // src < max and current >= src --> src
                    // src < max and current < src --> current
                    val currentHeight = _screenState.value.videoHeight
                    val isOriginalHigherThanMax =
                        _screenState.value.originalVideoHeight >= _screenState.value.maximumHeight
                    val isCurrentHigherThanMax =
                        currentHeight >= _screenState.value.maximumHeight
                    val isCurrentHigherThanOriginal =
                        currentHeight >= _screenState.value.originalVideoHeight
                    val adjustMinBound =
                        with(density) { (_screenState.value.videoHeight - _screenState.value.minimumHeight).toPx() }
                    _screenState.update {
                        when {
                            currentHeight >= _screenState.value.screenHeight.dp -> {
                                it.copy(
                                    isFullscreen = true,
                                    videoHeight = _screenState.value.screenHeight.dp,
                                )
                            }

                            isOriginalHigherThanMax && isCurrentHigherThanMax -> {
                                it.copy(
                                    videoHeight = it.maximumHeight
                                )
                            }

                            !isOriginalHigherThanMax && isCurrentHigherThanOriginal -> {
                                it.copy(
                                    videoHeight = it.originalVideoHeight
                                )
                            }

                            else -> {
                                it.copy(
                                    isFullscreen = false
                                )
                            }
                        }.copy(
                            basicDelta = 0f,
                            isDrag = false,
                            minBound = -adjustMinBound
                        )
                    }
                    super.onPostFling(consumed, available)
                }
            }
        }
    }

    override fun updateState(state: ScreenState) {
        _screenState.update { state }
    }

    override fun onNewDelta(delta: Float): Float {
        val oldState = _screenState.value.basicDelta
        val newState =
            (_screenState.value.basicDelta + delta)
                .coerceIn(_screenState.value.minBound, _screenState.value.maxBound)
        _screenState.update { it.copy(basicDelta = newState) }
        return newState - oldState
    }

    override fun onFullscreenChange(
        fullscreen: Boolean,
        newHeight: Dp,
        isOrientationPortrait: Boolean
    ) {
        _screenState.update {
            it.copy(
                isDrag = false,
                isFullscreen = fullscreen,
                basicDelta = 0f,
                videoHeight = newHeight,
                minBound = when {
                    isOrientationPortrait -> {
                        with(density) { -(newHeight - it.minimumHeight).toPx() }
                    }

                    else -> it.minBound
                }
            )
        }
    }

    override fun onScreenAction(
        action: ScreenAction,
        isOrientationPortrait: Boolean,
        scope: CoroutineScope?,
        onLockScreenCallback: (() -> Unit)?
    ) {
        when (action) {
            is ScreenAction.SetBackground -> setBackground(action)

            is ScreenAction.SetControlVisible -> setControlVisible(action)

            is ScreenAction.SetLockScreen -> setLockScreen(action, onLockScreenCallback)

            is ScreenAction.SetUpInfoVisible -> setUpInfoVisible(action)

            is ScreenAction.SetPlaylistVisible -> setPlaylistVisible(action)

            is ScreenAction.SetOtherSettingVisible -> setOtherSettingVisible(action)

            is ScreenAction.SetDownloadVisible -> setDownloadVisible(action)

            is ScreenAction.SetLikeAnimationVisible -> setLikeAnimationVisible(action)

            is ScreenAction.SetModifyFolderVisible -> setModifyFolderVisible(action)

            is ScreenAction.SetCreatedFolderVisible -> setCreatedFolderVisible(action)

            is ScreenAction.SetAddCoinVisible -> setAddCoinVisible(action)

            is ScreenAction.SetSettingQualityVisible -> setSettingQualityVisible(action)

            is ScreenAction.SetSettingSpeedVisible -> setSettingSpeedVisible(action)

            is ScreenAction.SetSettingVisible -> setSettingVisible(action)

            is ScreenAction.SetArchiveVisible -> setArchiveVisible(action)

            is ScreenAction.SetVideoDetailVisible -> setVideoDetailVisible(action)

            is ScreenAction.SetReplyVisible -> setReplyVisible(action)

            ScreenAction.Subscribe -> {}

            ScreenAction.NavigateToUserSpace -> {}

            ScreenAction.ShowRelated -> showRelated(
                action as ScreenAction.ShowRelated, isOrientationPortrait, scope
            )

            is ScreenAction.SetVideoMenuVisible -> setVideoMenuVisible(action)

            is ScreenAction.SetUserSwitch -> setUserSwitch(action)
        }
    }

    private fun setUserSwitch(action: ScreenAction.SetUserSwitch) {
        _screenState.update { it.copy(isUserSwitch = action.flag) }
    }

    private fun setModifyFolderVisible(action: ScreenAction.SetModifyFolderVisible) {
        _screenState.update { it.copy(isShowFolderSheet = action.flag) }
    }

    private fun setCreatedFolderVisible(action: ScreenAction.SetCreatedFolderVisible) {
        _screenState.update {
            it.copy(
                isShowFolderSheet = action.flag.not(),
                isShowAddFolder = action.flag
            )
        }
    }

    private fun setAddCoinVisible(action: ScreenAction.SetAddCoinVisible) {
        _screenState.update { it.copy(isShowAddCoinUI = action.flag) }
    }

    private fun setSettingQualityVisible(action: ScreenAction.SetSettingQualityVisible) {
        _screenState.update { it.copy(isShowQualityUI = action.flag) }
    }

    private fun setSettingSpeedVisible(action: ScreenAction.SetSettingSpeedVisible) {
        _screenState.update { it.copy(isShowSpeedUI = action.flag) }
    }

    private fun setSettingVisible(action: ScreenAction.SetSettingVisible) {
        _screenState.update { it.copy(isShowVideoSettingsSheet = action.flag) }
    }

    private fun setArchiveVisible(action: ScreenAction.SetArchiveVisible) {
        _screenState.update {
            it.copy(
                isShowArchiveUI = action.flag,
                videoHeight = it.minimumHeight
            )
        }
    }

    private fun setVideoDetailVisible(action: ScreenAction.SetVideoDetailVisible) {
        _screenState.update {
            it.copy(
                isShowVideoDetailUI = action.flag,
                videoHeight = it.minimumHeight
            )
        }
    }

    private fun setReplyVisible(action: ScreenAction.SetReplyVisible) {
        _screenState.update {
            it.copy(
                isShowReplyUI = action.flag,
                videoHeight = it.minimumHeight
            )
        }
    }

    private fun showRelated(
        action: ScreenAction.ShowRelated,
        isOrientationPortrait: Boolean,
        scope: CoroutineScope?
    ) {
        val state = _screenState.value
        when {
            isOrientationPortrait && state.isFullscreen -> {
                val lazyListState = state.listState
                onFullscreenChange(false, state.minimumHeight, true)
                scope?.launch {
                    lazyListState.animateScrollToItem(5)
                }
            }

            !isOrientationPortrait && state.isFullscreen -> {
                val newShowState = state.isShowRelatedList.not()
                _screenState.update {
                    it.copy(
                        relatedListOffset = if (newShowState) 0f else 500f,
                        isShowRelatedList = newShowState
                    )
                }
            }
        }
    }

    private fun setVideoMenuVisible(action: ScreenAction.SetVideoMenuVisible) {
        _screenState.update { it.copy(isShowVideoMenuUIAction = action.flag) }
    }

    private fun setBackground(action: ScreenAction.SetBackground) {
        _screenState.update { it.copy(background = action.bitmap) }
    }

    private fun setControlVisible(action: ScreenAction.SetControlVisible) {
        _screenState.update { it.copy(isShowControlUI = action.flag) }
    }

    private fun setLockScreen(
        action: ScreenAction.SetLockScreen,
        onLockScreenCallback: (() -> Unit)?
    ) {
        _screenState.update {
            it.copy(
                isFullscreen = true,
                isLockScreen = action.flag,
                isShowControlUI = false
            )
        }
        onLockScreenCallback?.invoke()
    }

    private fun setUpInfoVisible(action: ScreenAction.SetUpInfoVisible) {
        _screenState.update {
            it.copy(
                isShowUpInfoSheet = action.flag,
                videoHeight = it.minimumHeight
            )
        }
    }

    private fun setPlaylistVisible(action: ScreenAction.SetPlaylistVisible) {
        _screenState.update {
            it.copy(
                isShowPlaylistSheet = action.flag,
                videoHeight = it.minimumHeight
            )
        }
    }

    private fun setOtherSettingVisible(action: ScreenAction.SetOtherSettingVisible) {
        _screenState.update { it.copy(isShowOtherSettingUI = action.flag) }
    }

    private fun setDownloadVisible(action: ScreenAction.SetDownloadVisible) {
        _screenState.update { it.copy(isShowDownloadSheet = action.flag) }
    }

    private fun setLikeAnimationVisible(action: ScreenAction.SetLikeAnimationVisible) {
        _screenState.update { it.copy(isShowLikeAnimation = action.flag) }
    }

    override fun onMaskAlphaChange(offset: Float) {
        val state = _screenState.value
        when {
            shouldShowOverlayMask() -> {
                val maxOffset =
                    with(density) { (state.screenHeight.dp - state.videoHeight).toPx() }
                _screenState.update {
                    it.copy(maskAlpha = (1f - (offset / maxOffset)).coerceIn(0f, 1f))
                }
            }
        }
    }

    override fun onRelatedListDrag(offset: Float) {
        val newOffset = (_screenState.value.relatedListOffset + offset).coerceIn(0f, 500f)
        _screenState.update {
            it.copy(relatedListOffset = newOffset)
        }
    }

    override fun applyRelatedListOffset() {
        val isReachedShow = _screenState.value.relatedListOffset < 200f
        _screenState.update {
            it.copy(
                isShowRelatedList = isReachedShow,
                relatedListOffset = if (isReachedShow) 0f else with(density) { 500.dp.toPx() }
            )
        }
    }

    override fun computeScreenSize(videoWidth: Int, videoHeight: Int) {
        val newAspectRatio = videoWidth.toFloat() / videoHeight.toFloat()
        if (newAspectRatio.areFloatsEqualCompareTo(_currentAspect)) {
            return
        }
        _currentAspect = newAspectRatio
        val newState =
            computeInitialScreenState(
                _screenState.value.screenWidth,
                _screenState.value.screenHeight,
                videoWidth,
                videoHeight
            )
        _screenState.update {
            it.copy(
                originalVideoHeight = newState.originalVideoHeight,
                videoHeight = newState.originalVideoHeight,
                minBound = newState.minBound,
                maxBound = newState.maxBound
            )
        }
    }

    private fun computeInitialScreenState(
        screenWidth: Int,
        screenHeight: Int,
        videoWidth: Int,
        videoHeight: Int
    ): ScreenState {
        val minimumHeight = min(screenWidth, screenHeight) * 9f / 16f
        val maximumHeight = max(screenWidth, screenHeight) * 2 / 3f
        val originalVideoHeight =
            (min(screenWidth, screenHeight) * videoHeight.toFloat() / videoWidth.toFloat())
                .coerceIn(minimumHeight, maximumHeight)
        val minBoundPx = with(density) { (originalVideoHeight - minimumHeight).dp.toPx() }
        val maxBoundPx = with(density) { screenHeight.dp.toPx() }
        return ScreenState(
            screenWidth = screenWidth,
            screenHeight = screenHeight,
            maximumHeight = maximumHeight.dp,
            minimumHeight = minimumHeight.dp,
            videoHeight = originalVideoHeight.dp,
            minBound = -minBoundPx,
            maxBound = maxBoundPx,
            relatedListOffset = with(density) { 500.dp.toPx() } // TODO: avoiding writing dead fixed values
        )
    }

    private fun shouldShowOverlayMask(): Boolean {
        val state = _screenState.value
        return listOf(
            state.isShowVideoDetailUI,
            state.isShowReplyUI,
            state.isShowArchiveUI,
            state.isShowUpInfoSheet,
            state.isShowPlaylistSheet
        ).any { it }
    }
}
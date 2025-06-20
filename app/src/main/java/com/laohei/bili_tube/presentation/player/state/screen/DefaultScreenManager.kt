package com.laohei.bili_tube.presentation.player.state.screen

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
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

internal class DefaultScreenManager(
    private val density: Density,
    screenHeight: Int,
    screenWidth: Int,
    width: Int = 1920,
    height: Int = 1080
) : ScreenManager {

    companion object {
        private val TAG = DefaultScreenManager::class.simpleName
    }

    private var _mCurrentAspectRation: Float? = null
    private val _mState = MutableStateFlow(getScreenState(screenWidth, screenHeight, width, height))
    override val screenState: StateFlow<ScreenState>
        get() = _mState
    override val nestedScrollConnection: NestedScrollConnection
        get() = object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val lazyListState = _mState.value.listState
                val vertical = available.y
                return when {
                    lazyListState.firstVisibleItemIndex != 0 ||
                            lazyListState.firstVisibleItemScrollOffset != 0 -> {
                        Offset.Zero
                    }

                    else -> {
                        if (_mState.value.isDrag.not()) {
                            _mState.update { it.copy(isDrag = true) }
                        }
                        val weConsumed = onNewDelta(vertical)
                        val newVideoHeight =
                            _mState.value.videoHeight + with(density) { (weConsumed).toDp() }
                        _mState.update {
                            it.copy(
                                videoHeight = newVideoHeight.coerceIn(
                                    it.minLimitedHeight,
                                    _mState.value.screenHeight.dp
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
                val lazyListState = _mState.value.listState
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
                        val currentHeight = _mState.value.videoHeight
                        val isOriginalHigherThanMax =
                            _mState.value.originalVideoHeight >= _mState.value.maxLimitedHeight
                        val isCurrentHigherThanMax = currentHeight >= _mState.value.maxLimitedHeight
                        val isCurrentHigherThanOriginal =
                            currentHeight >= _mState.value.originalVideoHeight
                        val adjustMinBound =
                            with(density) { (_mState.value.videoHeight - _mState.value.minLimitedHeight).toPx() }
                        _mState.update {
                            when {
                                currentHeight >= _mState.value.screenHeight.dp -> {
                                    it.copy(
                                        isFullscreen = true,
                                        videoHeight = _mState.value.screenHeight.dp,
                                    )
                                }

                                isOriginalHigherThanMax && isCurrentHigherThanMax -> {
                                    it.copy(
                                        videoHeight = it.maxLimitedHeight
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

    override fun updateState(other: ScreenState) {
        _mState.update { other }
    }

    override fun onNewDelta(delta: Float): Float {
        val oldState = _mState.value.basicDelta
        val newState =
            (_mState.value.basicDelta + delta).coerceIn(
                _mState.value.minBound,
                _mState.value.maxBound
            )
        _mState.update { it.copy(basicDelta = newState) }
        return newState - oldState
    }

    override fun changeFullscreen(
        fullscreen: Boolean,
        newHeight: Dp,
        isOrientationPortrait: Boolean
    ) {
        _mState.update {
            it.copy(
                isDrag = false,
                isFullscreen = fullscreen,
                basicDelta = 0f,
                videoHeight = newHeight,
                minBound = when {
                    isOrientationPortrait -> {
                        with(density) { -(newHeight - it.minLimitedHeight).toPx() }
                    }

                    else -> it.minBound
                }
            )
        }
    }

    override fun handleScreenAction(
        action: ScreenAction,
        isOrientationPortrait: Boolean,
        scope: CoroutineScope?,
        lockScreenCallback: (() -> Unit)?
    ) {
        when (action) {
            is ScreenAction.LockScreenAction -> {
                _mState.update {
                    it.copy(
                        isFullscreen = true,
                        isLockScreen = action.flag,
                        isShowUI = false
                    )
                }
                lockScreenCallback?.invoke()
            }

            is ScreenAction.ShowUpInfoSheetAction -> {
                _mState.update {
                    it.copy(
                        isShowUpInfoSheet = action.flag,
                        videoHeight = it.minLimitedHeight
                    )
                }
            }

            is ScreenAction.ShowPlaylistSheetAction -> {
                _mState.update {
                    it.copy(
                        isShowPlaylistSheet = action.flag,
                        videoHeight = it.minLimitedHeight
                    )
                }
            }

            is ScreenAction.ShowOtherSettingsSheetAction -> {
                _mState.update { it.copy(isShowOtherSettingsSheet = action.flag) }
            }

            is ScreenAction.ShowDownloadSheetAction -> {
                _mState.update { it.copy(isShowDownloadSheet = action.flag) }
            }

            is ScreenAction.ShowLikeAnimationAction -> {
                _mState.update { it.copy(isShowLikeAnimation = action.flag) }
            }

            is ScreenAction.ShowFolderSheetAction -> {
                _mState.update { it.copy(isShowFolderSheet = action.flag) }
            }

            is ScreenAction.CreatedFolderAction -> {
                _mState.update {
                    it.copy(
                        isShowFolderSheet = action.flag.not(),
                        isShowAddFolder = action.flag
                    )
                }
            }

            is ScreenAction.ShowCoinSheetAction -> {
                _mState.update { it.copy(isShowCoinSheet = action.flag) }
            }

            is ScreenAction.ShowQualitySheetAction -> {
                _mState.update { it.copy(isShowQualitySheet = action.flag) }
            }

            is ScreenAction.ShowSpeedSheetAction -> {
                _mState.update { it.copy(isShowSpeedSheet = action.flag) }
            }

            is ScreenAction.ShowSettingsSheetAction -> {
                _mState.update { it.copy(isShowVideoSettingsSheet = action.flag) }
            }

            is ScreenAction.ShowArchiveSheetAction -> {
                _mState.update {
                    it.copy(
                        isShowArchiveSheet = action.flag,
                        videoHeight = it.minLimitedHeight
                    )
                }
            }

            ScreenAction.ShowVideoDetailAction -> {
                _mState.update {
                    it.copy(
                        isShowDetailSheet = true,
                        videoHeight = it.minLimitedHeight
                    )
                }
            }

            ScreenAction.ShowReplyAction -> {
                _mState.update {
                    it.copy(
                        isShowReplySheet = true,
                        videoHeight = it.minLimitedHeight
                    )
                }
            }

            ScreenAction.SubscribeAction -> {

            }

            ScreenAction.ToUserSpaceAction -> {

            }

            ScreenAction.ShowRelatedAction -> {
                when {
                    isOrientationPortrait && _mState.value.isFullscreen -> {
                        val lazyListState = _mState.value.listState
                        changeFullscreen(false, _mState.value.minLimitedHeight, true)
                        scope?.launch {
                            lazyListState.animateScrollToItem(5)
                        }
                    }

                    !isOrientationPortrait && _mState.value.isFullscreen -> {
                        val newShowState = _mState.value.isShowRelatedList.not()
                        _mState.update {
                            it.copy(
                                relatedListOffset = if (newShowState) 0f else 500f,
                                isShowRelatedList = newShowState
                            )
                        }
                    }
                }
            }
        }
    }

    override fun changeMaskAlpha(offset: Float) {
        when {
            isShowMask() -> {
                val maxOffset =
                    with(density) { (_mState.value.screenHeight.dp - _mState.value.videoHeight).toPx() }
                _mState.update {
                    it.copy(
                        maskAlpha = (1f - (offset / maxOffset)).coerceIn(0f, 1f)
                    )
                }
            }
        }
    }

    override fun handleRelatedListDrag(offset: Float) {
        val newOffset = (_mState.value.relatedListOffset + offset).coerceIn(0f, 500f)
        _mState.update {
            it.copy(relatedListOffset = newOffset)
        }
    }

    override fun adjustRelatedListOffset() {
        val isReachedShow = _mState.value.relatedListOffset < 200f
        _mState.update {
            it.copy(
                isShowRelatedList = isReachedShow,
                relatedListOffset = if (isReachedShow) 0f else with(density) { 500.dp.toPx() }
            )
        }
    }

    override fun calculateScreenSize(vW: Int, vH: Int) {
        val newAspectRatio = vW.toFloat() / vH.toFloat()
        if (newAspectRatio.areFloatsEqualCompareTo(_mCurrentAspectRation)) {
            return
        }
        _mCurrentAspectRation = newAspectRatio
        val newState = getScreenState(_mState.value.screenWidth, _mState.value.screenHeight, vW, vH)
        _mState.update {
            it.copy(
                originalVideoHeight = newState.originalVideoHeight,
                videoHeight = newState.originalVideoHeight,
                minBound = newState.minBound,
                maxBound = newState.maxBound
            )
        }
    }

    private fun getScreenState(w: Int, h: Int, vW: Int, vH: Int): ScreenState {
        Log.d(TAG, "getScreenState: $w $h $vW $vH")
        val minLimitedHeight = w * 9f / 16f
        val maxLimitedHeight = h * 2 / 3f
        val originalVideoHeight = (w * vH.toFloat() / vW.toFloat())
            .coerceIn(minLimitedHeight, maxLimitedHeight)
        val minBoundPx = with(density) { (originalVideoHeight - minLimitedHeight).dp.toPx() }
        val maxBoundPx = with(density) { h.dp.toPx() }
        return ScreenState(
            screenWidth = w,
            screenHeight = h,
            maxLimitedHeight = maxLimitedHeight.dp,
            minLimitedHeight = minLimitedHeight.dp,
            videoHeight = originalVideoHeight.dp,
            listState = LazyListState(),
            minBound = -minBoundPx,
            maxBound = maxBoundPx,
            relatedListOffset = with(density) { 500.dp.toPx() }
        )
    }

    private fun isShowMask(): Boolean {
        return _mState.value.isShowDetailSheet || _mState.value.isShowReplySheet
                || _mState.value.isShowArchiveSheet || _mState.value.isShowUpInfoSheet
                || _mState.value.isShowPlaylistSheet
    }
}
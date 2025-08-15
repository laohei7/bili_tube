package com.laohei.bili_tube.features.player.state.screen

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
import kotlin.math.max
import kotlin.math.min

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
    private val mScreenState =
        MutableStateFlow(getScreenState(screenWidth, screenHeight, width, height))
    override val screenState: StateFlow<ScreenState>
        get() = mScreenState
    override val nestedScrollConnection: NestedScrollConnection
        get() = object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val lazyListState = mScreenState.value.listState
                val vertical = available.y
                return when {
                    lazyListState.firstVisibleItemIndex != 0 ||
                            lazyListState.firstVisibleItemScrollOffset != 0 -> {
                        Offset.Zero
                    }

                    else -> {
                        if (mScreenState.value.isDrag.not()) {
                            mScreenState.update { it.copy(isDrag = true) }
                        }
                        val weConsumed = onNewDelta(vertical)
                        val newVideoHeight =
                            mScreenState.value.videoHeight + with(density) { (weConsumed).toDp() }
                        mScreenState.update {
                            it.copy(
                                videoHeight = newVideoHeight.coerceIn(
                                    it.minLimitedHeight,
                                    mScreenState.value.screenHeight.dp
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
                val lazyListState = mScreenState.value.listState
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
                        val currentHeight = mScreenState.value.videoHeight
                        val isOriginalHigherThanMax =
                            mScreenState.value.originalVideoHeight >= mScreenState.value.maxLimitedHeight
                        val isCurrentHigherThanMax =
                            currentHeight >= mScreenState.value.maxLimitedHeight
                        val isCurrentHigherThanOriginal =
                            currentHeight >= mScreenState.value.originalVideoHeight
                        val adjustMinBound =
                            with(density) { (mScreenState.value.videoHeight - mScreenState.value.minLimitedHeight).toPx() }
                        mScreenState.update {
                            when {
                                currentHeight >= mScreenState.value.screenHeight.dp -> {
                                    it.copy(
                                        isFullscreen = true,
                                        videoHeight = mScreenState.value.screenHeight.dp,
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
        mScreenState.update { other }
    }

    override fun onNewDelta(delta: Float): Float {
        val oldState = mScreenState.value.basicDelta
        val newState =
            (mScreenState.value.basicDelta + delta).coerceIn(
                mScreenState.value.minBound,
                mScreenState.value.maxBound
            )
        mScreenState.update { it.copy(basicDelta = newState) }
        return newState - oldState
    }

    override fun onFullscreenChange(
        fullscreen: Boolean,
        newHeight: Dp,
        isOrientationPortrait: Boolean
    ) {
        mScreenState.update {
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

    override fun onScreenAction(
        action: ScreenAction,
        isOrientationPortrait: Boolean,
        scope: CoroutineScope?,
        lockScreenCallback: (() -> Unit)?
    ) {
        when (action) {
            is ScreenAction.SetBackgroundAction -> {
                mScreenState.update { it.copy(background = action.bitmap) }
            }

            is ScreenAction.ShowControlUIAction -> {
                mScreenState.update { it.copy(isShowControlUI = action.flag) }
            }

            is ScreenAction.LockScreenAction -> {
                mScreenState.update {
                    it.copy(
                        isFullscreen = true,
                        isLockScreen = action.flag,
                        isShowControlUI = false
                    )
                }
                lockScreenCallback?.invoke()
            }

            is ScreenAction.UpInfoUIAction -> {
                mScreenState.update {
                    it.copy(
                        isShowUpInfoSheet = action.flag,
                        videoHeight = it.minLimitedHeight
                    )
                }
            }

            is ScreenAction.ShowPlaylistSheetAction -> {
                mScreenState.update {
                    it.copy(
                        isShowPlaylistSheet = action.flag,
                        videoHeight = it.minLimitedHeight
                    )
                }
            }

            is ScreenAction.OtherSettingUIAction -> {
                mScreenState.update { it.copy(isShowOtherSettingUI = action.flag) }
            }

            is ScreenAction.DownloadUIAction -> {
                mScreenState.update { it.copy(isShowDownloadSheet = action.flag) }
            }

            is ScreenAction.ShowLikeAnimationAction -> {
                mScreenState.update { it.copy(isShowLikeAnimation = action.flag) }
            }

            is ScreenAction.ModifyFolderUIAction -> {
                mScreenState.update { it.copy(isShowFolderSheet = action.flag) }
            }

            is ScreenAction.CreatedFolderUIAction -> {
                mScreenState.update {
                    it.copy(
                        isShowFolderSheet = action.flag.not(),
                        isShowAddFolder = action.flag
                    )
                }
            }

            is ScreenAction.AddCoinUIAction -> {
                mScreenState.update { it.copy(isShowAddCoinUI = action.flag) }
            }

            is ScreenAction.SettingQualityUIAction -> {
                mScreenState.update { it.copy(isShowQualityUI = action.flag) }
            }

            is ScreenAction.SettingSpeedUIAction -> {
                mScreenState.update { it.copy(isShowSpeedUI = action.flag) }
            }

            is ScreenAction.SettingUIAction -> {
                mScreenState.update { it.copy(isShowVideoSettingsSheet = action.flag) }
            }

            is ScreenAction.ArchiveUIAction -> {
                mScreenState.update {
                    it.copy(
                        isShowArchiveUI = action.flag,
                        videoHeight = it.minLimitedHeight
                    )
                }
            }

            is ScreenAction.VideoDetailUIAction -> {
                mScreenState.update {
                    it.copy(
                        isShowVideoDetailUI = action.flag,
                        videoHeight = it.minLimitedHeight
                    )
                }
            }

            is ScreenAction.ReplyUIAction -> {
                mScreenState.update {
                    it.copy(
                        isShowReplyUI = action.flag,
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
                    isOrientationPortrait && mScreenState.value.isFullscreen -> {
                        val lazyListState = mScreenState.value.listState
                        onFullscreenChange(false, mScreenState.value.minLimitedHeight, true)
                        scope?.launch {
                            lazyListState.animateScrollToItem(5)
                        }
                    }

                    !isOrientationPortrait && mScreenState.value.isFullscreen -> {
                        val newShowState = mScreenState.value.isShowRelatedList.not()
                        mScreenState.update {
                            it.copy(
                                relatedListOffset = if (newShowState) 0f else 500f,
                                isShowRelatedList = newShowState
                            )
                        }
                    }
                }
            }

            is ScreenAction.VideoMenuUIAction -> {
                mScreenState.update { it.copy(isShowVideoMenuUIAction = action.flag) }
            }
        }
    }

    override fun onMaskAlphaChange(offset: Float) {
        when {
            isShowMask() -> {
                val maxOffset =
                    with(density) { (mScreenState.value.screenHeight.dp - mScreenState.value.videoHeight).toPx() }
                mScreenState.update {
                    it.copy(
                        maskAlpha = (1f - (offset / maxOffset)).coerceIn(0f, 1f)
                    )
                }
            }
        }
    }

    override fun handleRelatedListDrag(offset: Float) {
        val newOffset = (mScreenState.value.relatedListOffset + offset).coerceIn(0f, 500f)
        mScreenState.update {
            it.copy(relatedListOffset = newOffset)
        }
    }

    override fun adjustRelatedListOffset() {
        val isReachedShow = mScreenState.value.relatedListOffset < 200f
        mScreenState.update {
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
        val newState =
            getScreenState(mScreenState.value.screenWidth, mScreenState.value.screenHeight, vW, vH)
        mScreenState.update {
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
        val minLimitedHeight = min(w, h) * 9f / 16f
        val maxLimitedHeight = max(w, h) * 2 / 3f
        val originalVideoHeight = (min(w, h) * vH.toFloat() / vW.toFloat())
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
        return mScreenState.value.isShowVideoDetailUI || mScreenState.value.isShowReplyUI
                || mScreenState.value.isShowArchiveUI || mScreenState.value.isShowUpInfoSheet
                || mScreenState.value.isShowPlaylistSheet
    }
}
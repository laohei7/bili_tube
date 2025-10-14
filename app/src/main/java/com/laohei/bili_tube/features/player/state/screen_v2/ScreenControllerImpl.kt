package com.laohei.bili_tube.features.player.state.screen_v2

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.max
import kotlin.math.min

class ScreenControllerImpl(
    private val density: Density,
    context: Context,
    screenWidth: Int,
    screenHeight: Int,
    videoWidth: Int = 1920,
    videoHeight: Int = 1080
) : ScreenController {
    companion object {
        private val TAG = ScreenControllerImpl::class.simpleName
    }

    private val _uiState = MutableStateFlow(
        calculateVideoLayoutState(screenWidth, screenHeight, videoWidth, videoHeight)
    )
    override val screenState: StateFlow<ScreenState> = _uiState.asStateFlow()

    private var currentAspect: Float = 16 / 9f

    private var callbackListener: ScreenController.CallbackListener? = null

    override val nestedScrollConnection: NestedScrollConnection = object : NestedScrollConnection {

        private val lazyListState get() = _uiState.value.contentScrollState

        // Helper function to check if the list is at the top
        private fun isListAtTop(): Boolean =
            lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val vertical = available.y
            return if (!isListAtTop()) {
                // If list isn't at the top, we don't want to consume scroll
                Offset.Zero
            } else {
                if (!_uiState.value.isDragging) {
                    _uiState.update { it.copy(isDragging = true) }
                }

                val deltaConsumed = onNewDelta(vertical)

                val newVideoHeight =
                    _uiState.value.currentVideoHeight + with(density) { deltaConsumed.toDp() }
                _uiState.update {
                    it.copy(
                        currentVideoHeight = newVideoHeight.coerceIn(
                            it.minHeight, _uiState.value.screenHeight.dp
                        )
                    )
                }
                // Return the consumed delta as the scroll offset
                Offset(x = 0f, y = deltaConsumed)
            }
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            return if (!isListAtTop()) {
                super.onPostFling(consumed, available)
            } else {
                handlePostFling()
                super.onPostFling(consumed, available)
            }
        }

        // Helper function to handle the post-fling logic
        private fun handlePostFling() {
            val currentHeight = _uiState.value.currentVideoHeight
            val isOriginalHigherThanMax =
                _uiState.value.initialVideoHeight >= _uiState.value.maxHeight
            val isCurrentHigherThanMax = currentHeight >= _uiState.value.maxHeight
            val isCurrentHigherThanOriginal =
                currentHeight >= _uiState.value.initialVideoHeight

            val adjustMinBound =
                with(density) { (_uiState.value.currentVideoHeight - _uiState.value.minHeight).toPx() }

            _uiState.update {
                when {
                    currentHeight >= _uiState.value.screenHeight.dp -> {
                        it.copy(
                            isFullScreenActive = true,
                            currentVideoHeight = _uiState.value.screenHeight.dp
                        )
                    }

                    isOriginalHigherThanMax && isCurrentHigherThanMax -> {
                        it.copy(currentVideoHeight = it.maxHeight)
                    }

                    !isOriginalHigherThanMax && isCurrentHigherThanOriginal -> {
                        it.copy(currentVideoHeight = it.initialVideoHeight)
                    }

                    else -> {
                        it.copy(isFullScreenActive = false)
                    }
                }.copy(
                    dragDelta = 0f,
                    isDragging = false,
                    minLimit = -adjustMinBound
                )
            }
        }
    }

    private fun onNewDelta(delta: Float): Float {
        val oldState = _uiState.value.dragDelta
        val newState =
            (_uiState.value.dragDelta + delta).coerceIn(
                _uiState.value.minLimit,
                _uiState.value.maxLimit
            )
        _uiState.update { it.copy(dragDelta = newState) }
        return newState - oldState
    }

    private fun calculateVideoLayoutState(
        screenWidth: Int,
        screenHeight: Int,
        videoWidth: Int,
        videoHeight: Int,
        relatedListOffset: Dp = 500.dp // Use parameter to avoid hardcoded values
    ): ScreenState {
        val screenMin = min(screenWidth, screenHeight)
        val screenMax = max(screenWidth, screenHeight)

        val minimumHeight = screenMin * 9f / 16f
        val maximumHeight = screenMax * 2 / 3f

        val originalVideoHeight = (screenMin * videoHeight.toFloat() / videoWidth.toFloat())
            .coerceIn(minimumHeight, maximumHeight)

        val minBoundPx = with(density) { (originalVideoHeight - minimumHeight).dp.toPx() }
        val maxBoundPx = with(density) { screenHeight.dp.toPx() }
        val relatedListOffsetPx = with(density) { relatedListOffset.toPx() }

        return ScreenState(
            screenWidth = screenWidth,
            screenHeight = screenHeight,
            maxHeight = maximumHeight.dp,
            minHeight = minimumHeight.dp,
            currentVideoHeight = originalVideoHeight.dp,
            minLimit = -minBoundPx,
            maxLimit = maxBoundPx,
            relatedListOffsetY = relatedListOffsetPx,
            dragDelta = 0f
        )
    }

    override fun calculateScreenSize(videoWidth: Int, videoHeight: Int) {
        val newAspectRatio = videoWidth.toFloat() / videoHeight.toFloat()
//        if (newAspectRatio.isNearlyEqual(currentAspect)) {
//            return
//        }
        currentAspect = newAspectRatio
        val newState =
            calculateVideoLayoutState(
                _uiState.value.screenWidth,
                _uiState.value.screenHeight,
                videoWidth,
                videoHeight
            )

        _uiState.update {
            it.copy(
                initialVideoHeight = newState.initialVideoHeight,
                currentVideoHeight = if (it.isFullScreenActive) {
                    it.currentVideoHeight
                } else {
                    newState.initialVideoHeight
                },
                minLimit = newState.minLimit,
                maxLimit = newState.maxLimit,
                dragDelta = newState.dragDelta
            )
        }
    }

    override fun updateMaskAlpha(offset: Float) {
        val state = _uiState.value
        when {
            shouldShowOverlayMask() -> {
                val maxOffset =
                    with(density) { (state.screenHeight.dp - state.currentVideoHeight).toPx() }
                _uiState.update {
                    it.copy(maskOpacity = (1f - (offset / maxOffset)).coerceIn(0f, 1f))
                }
            }
        }
    }

    override fun updateFullscreenState(
        isFullScreen: Boolean,
        newHeight: Dp,
        isPortraitMode: Boolean
    ) {
        _uiState.update {
            it.copy(
                isDragging = false,
                isFullScreenActive = isFullScreen,
                dragDelta = 0f,
                currentVideoHeight = newHeight,
                minLimit = when {
                    isPortraitMode -> with(density) { -(newHeight - it.minHeight).toPx() }

                    else -> it.minLimit
                }
            )
        }
    }

    override fun handleScreenEvent(
        event: ScreenEvent,
        isPortraitMode: Boolean
    ) {
        when (event) {
            is ScreenEvent.SetBackgroundImage -> handleSetBackgroundImage(event)
            is ScreenEvent.AddCoinVisibility -> handleAddCoinVisibility(event)
            is ScreenEvent.ArchiveVisibility -> handleArchiveVisibility(event)
            is ScreenEvent.ControlVisibility -> handleControlVisibility(event)
            is ScreenEvent.DownloadVisibility -> handleDownloadVisibility(event)
            is ScreenEvent.FolderCreationVisibility -> handleFolderCreationVisibility(event)
            is ScreenEvent.FolderMediaVisibility -> handleFolderMediaVisibility(event)
            is ScreenEvent.FolderModificationVisibility -> handleFolderModificationVisibility(event)
            is ScreenEvent.HintVisibility -> handleHintVisibility(event)
            is ScreenEvent.LikeAnimationVisibility -> handleLikeAnimationVisibility(event)
            is ScreenEvent.OtherSettingsVisibility -> handleOtherSettingsVisibility(event)
            is ScreenEvent.PlaylistVisibility -> handlePlaylistVisibility(event)
            is ScreenEvent.QualitySettingsVisibility -> handleQualitySettingsVisibility(event)
            is ScreenEvent.ReplyVisibility -> handleReplyVisibility(event)
            is ScreenEvent.SettingsVisibility -> handleSettingsVisibility(event)
            is ScreenEvent.SpeedSettingsVisibility -> handleSpeedSettingsVisibility(event)
            is ScreenEvent.UpInfoVisibility -> handleUpInfoVisibility(event)
            is ScreenEvent.VideoDetailVisibility -> handleVideoDetailVisibility(event)
            is ScreenEvent.VideoMenuVisibility -> handleVideoMenuVisibility(event)
            is ScreenEvent.WatchLaterVisibility -> handleWatchLaterVisibility(event)
            is ScreenEvent.SetUserFullscreenToggle -> handleSetUserFullscreenToggle(event)
            is ScreenEvent.ResetScrollPosition -> handleResetScrollPosition()
        }
    }

    private fun handleResetScrollPosition() {
        _uiState.update {
            it.copy(
                contentScrollState = LazyListState(),
                archiveListScrollState = LazyListState(),
                watchLaterListState = LazyListState(),
                folderMediaListState = LazyListState(),
            )
        }
    }

    private fun handleSetUserFullscreenToggle(event: ScreenEvent.SetUserFullscreenToggle) {
        _uiState.update { it.copy(isUserFullscreenToggle = event.flag) }
    }

    private fun handleWatchLaterVisibility(event: ScreenEvent.WatchLaterVisibility) {
        _uiState.update {
            it.copy(isWatchLaterVisible = event.isVisible, currentVideoHeight = it.minHeight)
        }
    }

    private fun handleVideoMenuVisibility(event: ScreenEvent.VideoMenuVisibility) {
        _uiState.update { it.copy(showVideoMenuUIAction = event.isVisible) }
    }

    private fun handleVideoDetailVisibility(event: ScreenEvent.VideoDetailVisibility) {
        _uiState.update {
            it.copy(showVideoDetailUI = event.isVisible, currentVideoHeight = it.minHeight)
        }
    }

    private fun handleUpInfoVisibility(event: ScreenEvent.UpInfoVisibility) {
        _uiState.update {
            it.copy(showUpInfoSheet = event.isVisible, currentVideoHeight = it.minHeight)
        }
    }

    private fun handleSpeedSettingsVisibility(event: ScreenEvent.SpeedSettingsVisibility) {
        _uiState.update { it.copy(showSpeedUI = event.isVisible, showVideoSettingsSheet = false) }
    }

    private fun handleSettingsVisibility(event: ScreenEvent.SettingsVisibility) {
        _uiState.update { it.copy(showVideoSettingsSheet = event.isVisible) }
    }

    private fun handleReplyVisibility(event: ScreenEvent.ReplyVisibility) {
        _uiState.update {
            it.copy(showReplyUI = event.isVisible, currentVideoHeight = it.minHeight)
        }
    }

    private fun handleQualitySettingsVisibility(event: ScreenEvent.QualitySettingsVisibility) {
        _uiState.update { it.copy(showQualityUI = event.isVisible, showVideoSettingsSheet = false) }
    }

    private fun handlePlaylistVisibility(event: ScreenEvent.PlaylistVisibility) {
        _uiState.update {
            it.copy(showPlaylistSheet = event.isVisible, currentVideoHeight = it.minHeight)
        }
    }

    private fun handleOtherSettingsVisibility(event: ScreenEvent.OtherSettingsVisibility) {
        _uiState.update {
            it.copy(
                showOtherSettingUI = event.isVisible,
                showVideoSettingsSheet = false
            )
        }
    }

    private fun handleLikeAnimationVisibility(event: ScreenEvent.LikeAnimationVisibility) {
        _uiState.update { it.copy(showLikeAnimation = event.isVisible) }
    }

    private fun handleAddCoinVisibility(event: ScreenEvent.AddCoinVisibility) {
        _uiState.update { it.copy(showAddCoinUI = event.isVisible) }
    }

    private fun handleArchiveVisibility(event: ScreenEvent.ArchiveVisibility) {
        _uiState.update { it.copy(showArchiveUI = event.isVisible) }
    }

    private fun handleControlVisibility(event: ScreenEvent.ControlVisibility) {
        _uiState.update { it.copy(showControlUI = event.isVisible) }
    }

    private fun handleDownloadVisibility(event: ScreenEvent.DownloadVisibility) {
        _uiState.update { it.copy(showDownloadSheet = event.isVisible) }
    }

    private fun handleFolderCreationVisibility(event: ScreenEvent.FolderCreationVisibility) {
        _uiState.update {
            it.copy(
                showFolderSheet = event.isVisible.not(), showAddFolder = event.isVisible
            )
        }
    }

    private fun handleFolderMediaVisibility(event: ScreenEvent.FolderMediaVisibility) {
        _uiState.update { it.copy(showDownloadSheet = event.isVisible) }
    }

    private fun handleFolderModificationVisibility(event: ScreenEvent.FolderModificationVisibility) {
        _uiState.update { it.copy(showFolderSheet = event.isVisible) }
    }

    private fun handleHintVisibility(event: ScreenEvent.HintVisibility) {
        _uiState.update { it.copy(isHintVisible = event.isVisible) }
    }

    override fun setOnCallBackListener(listener: ScreenController.CallbackListener) {
        callbackListener = listener
    }

    private fun handleSetBackgroundImage(event: ScreenEvent.SetBackgroundImage) {
        _uiState.update { it.copy(backgroundImage = event.image) }
    }

    private fun shouldShowOverlayMask(): Boolean {
        val state = _uiState.value
        return listOf(
            state.showVideoDetailUI,
            state.showReplyUI,
            state.showArchiveUI,
            state.showUpInfoSheet,
            state.showPlaylistSheet,
            state.isWatchLaterVisible,
            state.isFolderMediaVisible
        ).any { it }
    }
}
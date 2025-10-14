package com.laohei.bili_tube.features.player.component

import android.content.Context
import android.view.TextureView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.media3.exoplayer.ExoPlayer
import com.laohei.bili_tube.features.player.MediaPlayerUIState
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.features.player.component.control.PlayerControl
import com.laohei.bili_tube.features.player.state.media_v2.MediaUIState
import com.laohei.bili_tube.features.player.state.screen_v2.ScreenEvent
import com.laohei.bili_tube.features.player.state.screen_v2.ScreenState
import com.laohei.bili_tube.model.play.MediaPlayConfig
import com.laohei.bili_tube.ui.util.isOrientationPortrait
import com.laohei.bili_tube.util.toTimeString
import kotlinx.coroutines.delay


@Composable
internal fun LandscapeFullscreenVideoPage(
    context: Context,
    playParam: MediaPlayConfig,
    exoPlayer: ExoPlayer,
    screenState: ScreenState,
    mediaUIState: MediaUIState,
    playerState: MediaPlayerUIState,
    onBackPress: () -> Unit,
    onProgressUpdate: (Float) -> Unit,
    onPlayChange: (Boolean) -> Unit,
    onFullscreenChange: (Boolean) -> Unit,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
    handleScreenEvent: (ScreenEvent) -> Unit,
    resetHideTimer: () -> Unit,
    onSelectedAidChange: (Long) -> Unit,
    onDoubleSpeedChange: (Boolean) -> Unit
) {
    val aspectRatio by rememberUpdatedState(mediaUIState.videoAspect)
    val videoModifier = Modifier
        .fillMaxHeight()
        .aspectRatio(aspectRatio)
    val textureView = remember { TextureView(context) }

    LaunchedEffect(mediaUIState.isPlaying) {
        while (mediaUIState.isPlaying) {
            val bitmap = textureView.bitmap
            bitmap?.let {
                handleScreenEvent(ScreenEvent.SetBackgroundImage(it))
            }
            delay(8000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        BlurBackground(
            bitmap = screenState.backgroundImage,
            isDrag = screenState.isDragging,
            isFullscreen = screenState.isFullScreenActive,
        )

        PlayerControl(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(99f),
            title = playerState.title,
            progress = mediaUIState.progress,
            bufferProgress = mediaUIState.bufferProgress,
            isShowUI = screenState.showControlUI,
            isShowRelatedList = screenState.showRelatedList,
            isFullscreen = screenState.isFullScreenActive,
            isLockScreen = screenState.isScreenLocked,
            onFullscreenToggle = onFullscreenChange,
            isPlaying = mediaUIState.isPlaying,
            isLoading = mediaUIState.isLoading,
            totalDuration = mediaUIState.duration.toTimeString(),
            currentDuration = mediaUIState.currentPosition.toTimeString(),
            onPlaybackStateChanged = onPlayChange,
            onProgressUpdate = onProgressUpdate,
            onLongPressStart = { onDoubleSpeedChange(true) },
            onLongPressEnd = { onDoubleSpeedChange(false) },
            onControlUIVisibilityChange = { handleScreenEvent(ScreenEvent.ControlVisibility(it)) },
            onOpenSettings = { handleScreenEvent(ScreenEvent.SettingsVisibility(true)) },
            onBackPressed = onBackPress,
            hintContent = { SpeedHint(speed = mediaUIState.activeSpeed) },
            bottomControls = {
                FullscreenBottomControlContent(
                    images = when {
                        playerState.isVideo -> {
                            playerState.videoDetail?.related?.take(3)?.map { it.pic }
                        }

                        else -> {
                            playerState.relatedBangumis?.take(3)?.map { it.cover }
                        }
                    },
                    hasLike = playerState.hasLike,
                    hasFavoured = playerState.hasFavoured,
                    showLikeAnimation = screenState.showLikeAnimation,
                    isFullscreen = screenState.isFullScreenActive,
                    showLabel = screenState.isFullScreenActive && !isOrientationPortrait(),
                    handleScreenEvent = {
                        if (it is ScreenEvent.FolderModificationVisibility) {
                            onSelectedAidChange(playParam.aid)
                        }
                        handleScreenEvent(it)
                    },
                    onVideoMenuAction = onVideoMenuAction,
                )
            },
            unlockScreen = {},
            restartHideTimer = resetHideTimer
        ) {
            AndroidView(
                modifier = videoModifier,
                factory = { _ ->
                    textureView
                },
                update = { view ->
                    exoPlayer.setVideoTextureView(view)
                },
            )
        }
    }
}
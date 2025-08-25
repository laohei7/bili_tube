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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.media3.exoplayer.ExoPlayer
import coil3.Bitmap
import com.laohei.bili_tube.PlayParam
import com.laohei.bili_tube.features.player.MediaPlayerUIState
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.features.player.component.control.PlayerControl
import com.laohei.bili_tube.features.player.state.media.MediaState
import com.laohei.bili_tube.features.player.state.screen.ScreenAction
import com.laohei.bili_tube.features.player.state.screen.ScreenState
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.isOrientationPortrait
import kotlinx.coroutines.delay


@Composable
internal fun LandscapeFullscreenVideoPage(
    context: Context,
    playParam: PlayParam,
    exoPlayer: ExoPlayer,
    screenState: ScreenState,
    mediaState: MediaState,
    playerState: MediaPlayerUIState,
    onVideoFrameChange: (Bitmap) -> Unit,
    onControlUIChange: (Boolean) -> Unit,
    onBackPress: () -> Unit,
    onProgressChange: (Float) -> Unit,
    onPlayChange: (Boolean) -> Unit,
    onFullscreenChange: (Boolean) -> Unit,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
    onScreenAction: (ScreenAction) -> Unit,
    resetHideTimer: () -> Unit,
    onSelectedAidChange: (Long) -> Unit,
    onDoubleSpeedChange: (Boolean) -> Unit
) {
    val aspectRatio = (mediaState.width.toFloat() / mediaState.height)
    val videoModifier = Modifier
        .fillMaxHeight()
        .aspectRatio(aspectRatio)
    val textureView = remember { TextureView(context) }

    LaunchedEffect(mediaState.isPlaying) {
        while (mediaState.isPlaying) {
            val bitmap = textureView.bitmap
            bitmap?.let {
                onVideoFrameChange(it)
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
            bitmap = screenState.background,
            isDrag = screenState.isDrag,
            isFullscreen = screenState.isFullscreen,
        )

        PlayerControl(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(99f),
            title = playerState.title,
            progress = mediaState.progress,
            bufferProgress = mediaState.bufferProgress,
            isShowUI = screenState.isShowControlUI,
            isShowRelatedList = screenState.isShowRelatedList,
            isFullscreen = screenState.isFullscreen,
            isLockScreen = screenState.isLockScreen,
            onFullscreenChange = onFullscreenChange,
            isPlaying = mediaState.isPlaying,
            isLoading = mediaState.isLoading,
            totalDuration = mediaState.totalDuration.formatTimeString(),
            currentDuration = mediaState.currentDuration.formatTimeString(),
            onPlayChange = onPlayChange,
            onProgressChange = onProgressChange,
            onLongPressStart = { onDoubleSpeedChange(true) },
            onLongPressEnd = { onDoubleSpeedChange(false) },
            onControlUIChange = onControlUIChange,
            onSetting = { onScreenAction(ScreenAction.SetSettingVisible(true)) },
            onBackPress = onBackPress,
            hintContent = {
                SpeedHint(speed = mediaState.speed)
            },
            bottomControlContent = {
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
                    showLikeAnimation = screenState.isShowLikeAnimation,
                    isFullscreen = screenState.isFullscreen,
                    showLabel = screenState.isFullscreen && !isOrientationPortrait(),
                    onScreenAction = {
                        if (it is ScreenAction.SetModifyFolderVisible) {
                            onSelectedAidChange(playParam.aid)
                        }
                        onScreenAction(it)
                    },
                    onVideoMenuAction = onVideoMenuAction,
                )
            },
            unlockScreen = {},
            resetHideTimer = resetHideTimer
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
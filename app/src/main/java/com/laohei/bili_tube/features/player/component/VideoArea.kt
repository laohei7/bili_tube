package com.laohei.bili_tube.features.player.component

import android.graphics.Bitmap
import android.view.TextureView
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.laohei.bili_tube.component.video.VideoAction
import com.laohei.bili_tube.features.player.MediaPlayerUIState
import com.laohei.bili_tube.features.player.component.control.PlayerControl
import com.laohei.bili_tube.features.player.state.media.MediaState
import com.laohei.bili_tube.features.player.state.screen.ScreenAction
import com.laohei.bili_tube.features.player.state.screen.ScreenState
import com.laohei.bili_tube.utill.SystemUtil
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.isOrientationPortrait
import kotlinx.coroutines.delay


@OptIn(UnstableApi::class)
@Composable
internal fun VideoArea(
    modifier: Modifier = Modifier,
    videoControlModifier: Modifier = Modifier,
    exoPlayer: ExoPlayer,
    playerState: MediaPlayerUIState,
    mediaState: MediaState,
    screenState: ScreenState,
    videoFrame: (Bitmap) -> Unit = {},
    fullscreen: (Boolean) -> Unit = {},
    uploadVideoHistory: ((Long) -> Unit)? = null,
    seekTo: (Float) -> Unit,
    setSpeed: (Float) -> Unit,
    togglePlayPause: () -> Unit,
    onShowUIChanged: (Boolean) -> Unit,
    backPressedClick: (() -> Unit)? = null,
    screenActionClick: (ScreenAction) -> Unit,
    videoMenuClick: (VideoAction.VideoMenuAction) -> Unit,
) {
    val localContext = LocalContext.current
    val textureView = remember { TextureView(localContext) }

    LaunchedEffect(mediaState.isPlaying) {
        while (mediaState.isPlaying) {
            val bitmap = textureView.bitmap
            bitmap?.let {
                videoFrame(it)
            }
            delay(8000)
        }
    }

    LaunchedEffect(mediaState.isPlaying) {
        while (mediaState.isPlaying) {
            val history = exoPlayer.currentPosition / 1000
            uploadVideoHistory?.invoke(history)
            delay(15000)
        }
    }

    PlayerControl(
        modifier = videoControlModifier.zIndex(99f),
        title = playerState.title,
        progress = mediaState.progress,
        bufferProgress = mediaState.bufferProgress,
        isShowUI = screenState.isShowControlUI,
        isShowRelatedList = screenState.isShowRelatedList,
        isFullscreen = screenState.isFullscreen,
        isLockScreen = screenState.isLockScreen,
        onFullscreenChange = fullscreen,
        isPlaying = mediaState.isPlaying,
        isLoading = mediaState.isLoading,
        totalDuration = mediaState.totalDuration.formatTimeString(),
        currentDuration = mediaState.currentDuration.formatTimeString(),
        onPlayChange = { togglePlayPause.invoke() },
        onProgressChange = { seekTo.invoke(it) },
        onLongPressStart = { setSpeed.invoke(2f) },
        onLongPressEnd = { setSpeed.invoke(1f) },
        onControlUIChange = onShowUIChanged,
        onBackPress = backPressedClick,
        onSetting = {
            screenActionClick.invoke(ScreenAction.SetSettingVisible(true))
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
                onScreenAction = screenActionClick,
                onVideoMenuAction = {},
            )
        },
        unlockScreen = {
            screenActionClick.invoke(ScreenAction.SetLockScreen(false))
        },
        resetHideTimer = {}
    ) {
        val aspectRatio = when {
            isOrientationPortrait() -> mediaState.width.toFloat() / mediaState.height
            else -> (mediaState.width.toFloat() / mediaState.height).coerceIn(
                SystemUtil.MIN_ASPECT_RATIO,
                SystemUtil.MAX_ASPECT_RATIO
            )
        }
        AndroidView(
            modifier = modifier
                .aspectRatio(aspectRatio),

            factory = { _ ->
                textureView
            },
            update = { view ->
                exoPlayer.setVideoTextureView(view)
            },
        )

    }
}

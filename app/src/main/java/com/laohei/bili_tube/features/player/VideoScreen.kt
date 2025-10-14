@file:kotlin.OptIn(ExperimentalTime::class)

package com.laohei.bili_tube.features.player

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.Log
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.compose.collectAsLazyPagingItems
import com.laohei.bili_tube.core.extension.hideSystemUI
import com.laohei.bili_tube.core.extension.showSystemUI
import com.laohei.bili_tube.core.runtime.LifecycleEffect
import com.laohei.bili_tube.features.player.component.LandscapeFullscreenVideoPage
import com.laohei.bili_tube.features.player.component.LandscapeVideoPage
import com.laohei.bili_tube.features.player.component.PlayerSnackHost
import com.laohei.bili_tube.features.player.component.PortraitVideoPage
import com.laohei.bili_tube.features.player.component.setting.OtherSettingsSheet
import com.laohei.bili_tube.features.player.component.setting.PlaySpeedSheet
import com.laohei.bili_tube.features.player.component.setting.VideoQualitySheet
import com.laohei.bili_tube.features.player.component.setting.VideoSettingSheet
import com.laohei.bili_tube.features.player.state.screen_v2.ScreenControllerImpl
import com.laohei.bili_tube.features.player.state.screen_v2.ScreenEvent
import com.laohei.bili_tube.model.play.MediaPlayConfig
import com.laohei.bili_tube.ui.component.dialog.CreateFolderDialog
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.component.sheet.FolderSheet
import com.laohei.bili_tube.ui.foundation.DeviceConfiguration
import com.laohei.bili_tube.ui.util.OnOrientationChanged
import com.laohei.bili_tube.ui.util.SystemUtil
import com.laohei.bili_tube.ui.util.isOrientationPortrait
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val TAG = "VideoScreen"

@OptIn(UnstableApi::class)
@Composable
fun VideoScreen(
    playParam: MediaPlayConfig,
    upPress: () -> Unit
) {
    val systemBarHeight: Dp = SystemUtil.getSystemBarHeightDp()
    val context: Context = LocalContext.current
    val density: Density = LocalDensity.current
    val view: View = LocalView.current
    val scope: CoroutineScope = rememberCoroutineScope()
    val activity: Activity? = LocalActivity.current
    val configuration = LocalConfiguration.current
    val windowInfo = LocalWindowInfo.current
    val containerSize = windowInfo.containerSize
    val (containerWidth, containerHeight) = remember {
        (containerSize.width / density.density).roundToInt() to
                (containerSize.height / density.density).roundToInt()
    }

    // The Listen Control UI is automatically hidden
    var autoHideJob by remember { mutableStateOf<Job?>(null) }
    var lastTapTimestamp by remember { mutableLongStateOf(0) }
    val isOrientationPortrait = isOrientationPortrait()
    val (screenWidthDp, screenHeightDp) = remember {
        when {
            isOrientationPortrait -> containerWidth

            else -> containerHeight + systemBarHeight.value.roundToInt()
        } to when {
            isOrientationPortrait -> containerHeight + systemBarHeight.value.roundToInt()
            else -> containerWidth
        }
    }

    val screenControllerImpl = remember {
        ScreenControllerImpl(density, context, screenWidthDp, screenHeightDp)
    }
    val viewModel = koinViewModel<MediaViewModel> {
        parametersOf(playParam, screenControllerImpl)
    }

    val mediaController = viewModel.mediaController
    val mediaUIState by mediaController.uiState.collectAsStateWithLifecycle()

    val playerState by viewModel.mediaPlayerUIState.collectAsStateWithLifecycle()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    val replies = playerState.repliesFlow.collectAsLazyPagingItems()
    val userVideos = playerState.uploadedVideosFlow.collectAsLazyPagingItems()

    fun togglePlayPauseState() {
        if (mediaController.isPlaying) {
            mediaController.pause()
        } else {
            mediaController.play()
        }
    }

    fun resetHideTimer() {
        autoHideJob?.cancel()
        lastTapTimestamp = Clock.System.now().toEpochMilliseconds()
    }

    fun delayHideControl() {
        if (!screenState.showControlUI) return

        scope.launch {
            while (true) {
                delay(5000)
                val currentTimestamp = Clock.System.now().toEpochMilliseconds()
                val canHide = (currentTimestamp - lastTapTimestamp) > 5000
                if (canHide) {
                    viewModel.handleScreenEvent(ScreenEvent.ControlVisibility(false))
                }
            }
        }
    }

    // video size changed
    LaunchedEffect(mediaUIState.videoWidth, mediaUIState.videoHeight) {
        viewModel.calculateScreenSize(mediaUIState.videoWidth, mediaUIState.videoHeight)
    }

    LaunchedEffect(screenState.isFullScreenActive) {
        if (screenState.isFullScreenActive) {
            activity?.hideSystemUI()
        } else {
            activity?.showSystemUI()
        }
    }

    LaunchedEffect(screenState.showControlUI) {
        delayHideControl()
    }

    view.keepScreenOn = mediaUIState.isPlaying

    DisposableEffect(mediaUIState.isPlaying, view) {
        onDispose { view.keepScreenOn = false }
    }



    LifecycleEffect(
        onResume = {
            if (!mediaUIState.isLoading && !mediaUIState.isPlaying) {
                togglePlayPauseState()
            }
        },
        onPause = {
            if (mediaUIState.isPlaying) {
                togglePlayPauseState()
            }
        }
    )

    fun enterFullscreen() {
        val aspectRatio = mediaUIState.videoAspect
        val isAutoRotationEnabled = screenState.isAutoRotationEnabled
        if (isOrientationPortrait && aspectRatio <= 1f) {
            activity?.requestedOrientation = if (isAutoRotationEnabled) {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            viewModel.updateFullscreenState(true, screenState.screenHeight.dp, true)
        } else {
            activity?.requestedOrientation = if (isAutoRotationEnabled) {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            viewModel.updateFullscreenState(true, screenState.initialVideoHeight, false)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    fun exitFullscreen(uiType: DeviceConfiguration) {
        val isAutoRotationEnabled = screenState.isAutoRotationEnabled
        activity?.requestedOrientation = if (isAutoRotationEnabled) {
            ActivityInfo.SCREEN_ORIENTATION_USER
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        viewModel.updateFullscreenState(
            false, screenState.initialVideoHeight, isOrientationPortrait
        )
    }

    fun onBackHandler(uiType: DeviceConfiguration) {
        Log.d("TAG", "VideoScreen: ${screenState.isFullScreenActive}")
        when {
            screenState.isScreenLocked -> {}
            screenState.isFullScreenActive -> {
                viewModel.handleScreenEvent(ScreenEvent.SetUserFullscreenToggle(true))
                exitFullscreen(uiType)
            }

            else -> {
                upPress()
            }
        }
    }



    LaunchedEffect(mediaUIState.isPlaying) {
        while (mediaUIState.isPlaying) {
            val history = mediaController.currentPosition / 1000
            viewModel.reportPlaybackProgress(history)
            delay(15000)
        }
    }

    AdaptiveLayout { uiType, width, height ->
        // listener orientation
        BackHandler(enabled = screenState.isFullScreenActive) {
            onBackHandler(uiType)
        }
        OnOrientationChanged { orientation ->
            if (!screenState.isAutoRotationEnabled) return@OnOrientationChanged
            val isFullscreen = screenState.isFullScreenActive
            if (!screenState.isUserFullscreenToggle) {
                when (orientation) {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> {
                        if (isFullscreen) {
                            Log.d(TAG, "VideoScreen: exit fullscreen (portrait)")
                            exitFullscreen(uiType)
                        }
                    }

                    ActivityInfo.SCREEN_ORIENTATION_USER,
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> {
                        if (uiType == DeviceConfiguration.TABLE_LANDSCAPE ||
                            uiType == DeviceConfiguration.DESKTOP
                        ) {
                            return@OnOrientationChanged
                        }
                        if (!isFullscreen) {
                            Log.d(TAG, "VideoScreen: enter fullscreen (landscape)")
                            enterFullscreen()
                        }
                    }

                    else -> Unit
                }
            }
            viewModel.handleScreenEvent(ScreenEvent.SetUserFullscreenToggle(false))
        }
        when (uiType) {
            DeviceConfiguration.MOBILE_PORTRAIT,
            DeviceConfiguration.TABLE_PORTRAIT -> {
                PortraitVideoPage(
                    context = context,
                    nestedScrollConnection = viewModel.nestedScrollConnection,
                    exoPlayer = mediaController.player as ExoPlayer,
                    mediaUIState = mediaUIState,
                    playParam = playerState.mediaPlayConfig,
                    screenState = screenState,
                    playerState = playerState,
                    replies = replies,
                    userVideos = userVideos,
                    onPostHistory = {},
                    onBackPress = { onBackHandler(uiType) },
                    onProgressUpdate = { mediaController.seekTo((mediaUIState.duration * it).toLong()) },
                    onPlayChange = { togglePlayPauseState() },
                    onFullscreenChange = { isFullscreen ->
                        viewModel.handleScreenEvent(ScreenEvent.SetUserFullscreenToggle(true))
                        if (isFullscreen) {
                            enterFullscreen()
                        } else {
                            exitFullscreen(uiType)
                        }
                    },
                    onVideoMenuAction = viewModel::onVideoMenuAction,
                    handleScreenEvent = viewModel::handleScreenEvent,
                    restartHideTimer = ::resetHideTimer,
                    onMaskAlphaChange = viewModel::updateMaskAlpha,
                    onSelectedAidChange = viewModel::setSelectedAid,
                    onSelectedBvidChange = viewModel::setSelectedBvid,
                    onDoubleSpeedChange = { enabled ->
                        viewModel.handleScreenEvent(ScreenEvent.HintVisibility(enabled))
                        val targetSpeed = if (enabled) 2.0f else mediaUIState.sessionSpeed
                        mediaController.setSpeed(targetSpeed)
                    },
                )
            }

            DeviceConfiguration.MOBILE_LANDSCAPE -> {
                LandscapeFullscreenVideoPage(
                    context = context,
                    playParam = playerState.mediaPlayConfig,
                    exoPlayer = mediaController.player as ExoPlayer,
                    screenState = screenState,
                    mediaUIState = mediaUIState,
                    playerState = playerState,
                    onBackPress = { onBackHandler(uiType) },
                    onProgressUpdate = { mediaController.seekTo((mediaUIState.duration * it).toLong()) },
                    onPlayChange = { togglePlayPauseState() },
                    handleScreenEvent = viewModel::handleScreenEvent,
                    onFullscreenChange = { isFullscreen ->
                        viewModel.handleScreenEvent(ScreenEvent.SetUserFullscreenToggle(true))
                        if (isFullscreen) {
                            enterFullscreen()
                        } else {
                            exitFullscreen(uiType)
                        }
                    },
                    onVideoMenuAction = viewModel::onVideoMenuAction,
                    resetHideTimer = ::resetHideTimer,
                    onSelectedAidChange = viewModel::setSelectedAid,
                    onDoubleSpeedChange = { enabled ->
                        viewModel.handleScreenEvent(ScreenEvent.HintVisibility(enabled))
                        val targetSpeed = if (enabled) 2.0f else mediaUIState.sessionSpeed
                        mediaController.setSpeed(targetSpeed)
                    },
                )
            }

            DeviceConfiguration.TABLE_LANDSCAPE,
            DeviceConfiguration.DESKTOP -> {
                if (screenState.isFullScreenActive) {
                    LandscapeFullscreenVideoPage(
                        context = context,
                        playParam = playerState.mediaPlayConfig,
                        exoPlayer = mediaController.player as ExoPlayer,
                        screenState = screenState,
                        mediaUIState = mediaUIState,
                        playerState = playerState,
                        onBackPress = { onBackHandler(uiType) },
                        onProgressUpdate = { mediaController.seekTo((mediaUIState.duration * it).toLong()) },
                        onPlayChange = { togglePlayPauseState() },
                        onFullscreenChange = { isFullscreen ->
                            viewModel.handleScreenEvent(ScreenEvent.SetUserFullscreenToggle(true))
                            if (isFullscreen) {
                                enterFullscreen()
                            } else {
                                exitFullscreen(uiType)
                            }
                        },
                        handleScreenEvent = viewModel::handleScreenEvent,
                        onVideoMenuAction = viewModel::onVideoMenuAction,
                        resetHideTimer = ::resetHideTimer,
                        onSelectedAidChange = viewModel::setSelectedAid,
                        onDoubleSpeedChange = { enabled ->
                            viewModel.handleScreenEvent(ScreenEvent.HintVisibility(enabled))
                            val targetSpeed = if (enabled) 2.0f else mediaUIState.sessionSpeed
                            mediaController.setSpeed(targetSpeed)
                        },
                    )
                } else {
                    LandscapeVideoPage(
                        exoPlayer = mediaController.player as ExoPlayer,
                        playerUIState = playerState,
                        mediaUIState = mediaUIState,
                        screenState = screenState,
                        replies = replies,
                        works = userVideos,
                        handleScreenEvent = viewModel::handleScreenEvent,
                        onBackPress = { onBackHandler(uiType) },
                        onProgressUpdate = { mediaController.seekTo((mediaUIState.duration * it).toLong()) },
                        onPlayChange = { togglePlayPauseState() },
                        onFullscreenChange = { isFullscreen ->
                            viewModel.handleScreenEvent(ScreenEvent.SetUserFullscreenToggle(true))
                            if (isFullscreen) {
                                enterFullscreen()
                            } else {
                                exitFullscreen(uiType)
                            }
                        },
                        resetHideTimer = ::resetHideTimer,
                        onDoubleSpeedChange = { enabled ->
                            viewModel.handleScreenEvent(ScreenEvent.HintVisibility(enabled))
                            val targetSpeed = if (enabled) 2.0f else mediaUIState.sessionSpeed
                            mediaController.setSpeed(targetSpeed)
                        },
                        onVideoMenuAction = viewModel::onVideoMenuAction
                    )
                }
            }
        }

        // video setting sheets
        VideoSettingSheet(
            isShowSheet = screenState.showVideoSettingsSheet,
            speed = mediaUIState.activeSpeed,
            quality = mediaUIState.activeQuality.label,
            onDismiss = {
                viewModel.handleScreenEvent(ScreenEvent.SettingsVisibility(false))
            },
            handleScreenEvent = viewModel::handleScreenEvent,
        )
        PlaySpeedSheet(
            isSheetVisible = screenState.showSpeedUI,
            speed = mediaUIState.activeSpeed,
            onSpeedUpdated = {
                mediaController.setSessionSpeed(it)
            },
            onDismiss = {
                viewModel.handleScreenEvent(ScreenEvent.SpeedSettingsVisibility(false))
            }
        )
        VideoQualitySheet(
            isShowSheet = screenState.showQualityUI,
            qualities = mediaUIState.supportQualities,
            activeQuality = mediaUIState.activeQuality,
            onDismiss = {
                viewModel.handleScreenEvent(ScreenEvent.QualitySettingsVisibility(false))
            },
            onVideoQualityChanged = {
                viewModel.handleScreenEvent(ScreenEvent.QualitySettingsVisibility(false))
                mediaController.setQuality(it)
            }
        )
        OtherSettingsSheet(
            isShowSheet = screenState.showOtherSettingUI,
            autoSkip = playerState.autoSkip,
            onDismiss = {
                viewModel.handleScreenEvent(ScreenEvent.OtherSettingsVisibility(false))
            },
            videoSettingActionClick = viewModel::onVideoSettingAction
        )

        // folder
        FolderSheet(
            folders = playerState.folders,
            isShowSheet = screenState.showFolderSheet,
            onDismiss = {
                viewModel.handleScreenEvent(ScreenEvent.FolderModificationVisibility(false))
            },
            onCreateFolder = {
                viewModel.handleScreenEvent(ScreenEvent.FolderCreationVisibility(true))
            },
            onAddToFolder = { addAids, delAids ->
                viewModel.onVideoMenuAction(
                    VideoMenuAction.AddToFolders(
                        addAids = addAids,
                        delAids = delAids,
                    )
                )
                viewModel.handleScreenEvent(ScreenEvent.FolderModificationVisibility(false))
            }
        )

        CreateFolderDialog(
            isVisible = screenState.showAddFolder,
            value = playerState.folderName,
            onValueChange = viewModel::onFolderNameChange,
            onSubmit = viewModel::createFolder,
            checked = playerState.isPrivate,
            onCheckedChange = viewModel::onPrivateChanged,
            onDismiss = {
                viewModel.onFolderNameChange("")
                viewModel.handleScreenEvent(ScreenEvent.FolderCreationVisibility(false))
            }
        )

        PlayerSnackHost(
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }

}
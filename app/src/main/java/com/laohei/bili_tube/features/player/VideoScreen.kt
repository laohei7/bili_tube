package com.laohei.bili_tube.features.player

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.SimpleCache
import androidx.paging.compose.collectAsLazyPagingItems
import com.laohei.bili_tube.PlayParam
import com.laohei.bili_tube.core.runtime.LifecycleEffect
import com.laohei.bili_tube.features.player.component.LandscapeFullscreenVideoPage
import com.laohei.bili_tube.features.player.component.LandscapeVideoPage
import com.laohei.bili_tube.features.player.component.PlayerSnackHost
import com.laohei.bili_tube.features.player.component.PortraitVideoPage
import com.laohei.bili_tube.features.player.component.setting.OtherSettingsSheet
import com.laohei.bili_tube.features.player.component.setting.PlaySpeedSheet
import com.laohei.bili_tube.features.player.component.setting.VideoQualitySheet
import com.laohei.bili_tube.features.player.component.setting.VideoSettingSheet
import com.laohei.bili_tube.features.player.state.media.DefaultMediaController
import com.laohei.bili_tube.features.player.state.screen.DefaultScreenController
import com.laohei.bili_tube.features.player.state.screen.ScreenAction
import com.laohei.bili_tube.ui.component.dialog.CreatedFolderDialog
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.component.layout.DeviceConfiguration
import com.laohei.bili_tube.ui.component.sheet.FolderSheet
import com.laohei.bili_tube.utill.OnOrientationChanged
import com.laohei.bili_tube.utill.SystemUtil
import com.laohei.bili_tube.utill.hideSystemUI
import com.laohei.bili_tube.utill.isOrientationPortrait
import com.laohei.bili_tube.utill.showSystemUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.chromium.net.CronetEngine
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

private const val TAG = "VideoScreen"

@OptIn(UnstableApi::class)
@Composable
fun VideoScreen(
    cronetEngine: CronetEngine = koinInject(),
    simpleCache: SimpleCache = koinInject(),
    playParam: PlayParam,
    upPress: () -> Unit
) {
    val systemBarHeight: Dp = SystemUtil.getSystemBarHeightDp()
    val context: Context = LocalContext.current
    val density: Density = LocalDensity.current
    val view: View = LocalView.current
    val scope: CoroutineScope = rememberCoroutineScope()
    val activity: Activity? = LocalActivity.current
    val configuration: Configuration = LocalConfiguration.current

    // The Listen Control UI is automatically hidden
    var autoHideJob by remember { mutableStateOf<Job?>(null) }
    var lastTapTimestamp by remember { mutableLongStateOf(0) }

    val isOrientationPortrait = isOrientationPortrait()
    val defaultMediaManager = remember {
        DefaultMediaController(
            context, cronetEngine, simpleCache,
            playParam.width, playParam.height
        )
    }
    val defaultScreenManager = remember {
        DefaultScreenController(
            density, context,
            when {
                isOrientationPortrait -> configuration.screenHeightDp + systemBarHeight.value.roundToInt()
                else -> configuration.screenWidthDp + systemBarHeight.value.roundToInt()
            },
            when {
                isOrientationPortrait -> configuration.screenWidthDp
                else -> configuration.screenHeightDp
            },
            playParam.width, playParam.height
        )
    }
    val viewModel = koinViewModel<MediaViewModel> {
        parametersOf(playParam, defaultMediaManager, defaultScreenManager)
    }

    val playerState by viewModel.mediaPlayerUIState.collectAsStateWithLifecycle()
    val mediaState by viewModel.mediaState.collectAsStateWithLifecycle()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    val replies = playerState.repliesFlow.collectAsLazyPagingItems()
    val userVideos = playerState.uploadedVideosFlow.collectAsLazyPagingItems()


    fun resetHideTimer() {
        autoHideJob?.cancel()
        lastTapTimestamp = Clock.System.now().toEpochMilliseconds()
    }

    fun delayHideControl() {
        if (!screenState.isShowControlUI) return

        scope.launch {
            while (true) {
                delay(5000)
                val currentTimestamp = Clock.System.now().toEpochMilliseconds()
                val canHide = (currentTimestamp - lastTapTimestamp) > 5000
                if (canHide) {
                    viewModel.onScreenAction(ScreenAction.SetControlVisible(false), false)
                }
            }
        }
    }

    // video size changed
    LaunchedEffect(mediaState.width, mediaState.height) {
        viewModel.computeScreenSize(mediaState.width, mediaState.height)
    }

    LaunchedEffect(screenState.isFullscreen) {
        when {
            screenState.isFullscreen -> {
                activity?.hideSystemUI()
            }

            else -> {
                activity?.showSystemUI()
            }
        }
    }

    LaunchedEffect(screenState.isShowControlUI) {
        delayHideControl()
    }

    view.keepScreenOn = mediaState.isPlaying

    DisposableEffect(mediaState.isPlaying, view) {
        onDispose { view.keepScreenOn = false }
    }



    LifecycleEffect(
        onResume = {
            if (!mediaState.isLoading && !mediaState.isPlaying) {
                viewModel.togglePlayPause()
            }
        },
        onPause = {
            if (mediaState.isPlaying) {
                viewModel.togglePlayPause()
            }
        }
    )

    fun enterFullscreen() {
        val aspectRatio = mediaState.width.toFloat() / mediaState.height
        val isAutoRotateEnabled = screenState.isAutoRotateEnabled
        if (isOrientationPortrait && aspectRatio <= 1f) {
            activity?.requestedOrientation = if (isAutoRotateEnabled) {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            viewModel.onFullscreenChange(true, screenState.screenHeight.dp, true)
        } else {
            activity?.requestedOrientation = if (isAutoRotateEnabled) {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            viewModel.onFullscreenChange(true, screenState.originalVideoHeight, false)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    fun exitFullscreen(uiType: DeviceConfiguration) {
        val isAutoRotateEnabled = screenState.isAutoRotateEnabled
        activity?.requestedOrientation = if (isAutoRotateEnabled) {
            ActivityInfo.SCREEN_ORIENTATION_USER
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        viewModel.onFullscreenChange(false, screenState.originalVideoHeight, isOrientationPortrait)
    }

    fun onBackHandler(uiType: DeviceConfiguration) {
        Log.d("TAG", "VideoScreen: ${screenState.isFullscreen}")
        when {
            screenState.isLockScreen -> {}
            screenState.isFullscreen -> {
                viewModel.onScreenAction(ScreenAction.SetUserSwitch(true), isOrientationPortrait)
                exitFullscreen(uiType)
            }

            else -> {
                upPress()
            }
        }
    }



    LaunchedEffect(mediaState.isPlaying) {
        while (mediaState.isPlaying) {
            val history = viewModel.exoPlayer.currentPosition / 1000
            viewModel.reportPlaybackProgress(history)
            delay(15000)
        }
    }

    AdaptiveLayout { uiType, width, height ->
        // listener orientation
        BackHandler(enabled = screenState.isFullscreen) {
            onBackHandler(uiType)
        }
        OnOrientationChanged { orientation ->
            Log.d(TAG, "VideoScreen: orientation change: $orientation")
            if (!screenState.isAutoRotateEnabled) return@OnOrientationChanged
            val isFullscreen = screenState.isFullscreen
            if (!screenState.isUserSwitch) {
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

            viewModel.onScreenAction(ScreenAction.SetUserSwitch(false), isOrientationPortrait)
        }
        when (uiType) {
            DeviceConfiguration.MOBILE_PORTRAIT,
            DeviceConfiguration.TABLE_PORTRAIT -> {
                PortraitVideoPage(
                    context = context,
                    nestedScrollConnection = viewModel.nestedScrollConnection,
                    exoPlayer = viewModel.exoPlayer,
                    playParam = playerState.playParam,
                    screenState = screenState,
                    mediaState = mediaState,
                    playerState = playerState,
                    replies = replies,
                    userVideos = userVideos,
                    onVideoFrameChange = {
                        viewModel.onScreenAction(
                            ScreenAction.SetBackground(it), isOrientationPortrait
                        )
                    },
                    onPostHistory = {},
                    onControlUIChange = {
                        viewModel.onScreenAction(
                            ScreenAction.SetControlVisible(it), isOrientationPortrait
                        )
                    },
                    onBackPress = { onBackHandler(uiType) },
                    onProgressChange = { viewModel.seekToFraction(it) },
                    onPlayChange = { viewModel.togglePlayPause() },
                    onFullscreenChange = { isFullscreen ->
                        viewModel.onScreenAction(
                            ScreenAction.SetUserSwitch(true),
                            isOrientationPortrait
                        )
                        if (isFullscreen) {
                            enterFullscreen()
                        } else {
                            exitFullscreen(uiType)
                        }
                    },
                    onVideoMenuAction = viewModel::onVideoMenuAction,
                    onScreenAction = {
                        viewModel.onScreenAction(
                            action = it, isOrientationPortrait = false,
                            onLockScreenCallback = when (it) {
                                is ScreenAction.SetLockScreen -> ::enterFullscreen

                                else -> null
                            }
                        )
                    },
                    resetHideTimer = ::resetHideTimer,
                    onMaskAlphaChange = viewModel::onMaskAlphaChange,
                    onSelectedAidChange = viewModel::setSelectedAid,
                    onSelectedBvidChange = viewModel::setSelectedBvid,
                    onDownload = {
                        viewModel.download(it)
                        viewModel.onScreenAction(
                            ScreenAction.SetDownloadVisible(false),
                            isOrientationPortrait
                        )
                    },
                    onDoubleSpeedChange = { enabled ->
                        viewModel.onScreenAction(
                            ScreenAction.SetHintVisible(enabled),
                            isOrientationPortrait
                        )
                        val targetSpeed = if (enabled) 2.0f else mediaState.userSelectedSpeed
                        viewModel.setPlaybackSpeed(targetSpeed, false)
                    }
                )
            }

            DeviceConfiguration.MOBILE_LANDSCAPE -> {
                LandscapeFullscreenVideoPage(
                    context = context,
                    playParam = playerState.playParam,
                    exoPlayer = viewModel.exoPlayer,
                    screenState = screenState,
                    mediaState = mediaState,
                    playerState = playerState,
                    onVideoFrameChange = {
                        viewModel.onScreenAction(ScreenAction.SetBackground(it), false)
                    },
                    onControlUIChange = {
                        viewModel.onScreenAction(ScreenAction.SetControlVisible(it), false)
                    },
                    onBackPress = { onBackHandler(uiType) },
                    onProgressChange = { viewModel.seekToFraction(it) },
                    onPlayChange = { viewModel.togglePlayPause() },
                    onFullscreenChange = { isFullscreen ->
                        viewModel.onScreenAction(
                            ScreenAction.SetUserSwitch(true),
                            isOrientationPortrait
                        )
                        if (isFullscreen) {
                            enterFullscreen()
                        } else {
                            exitFullscreen(uiType)
                        }
                    },
                    onVideoMenuAction = viewModel::onVideoMenuAction,
                    onScreenAction = {
                        viewModel.onScreenAction(it, false)
                    },
                    resetHideTimer = ::resetHideTimer,
                    onSelectedAidChange = viewModel::setSelectedAid,
                    onDoubleSpeedChange = { enabled ->
                        viewModel.onScreenAction(
                            ScreenAction.SetHintVisible(enabled),
                            isOrientationPortrait
                        )
                        val targetSpeed = if (enabled) 2.0f else mediaState.userSelectedSpeed
                        viewModel.setPlaybackSpeed(targetSpeed, false)
                    }
                )
            }

            DeviceConfiguration.TABLE_LANDSCAPE,
            DeviceConfiguration.DESKTOP -> {
                if (screenState.isFullscreen) {
                    LandscapeFullscreenVideoPage(
                        context = context,
                        playParam = playerState.playParam,
                        exoPlayer = viewModel.exoPlayer,
                        screenState = screenState,
                        mediaState = mediaState,
                        playerState = playerState,
                        onVideoFrameChange = {
                            viewModel.onScreenAction(ScreenAction.SetBackground(it), false)
                        },
                        onControlUIChange = {
                            viewModel.onScreenAction(ScreenAction.SetControlVisible(it), false)
                        },
                        onBackPress = { onBackHandler(uiType) },
                        onProgressChange = { viewModel.seekToFraction(it) },
                        onPlayChange = { viewModel.togglePlayPause() },
                        onFullscreenChange = { isFullscreen ->
                            viewModel.onScreenAction(
                                ScreenAction.SetUserSwitch(true),
                                isOrientationPortrait
                            )
                            if (isFullscreen) {
                                enterFullscreen()
                            } else {
                                exitFullscreen(uiType)
                            }
                        },
                        onVideoMenuAction = viewModel::onVideoMenuAction,
                        onScreenAction = {
                            viewModel.onScreenAction(it, false)
                        },
                        resetHideTimer = ::resetHideTimer,
                        onSelectedAidChange = viewModel::setSelectedAid,
                        onDoubleSpeedChange = { enabled ->
                            viewModel.onScreenAction(
                                ScreenAction.SetHintVisible(enabled),
                                isOrientationPortrait
                            )
                            val targetSpeed = if (enabled) 2.0f else mediaState.userSelectedSpeed
                            viewModel.setPlaybackSpeed(targetSpeed, false)
                        }
                    )
                } else {
                    LandscapeVideoPage(
                        exoPlayer = viewModel.exoPlayer,
                        playerUIState = playerState,
                        mediaState = mediaState,
                        screenState = screenState,
                        replies = replies,
                        works = userVideos,
                        onVideoFrameChange = {
                            viewModel.onScreenAction(ScreenAction.SetBackground(it), false)
                        },
                        onControlUIChange = {
                            viewModel.onScreenAction(ScreenAction.SetControlVisible(it), false)
                        },
                        onBackPress = { onBackHandler(uiType) },
                        onProgressChange = { viewModel.seekToFraction(it) },
                        onPlayChange = { viewModel.togglePlayPause() },
                        onFullscreenChange = { isFullscreen ->
                            viewModel.onScreenAction(
                                ScreenAction.SetUserSwitch(true),
                                isOrientationPortrait
                            )
                            if (isFullscreen) {
                                enterFullscreen()
                            } else {
                                exitFullscreen(uiType)
                            }
                        },
                        onScreenAction = { viewModel.onScreenAction(it, false) },
                        resetHideTimer = ::resetHideTimer,
                        onDoubleSpeedChange = { enabled ->
                            viewModel.onScreenAction(
                                ScreenAction.SetHintVisible(enabled),
                                isOrientationPortrait
                            )
                            val targetSpeed = if (enabled) 2.0f else mediaState.userSelectedSpeed
                            viewModel.setPlaybackSpeed(targetSpeed, false)
                        },
                        onVideoMenuAction = viewModel::onVideoMenuAction
                    )
                }
            }
        }

        // video setting sheets
        VideoSettingSheet(
            isShowSheet = screenState.isShowVideoSettingsSheet,
            speed = mediaState.speed,
            quality = mediaState.videoQuality.second,
            onDismiss = {
                viewModel.onScreenAction(
                    ScreenAction.SetSettingVisible(false),
                    isOrientationPortrait
                )
            },
            onScreenAction = { action ->
                viewModel.onScreenAction(
                    ScreenAction.SetSettingVisible(false),
                    isOrientationPortrait
                )
                viewModel.onScreenAction(action, isOrientationPortrait)
            }
        )
        PlaySpeedSheet(
            isShowSheet = screenState.isShowSpeedUI,
            speed = mediaState.speed,
            onSpeedChange = {
                viewModel.setPlaybackSpeed(it)
            },
            onDismiss = {
                viewModel.onScreenAction(
                    ScreenAction.SetSettingSpeedVisible(false),
                    isOrientationPortrait
                )
            }
        )
        VideoQualitySheet(
            isShowSheet = screenState.isShowQualityUI,
            quality = mediaState.quality,
            defaultQuality = mediaState.videoQuality,
            onDismiss = {
                viewModel.onScreenAction(
                    ScreenAction.SetSettingQualityVisible(false),
                    isOrientationPortrait
                )
            },
            onQualityChanged = {
                viewModel.onScreenAction(
                    ScreenAction.SetSettingQualityVisible(false),
                    isOrientationPortrait
                )
                viewModel.changeQuality(it)
            }
        )
        OtherSettingsSheet(
            isShowSheet = screenState.isShowOtherSettingUI,
            autoSkip = playerState.autoSkip,
            onDismiss = {
                viewModel.onScreenAction(
                    ScreenAction.SetOtherSettingVisible(false),
                    isOrientationPortrait
                )
            },
            videoSettingActionClick = viewModel::onVideoSettingAction
        )

        // folder
        FolderSheet(
            folders = playerState.folders,
            isShowSheet = screenState.isShowFolderSheet,
            onDismiss = {
                viewModel.onScreenAction(
                    ScreenAction.SetModifyFolderVisible(false),
                    isOrientationPortrait
                )
            },
            onCreateFolder = {
                viewModel.onScreenAction(
                    ScreenAction.SetCreatedFolderVisible(true),
                    isOrientationPortrait
                )
            },
            onAddToFolder = { addAids, delAids ->
                viewModel.onVideoMenuAction(
                    VideoMenuAction.AddToFolders(
                        addAids = addAids,
                        delAids = delAids,
                    )
                )
                viewModel.onScreenAction(
                    ScreenAction.SetModifyFolderVisible(false),
                    isOrientationPortrait
                )
            }
        )

        CreatedFolderDialog(
            isShowDialog = screenState.isShowAddFolder,
            value = playerState.folderName,
            onValueChange = viewModel::onFolderNameChange,
            onSubmit = viewModel::createFolder,
            checked = playerState.isPrivate,
            onCheckedChange = viewModel::onPrivateChanged,
            onDismiss = {
                viewModel.onFolderNameChange("")
                viewModel.onScreenAction(
                    ScreenAction.SetCreatedFolderVisible(false),
                    isOrientationPortrait
                )
            }
        )

        PlayerSnackHost(
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }

}
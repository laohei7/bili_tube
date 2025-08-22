package com.laohei.bili_tube.features.player

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.util.Log
import android.view.TextureView
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.annotation.OptIn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.Bitmap
import coil3.compose.AsyncImage
import com.laohei.bili_sdk.module_v2.reply.ReplyItem
import com.laohei.bili_sdk.module_v2.user.UploadedVideoItem
import com.laohei.bili_tube.PlayParam
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.WRITE_STORAGE_PERMISSION
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.core.runtime.LifecycleEffect
import com.laohei.bili_tube.features.main.component.VideoMenuSheet
import com.laohei.bili_tube.features.player.component.AddCoinSheet
import com.laohei.bili_tube.features.player.component.BlurBackground
import com.laohei.bili_tube.features.player.component.FullscreenBottomControlContent
import com.laohei.bili_tube.features.player.component.GetContent
import com.laohei.bili_tube.features.player.component.PlayerSnackHost
import com.laohei.bili_tube.features.player.component.UserInfoCardSheet
import com.laohei.bili_tube.features.player.component.VideoDetailSheet
import com.laohei.bili_tube.features.player.component.archive.ArchiveSheet
import com.laohei.bili_tube.features.player.component.control.PlayerControl
import com.laohei.bili_tube.features.player.component.reply.VideoReplySheet
import com.laohei.bili_tube.features.player.component.setting.DownloadSheet
import com.laohei.bili_tube.features.player.component.setting.OtherSettingsSheet
import com.laohei.bili_tube.features.player.component.setting.PlaySpeedSheet
import com.laohei.bili_tube.features.player.component.setting.VideoQualitySheet
import com.laohei.bili_tube.features.player.component.setting.VideoSettingSheet
import com.laohei.bili_tube.features.player.state.media.DefaultMediaController
import com.laohei.bili_tube.features.player.state.media.MediaState
import com.laohei.bili_tube.features.player.state.screen.DefaultScreenController
import com.laohei.bili_tube.features.player.state.screen.ScreenAction
import com.laohei.bili_tube.features.player.state.screen.ScreenState
import com.laohei.bili_tube.ui.component.dialog.CreatedFolderDialog
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.component.layout.DeviceConfiguration
import com.laohei.bili_tube.ui.component.sheet.FolderSheet
import com.laohei.bili_tube.utill.OnOrientationChanged
import com.laohei.bili_tube.utill.SystemUtil
import com.laohei.bili_tube.utill.checkedPermissions
import com.laohei.bili_tube.utill.formatTimeString
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
            if (screenState.isAutoRotateEnabled.not()) {
                return@OnOrientationChanged
            }
            val isFullscreen = screenState.isFullscreen
            if (screenState.isUserSwitch.not()) {
                when (orientation) {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> {
                        if (isFullscreen) {
                            Log.d(TAG, "VideoScreen: orientation change: $orientation portrait")
                            exitFullscreen(uiType)
                        }
                    }

                    ActivityInfo.SCREEN_ORIENTATION_USER,
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> {
                        if (isFullscreen.not()) {
                            Log.d(TAG, "VideoScreen: orientation change: $orientation landscape")
                            enterFullscreen()
                        }
                    }

                    else -> {}
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
                    }
                )
            }

            DeviceConfiguration.MOBILE_LANDSCAPE -> {
                LandscapeFullscreenVideoPage(
                    context = context,
                    playParam =playerState.playParam,
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
                    onSelectedAidChange = viewModel::setSelectedAid
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
                        onSelectedAidChange = viewModel::setSelectedAid
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
            onSpeedChanged = { viewModel.setPlaybackSpeed(it) },
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

@Composable
private fun PortraitVideoPage(
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context,
    exoPlayer: ExoPlayer,
    nestedScrollConnection: NestedScrollConnection,
    playParam: PlayParam,
    screenState: ScreenState,
    mediaState: MediaState,
    playerState: MediaPlayerUIState,
    replies: LazyPagingItems<ReplyItem>,
    userVideos: LazyPagingItems<UploadedVideoItem>,
    onVideoFrameChange: (Bitmap) -> Unit,
    onPostHistory: ((Long) -> Unit)? = null,
    onControlUIChange: (Boolean) -> Unit,
    onBackPress: () -> Unit,
    onProgressChange: (Float) -> Unit,
    onPlayChange: (Boolean) -> Unit,
    onFullscreenChange: (Boolean) -> Unit,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
    onScreenAction: (ScreenAction) -> Unit,
    resetHideTimer: () -> Unit,
    onMaskAlphaChange: (Float) -> Unit,
    onDownload: (Pair<Int, String>) -> Unit,
    onSelectedAidChange: (Long) -> Unit,
    onSelectedBvidChange: (String) -> Unit,
) {
    val animatedVideoHeight by animateDpAsState(
        targetValue = screenState.videoHeight
    )
    val animatedContentOffset by animateDpAsState(
        targetValue = screenState.videoHeight + when {
            screenState.isFullscreen -> 0.dp
            else -> SystemUtil.getStatusBarHeightDp()
        }
    )
    val contentModifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
    val otherSheetModifier = contentModifier
        .offset { with(density) { IntOffset(0, animatedContentOffset.toPx().toInt()) } }
    val videoContentModifier = contentModifier
        .offset {
            with(density) {
                IntOffset(0, animatedContentOffset.toPx().roundToInt())
            }
        }
        .nestedScroll(connection = nestedScrollConnection)
        .draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { },
        )

    val videoModifier = Modifier
        .height(animatedVideoHeight)
        .width(animatedVideoHeight * mediaState.width.toFloat() / mediaState.height)

    val videoControlModifier = Modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Min)

    val sheetBottomPadding = screenState.videoHeight + 80.dp

    val textureView = remember { TextureView(context) }
    val aspectRatio = mediaState.width.toFloat() / mediaState.height

    LaunchedEffect(mediaState.isPlaying) {
        while (mediaState.isPlaying) {
            val bitmap = textureView.bitmap
            bitmap?.let {
                onVideoFrameChange(it)
            }
            delay(8000)
        }
    }

    LaunchedEffect(mediaState.isPlaying) {
        while (mediaState.isPlaying) {
            val history = exoPlayer.currentPosition / 1000
            onPostHistory?.invoke(history)
            delay(15000)
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
            modifier = videoControlModifier.zIndex(99f),
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
            onLongPressStart = { },
            onLongPressEnd = {},
            onControlUIChange = onControlUIChange,
            onSetting = { onScreenAction(ScreenAction.SetSettingVisible(true)) },
            onBackPress = onBackPress,
            hintContent = {},
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
            val cover = playerState.videoDetail?.view?.pic
                ?: playerState.bangumiDetail?.episodes?.find { it.epId == playerState.currentEpId }

            if (mediaState.showCover && cover != null) {
                AsyncImage(
                    modifier = videoModifier
                        .aspectRatio(aspectRatio),
                    model = cover,
                    contentDescription = "cover"
                )
            } else {
                AndroidView(
                    modifier = videoModifier
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

        GetContent(
            modifier = videoContentModifier,
            playerState = playerState,
            screenState = screenState,
            bottomPadding = screenState.videoHeight + 80.dp,
            onScreenAction = {
                if (it is ScreenAction.SetModifyFolderVisible) {
                    onSelectedAidChange(playParam.aid)
                }
                if (it is ScreenAction.SetModifyFolderVisible) {
                    onVideoMenuAction(VideoMenuAction.LoadSimpleFolders)
                }
                onScreenAction(it)
            },
            onVideoMenuAction = onVideoMenuAction,
            onSelectedAidChange = onSelectedAidChange,
            onSelectedBvidChange = onSelectedBvidChange
        )

        // Sheet Shadow Gradient Layer
        Box(
            modifier = contentModifier
                .graphicsLayer { alpha = screenState.maskAlpha }
                .background(Color.Black)
        )

        VideoReplySheet(
            isShowReplyUI = screenState.isShowReplyUI,
            shouldHideSystemBar = !isOrientationPortrait() && screenState.isFullscreen,
            replies = replies,
            onDismiss = { onScreenAction(ScreenAction.SetReplyVisible(false)) },
            modifier = otherSheetModifier,
            onMaskAlphaChange = { onMaskAlphaChange(it) },
            bottomPadding = sheetBottomPadding
        )

        VideoDetailSheet(
            videoDetail = playerState.videoDetail,
            isShowVideoDetailUI = screenState.isShowVideoDetailUI,
            onDismiss = { onScreenAction(ScreenAction.SetVideoDetailVisible(false)) },
            modifier = otherSheetModifier,
            onMaskAlphaChange = { onMaskAlphaChange(it) },
            bottomPadding = screenState.videoHeight + 80.dp
        )

        ArchiveSheet(
            lazyListState = screenState.archiveListState,
            modifier = otherSheetModifier,
            currentArchiveIndex = playerState.currentArchiveIndex,
            archiveMeta = playerState.videoArchiveMeta,
            archives = playerState.videoArchives,
            isShowArchiveUI = screenState.isShowArchiveUI,
            onMaskAlphaChange = { onMaskAlphaChange(it) },
            onDismiss = { onScreenAction(ScreenAction.SetArchiveVisible(false)) },
            onVideoMenuAction = onVideoMenuAction,
            bottomPadding = screenState.videoHeight + 80.dp
        )

        AddCoinSheet(
            isShowAddCoinUI = screenState.isShowAddCoinUI,
            onDismiss = {
                onScreenAction(ScreenAction.SetAddCoinVisible(false))
            },
            onVideoMenuAction = {
                onScreenAction(ScreenAction.SetAddCoinVisible(false))
                onVideoMenuAction(it)
            }
        )

        UserInfoCardSheet(
            isShowSheet = screenState.isShowUpInfoSheet,
            isLoading = playerState.infoCardModel == null,
            face = playerState.infoCardModel?.card?.face ?: "",
            name = playerState.infoCardModel?.card?.name ?: "",
            sign = playerState.infoCardModel?.card?.sign ?: "",
            isSubscribed = playerState.infoCardModel?.following == true,
            follower = playerState.infoCardModel?.follower ?: 0,
            likeNum = playerState.infoCardModel?.likeNum ?: 0,
            attention = playerState.infoCardModel?.card?.attention ?: 0,
            official = playerState.infoCardModel?.card?.official?.title ?: "",
            level = playerState.infoCardModel?.card?.levelInfo?.currentLevel ?: 0,
            uploadedVideos = userVideos,
            currentBvid = playParam.bvid,
            onSubscriptionChanged = {},
            onDismiss = {
                onScreenAction(ScreenAction.SetUpInfoVisible(false))
            },
            modifier = otherSheetModifier,
            onMaskAlphaChange = { onMaskAlphaChange(it) },
            onVideoChange = {
                onVideoMenuAction(
                    VideoMenuAction.SwitchVideo(it)
                )
            },
            bottomPadding = screenState.videoHeight + 80.dp
        )

        DownloadSheet(
            isShowSheet = screenState.isShowDownloadSheet,
            quality = mediaState.quality,
            defaultQuality = mediaState.videoQuality,
            onDismiss = {
                onScreenAction(ScreenAction.SetDownloadVisible(false))
            },
            onDownloadClick = {
                scope.launch {
                    if (!context.checkedPermissions(WRITE_STORAGE_PERMISSION)) {
                        EventBus.send(Event.AppEvent.PermissionRequestEvent(WRITE_STORAGE_PERMISSION))
                    } else {
                        onDownload(it)
                    }
                }
            }
        )

        VideoMenuSheet(
            isShowSheet = screenState.isShowVideoMenuUIAction,
            onDismiss = {
                onScreenAction(ScreenAction.SetVideoMenuVisible(false))
            }
        ) {
            when (it) {
                R.string.str_save_playlist -> {
                    onScreenAction(ScreenAction.SetVideoMenuVisible(false))
                    onVideoMenuAction(VideoMenuAction.LoadSimpleFolders)
                    onScreenAction(ScreenAction.SetModifyFolderVisible(true))
                }

                R.string.str_save_watch_later -> {
                    onVideoMenuAction(VideoMenuAction.AddToView)
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun LandscapeFullscreenVideoPage(
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
            onLongPressStart = { },
            onLongPressEnd = {},
            onControlUIChange = onControlUIChange,
            onSetting = { onScreenAction(ScreenAction.SetSettingVisible(true)) },
            onBackPress = onBackPress,
            hintContent = {},
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


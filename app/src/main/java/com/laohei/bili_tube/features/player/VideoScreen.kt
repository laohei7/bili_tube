package com.laohei.bili_tube.features.player

import android.app.Activity
import android.content.Context
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
import com.laohei.bili_tube.features.player.state.media.DefaultMediaManager
import com.laohei.bili_tube.features.player.state.media.MediaState
import com.laohei.bili_tube.features.player.state.screen.DefaultScreenManager
import com.laohei.bili_tube.features.player.state.screen.ScreenAction
import com.laohei.bili_tube.features.player.state.screen.ScreenState
import com.laohei.bili_tube.ui.component.dialog.CreatedFolderDialog
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.component.layout.DeviceConfiguration
import com.laohei.bili_tube.ui.component.sheet.FolderSheet
import com.laohei.bili_tube.utill.SystemUtil
import com.laohei.bili_tube.utill.checkedPermissions
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.hideSystemUI
import com.laohei.bili_tube.utill.isOrientationPortrait
import com.laohei.bili_tube.utill.showSystemUI
import com.laohei.bili_tube.utill.toggleOrientation
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

@OptIn(UnstableApi::class)
@Composable
fun VideoScreen(
    systemBarHeight: Dp = SystemUtil.getSystemBarHeightDp(),
    context: Context = LocalContext.current,
    density: Density = LocalDensity.current,
    view: View = LocalView.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    activity: Activity? = LocalActivity.current,
    configuration: Configuration = LocalConfiguration.current,
    cronetEngine: CronetEngine = koinInject(),
    simpleCache: SimpleCache = koinInject(),
    playParam: PlayParam,
    upPress: () -> Unit
) {
    // The Listen Control UI is automatically hidden
    var autoHideJob by remember { mutableStateOf<Job?>(null) }
    var lastTapTimestamp by remember { mutableLongStateOf(0) }
    var tiggerAutoFullscreen by remember { mutableStateOf(true) }

    val isOrientationPortrait = isOrientationPortrait()
    val defaultMediaManager = remember {
        DefaultMediaManager(context, cronetEngine, simpleCache, playParam.width, playParam.height)
    }
    val defaultScreenManager = remember {
        DefaultScreenManager(
            density,
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
    val viewModel = koinViewModel<PlayerViewModel> {
        parametersOf(playParam, defaultMediaManager, defaultScreenManager)
    }

    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val mediaState by viewModel.state.collectAsStateWithLifecycle()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    val replies = playerState.replies.collectAsLazyPagingItems()
    val userVideos = playerState.uploadedVideos.collectAsLazyPagingItems()


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
                    viewModel.onScreenAction(ScreenAction.ShowControlUIAction(false), false)
                }
            }
        }
    }

    // video size changed
    LaunchedEffect(mediaState.width, mediaState.height) {
        viewModel.calculateScreenSize(mediaState.width, mediaState.height)
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
                viewModel.exoPlayer().play()
            }
        },
        onPause = {
            if (mediaState.isPlaying) {
                viewModel.exoPlayer().pause()
            }
        }
    )

    fun onFullscreenChange(uiType: DeviceConfiguration, isFullscreen: Boolean) {
        val aspectRatio = mediaState.width.toFloat() / mediaState.height
        val keepPortrait = when {
            isFullscreen.not() -> isOrientationPortrait
            isFullscreen -> (uiType == DeviceConfiguration.MOBILE_PORTRAIT
                    || uiType == DeviceConfiguration.TABLE_PORTRAIT) && aspectRatio <= 1

            else -> false
        }
        viewModel.onFullscreenChange(
            isFullscreen,
            when {
                keepPortrait && isFullscreen -> screenState.screenHeight.dp
                else -> screenState.originalVideoHeight
            },
            isOrientationPortrait
        )
        when {
            keepPortrait.not() -> activity?.toggleOrientation()
        }
    }

    fun backHandler() {
        Log.d("TAG", "VideoScreen: ${screenState.isFullscreen}")
        when {
            screenState.isLockScreen -> {}
            screenState.isFullscreen -> {
                onFullscreenChange(
                    if (isOrientationPortrait) DeviceConfiguration.MOBILE_PORTRAIT else DeviceConfiguration.MOBILE_LANDSCAPE,
                    false
                )
            }

            else -> {
                upPress()
            }
        }
    }

    BackHandler(enabled = screenState.isFullscreen) {
        backHandler()
    }

    LaunchedEffect(mediaState.isPlaying) {
        while (mediaState.isPlaying) {
            val history = viewModel.exoPlayer().currentPosition / 1000
            viewModel.uploadVideoHistory(history)
            delay(15000)
        }
    }

    AdaptiveLayout { uiType, width, height ->
        when (uiType) {
            DeviceConfiguration.MOBILE_PORTRAIT,
            DeviceConfiguration.TABLE_PORTRAIT -> {
                PortraitVideoPage(
                    context = context,
                    nestedScrollConnection = viewModel.nestedScrollConnection,
                    exoPlayer = viewModel.exoPlayer(),
                    playParam = viewModel.playParam,
                    screenState = screenState,
                    mediaState = mediaState,
                    playerState = playerState,
                    replies = replies,
                    userVideos = userVideos,
                    onVideoFrameChange = {
                        viewModel.onScreenAction(
                            ScreenAction.SetBackgroundAction(it), isOrientationPortrait
                        )
                    },
                    onPostHistory = {},
                    onControlUIChange = {
                        viewModel.onScreenAction(
                            ScreenAction.ShowControlUIAction(it), isOrientationPortrait
                        )
                    },
                    onBackPress = { backHandler() },
                    onProgressChange = { viewModel.seekTo(it) },
                    onPlayChange = { viewModel.togglePlayPause() },
                    onFullscreenChange = { isFullscreen ->
                        onFullscreenChange(uiType, isFullscreen)
                    },
                    onVideoMenuAction = viewModel::onVideoMenuAction,
                    onScreenAction = {
                        viewModel.onScreenAction(
                            it, false,
                            lockScreenCallback = when (it) {
                                is ScreenAction.LockScreenAction -> {
                                    {
                                        onFullscreenChange(uiType, true)
                                    }
                                }

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
                            ScreenAction.DownloadUIAction(false),
                            isOrientationPortrait
                        )
                    }
                )
            }

            DeviceConfiguration.MOBILE_LANDSCAPE -> {
                LandscapeFullscreenVideoPage(
                    context = context,
                    playParam = viewModel.playParam,
                    exoPlayer = viewModel.exoPlayer(),
                    screenState = screenState,
                    mediaState = mediaState,
                    playerState = playerState,
                    onVideoFrameChange = {
                        viewModel.onScreenAction(ScreenAction.SetBackgroundAction(it), false)
                    },
                    onControlUIChange = {
                        viewModel.onScreenAction(ScreenAction.ShowControlUIAction(it), false)
                    },
                    onBackPress = { backHandler() },
                    onProgressChange = { viewModel.seekTo(it) },
                    onPlayChange = { viewModel.togglePlayPause() },
                    onFullscreenChange = { isFullscreen ->
                        onFullscreenChange(DeviceConfiguration.MOBILE_PORTRAIT, isFullscreen)
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
                        playParam = viewModel.playParam,
                        exoPlayer = viewModel.exoPlayer(),
                        screenState = screenState,
                        mediaState = mediaState,
                        playerState = playerState,
                        onVideoFrameChange = {
                            viewModel.onScreenAction(ScreenAction.SetBackgroundAction(it), false)
                        },
                        onControlUIChange = {
                            viewModel.onScreenAction(ScreenAction.ShowControlUIAction(it), false)
                        },
                        onBackPress = { backHandler() },
                        onProgressChange = { viewModel.seekTo(it) },
                        onPlayChange = { viewModel.togglePlayPause() },
                        onFullscreenChange = { isFullscreen ->
                            onFullscreenChange(uiType, isFullscreen)
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
                viewModel.onScreenAction(ScreenAction.SettingUIAction(false), isOrientationPortrait)
            },
            onScreenAction = { action ->
                viewModel.onScreenAction(ScreenAction.SettingUIAction(false), isOrientationPortrait)
                viewModel.onScreenAction(action, isOrientationPortrait)
            }
        )
        PlaySpeedSheet(
            isShowSheet = screenState.isShowSpeedUI,
            speed = mediaState.speed,
            onSpeedChanged = { viewModel.setSpeed(it) },
            onDismiss = {
                viewModel.onScreenAction(
                    ScreenAction.SettingSpeedUIAction(false),
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
                    ScreenAction.SettingQualityUIAction(false),
                    isOrientationPortrait
                )
            },
            onQualityChanged = {
                viewModel.onScreenAction(
                    ScreenAction.SettingQualityUIAction(false),
                    isOrientationPortrait
                )
                viewModel.switchQuality(it)
            }
        )
        OtherSettingsSheet(
            isShowSheet = screenState.isShowOtherSettingUI,
            autoSkip = playerState.autoSkip,
            onDismiss = {
                viewModel.onScreenAction(
                    ScreenAction.OtherSettingUIAction(false),
                    isOrientationPortrait
                )
            },
            videoSettingActionClick = viewModel::handleVideoSettingAction
        )

        // folder
        FolderSheet(
            folders = playerState.folders,
            isShowSheet = screenState.isShowFolderSheet,
            onDismiss = {
                viewModel.onScreenAction(
                    ScreenAction.ModifyFolderUIAction(false),
                    isOrientationPortrait
                )
            },
            onCreateFolder = {
                viewModel.onScreenAction(
                    ScreenAction.CreatedFolderUIAction(true),
                    isOrientationPortrait
                )
            },
            onAddToFolder = { addAids, delAids ->
                viewModel.onVideoMenuAction(
                    VideoMenuAction.AddToFoldersAction(
                        addAids = addAids,
                        delAids = delAids,
                    )
                )
                viewModel.onScreenAction(
                    ScreenAction.ModifyFolderUIAction(false),
                    isOrientationPortrait
                )
            }
        )

        CreatedFolderDialog(
            isShowDialog = screenState.isShowAddFolder,
            value = playerState.folderName,
            onValueChange = viewModel::onFolderNameChanged,
            onSubmit = viewModel::addNewFolder,
            checked = playerState.isPrivate,
            onCheckedChange = viewModel::onPrivateChanged,
            onDismiss = {
                viewModel.onFolderNameChanged("")
                viewModel.onScreenAction(
                    ScreenAction.CreatedFolderUIAction(false),
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
    playerState: PlayerState,
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
            onSetting = { onScreenAction(ScreenAction.SettingUIAction(true)) },
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
                        if (it is ScreenAction.ModifyFolderUIAction) {
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
                if (it is ScreenAction.ModifyFolderUIAction) {
                    onSelectedAidChange(playParam.aid)
                }
                if (it is ScreenAction.ModifyFolderUIAction) {
                    onVideoMenuAction(VideoMenuAction.GetSimpleFoldersAction)
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
            onDismiss = { onScreenAction(ScreenAction.ReplyUIAction(false)) },
            modifier = otherSheetModifier,
            onMaskAlphaChange = { onMaskAlphaChange(it) },
            bottomPadding = sheetBottomPadding
        )

        VideoDetailSheet(
            videoDetail = playerState.videoDetail,
            isShowVideoDetailUI = screenState.isShowVideoDetailUI,
            onDismiss = { onScreenAction(ScreenAction.VideoDetailUIAction(false)) },
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
            onDismiss = { onScreenAction(ScreenAction.ArchiveUIAction(false)) },
            onVideoMenuAction = onVideoMenuAction,
            bottomPadding = screenState.videoHeight + 80.dp
        )

        AddCoinSheet(
            isShowAddCoinUI = screenState.isShowAddCoinUI,
            onDismiss = {
                onScreenAction(ScreenAction.AddCoinUIAction(false))
            },
            onVideoMenuAction = {
                onScreenAction(ScreenAction.AddCoinUIAction(false))
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
                onScreenAction(ScreenAction.UpInfoUIAction(false))
            },
            modifier = otherSheetModifier,
            onMaskAlphaChange = { onMaskAlphaChange(it) },
            onVideoChange = {
                onVideoMenuAction(
                    VideoMenuAction.SwitchVideoAction(it)
                )
            },
            bottomPadding = screenState.videoHeight + 80.dp
        )

        DownloadSheet(
            isShowSheet = screenState.isShowDownloadSheet,
            quality = mediaState.quality,
            defaultQuality = mediaState.videoQuality,
            onDismiss = {
                onScreenAction(ScreenAction.DownloadUIAction(false))
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
                onScreenAction(ScreenAction.VideoMenuUIAction(false))
            }
        ) {
            when (it) {
                R.string.str_save_playlist -> {
                    onScreenAction(ScreenAction.VideoMenuUIAction(false))
                    onVideoMenuAction(VideoMenuAction.GetSimpleFoldersAction)
                    onScreenAction(ScreenAction.ModifyFolderUIAction(true))
                }

                R.string.str_save_watch_later -> {
                    onVideoMenuAction(VideoMenuAction.AddToViewAction)
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
    playerState: PlayerState,
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
            onSetting = { onScreenAction(ScreenAction.SettingUIAction(true)) },
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
                        if (it is ScreenAction.ModifyFolderUIAction) {
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


package com.laohei.bili_tube.presentation.player

import android.graphics.Bitmap
import android.util.Log
import android.view.TextureView
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.annotation.OptIn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.compose.collectAsLazyPagingItems
import com.laohei.bili_sdk.module_v2.bangumi.RelatedBangumiItem
import com.laohei.bili_sdk.module_v2.video.ArchiveMeta
import com.laohei.bili_sdk.module_v2.video.BangumiDetailModel
import com.laohei.bili_sdk.module_v2.video.BangumiStat
import com.laohei.bili_sdk.module_v2.video.PublishModel
import com.laohei.bili_sdk.module_v2.video.RatingModel
import com.laohei.bili_sdk.module_v2.video.VideoDetailModel
import com.laohei.bili_sdk.module_v2.video.VideoPageListModel
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.component.lottie.LottieIconPlaying
import com.laohei.bili_tube.component.text.IconText
import com.laohei.bili_tube.component.video.FolderSheet
import com.laohei.bili_tube.component.video.HorizontalVideoItem
import com.laohei.bili_tube.component.video.VideoAction
import com.laohei.bili_tube.component.video.VideoItem
import com.laohei.bili_tube.core.WRITE_STORAGE_PERMISSION
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.core.util.LifecycleEffect
import com.laohei.bili_tube.core.util.SystemUtil
import com.laohei.bili_tube.core.util.checkedPermissions
import com.laohei.bili_tube.core.util.hideSystemUI
import com.laohei.bili_tube.core.util.showSystemUI
import com.laohei.bili_tube.core.util.toggleOrientation
import com.laohei.bili_tube.core.util.useLightSystemBarIcon
import com.laohei.bili_tube.presentation.player.component.BlurBackgroundImage
import com.laohei.bili_tube.presentation.player.component.CoinSheet
import com.laohei.bili_tube.presentation.player.component.CommentCard
import com.laohei.bili_tube.presentation.player.component.FullscreenVideoActions
import com.laohei.bili_tube.presentation.player.component.PlayerPlaceholder
import com.laohei.bili_tube.presentation.player.component.PlayerSnackHost
import com.laohei.bili_tube.presentation.player.component.RelatedBangumiHorizontalList
import com.laohei.bili_tube.presentation.player.component.RelatedHorizontalList
import com.laohei.bili_tube.presentation.player.component.UserSimpleInfo
import com.laohei.bili_tube.presentation.player.component.VideoDetailSheet
import com.laohei.bili_tube.presentation.player.component.VideoMenus
import com.laohei.bili_tube.presentation.player.component.VideoSimpleInfo
import com.laohei.bili_tube.presentation.player.component.archive.ArchiveMetaItem
import com.laohei.bili_tube.presentation.player.component.archive.ArchiveSheet
import com.laohei.bili_tube.presentation.player.component.archive.PageListWidget
import com.laohei.bili_tube.presentation.player.component.control.PlayerControl
import com.laohei.bili_tube.presentation.player.component.reply.VideoReplySheet
import com.laohei.bili_tube.presentation.player.component.settings.DownloadSheet
import com.laohei.bili_tube.presentation.player.component.settings.PlaySpeedSheet
import com.laohei.bili_tube.presentation.player.component.settings.VideoQualitySheet
import com.laohei.bili_tube.presentation.player.component.settings.VideoSettingsSheet
import com.laohei.bili_tube.presentation.player.state.media.DefaultMediaManager
import com.laohei.bili_tube.presentation.player.state.media.MediaState
import com.laohei.bili_tube.presentation.player.state.screen.DefaultScreenManager
import com.laohei.bili_tube.presentation.player.state.screen.ScreenAction
import com.laohei.bili_tube.presentation.player.state.screen.ScreenState
import com.laohei.bili_tube.ui.theme.Pink
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.isOrientationPortrait
import com.laohei.bili_tube.utill.toTimeAgoString
import com.laohei.bili_tube.utill.toViewString
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.chromium.net.CronetEngine
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

private const val TAG = "PlayerScreen"

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    params: Route.Play,
    upPress: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val activity = LocalActivity.current
    val configuration = LocalConfiguration.current
    val systemBarHeight = SystemUtil.getStatusBarHeightDp() + SystemUtil.getNavigateBarHeightDp()

    val cronetEngine = koinInject<CronetEngine>()
    val simpleCache = koinInject<SimpleCache>()

    val viewModel =
        koinViewModel<PlayerViewModel> {
            parametersOf(
                params,
                DefaultMediaManager(
                    context = context,
                    cronetEngine = cronetEngine,
                    simpleCache = simpleCache,
                    originalWidth = params.width,
                    originalHeight = params.height
                ),
                DefaultScreenManager(
                    density,
                    configuration.screenHeightDp + systemBarHeight.value.roundToInt(),
                    configuration.screenWidthDp,
                    params.width,
                    params.height
                )
            )
        }
    val playerState by viewModel.playerState.collectAsState()
    val videoReplies = playerState.replies.collectAsLazyPagingItems()
    val mediaState by viewModel.state.collectAsStateWithLifecycle()


    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

//    val lazyListState = screenState.listState


    val isOrientationPortrait = isOrientationPortrait()

    val nestedScrollConnection = viewModel.nestedScrollConnection

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
    val videoWidthFraction by animateFloatAsState(
        targetValue = when {
            screenState.isFullscreen && screenState.isShowReplySheet && !isOrientationPortrait -> 0.65f
            else -> 1f
        }
    )
    val replayWidthFraction by animateFloatAsState(
        targetValue = when {
            screenState.isFullscreen && screenState.isShowReplySheet && !isOrientationPortrait -> 0.35f
            else -> 0f
        }
    )
    val videoModifier = when {
        isOrientationPortrait -> {
            Modifier
                .height(animatedVideoHeight)
                .width(animatedVideoHeight * mediaState.width.toFloat() / mediaState.height)
        }

        else -> {
            Modifier
                .fillMaxHeight()
        }
    }

    val videoControlModifier = when {
        isOrientationPortrait -> {
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        }

        else -> videoModifier.fillMaxWidth(videoWidthFraction)
    }
    val replyModifier = when {
        isOrientationPortrait -> otherSheetModifier
        else -> Modifier.fillMaxWidth(replayWidthFraction)
    }
    val isSystemDarkTheme = isSystemInDarkTheme()
    val animatedOffset by animateIntAsState(targetValue = screenState.relatedListOffset.toInt())
    val enabledDraggable =
        !isOrientationPortrait && screenState.isFullscreen && !screenState.isLockScreen

    fun backPressHandle() {
        if (screenState.isLockScreen) {
            return
        }
        if (screenState.isFullscreen.not()) {
            upPress.invoke()
        }
        viewModel.fullscreenChanged(false, screenState.originalVideoHeight, isOrientationPortrait)
        if (!isOrientationPortrait) {
            activity?.toggleOrientation()
        }
    }

    BackHandler(enabled = screenState.isFullscreen) {
        backPressHandle()
    }


    DisposableEffect(Unit) {
        activity?.useLightSystemBarIcon(false)
        onDispose {
            activity?.useLightSystemBarIcon(isSystemDarkTheme.not())
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

    DisposableEffect(mediaState.isPlaying, view) {
        view.keepScreenOn = mediaState.isPlaying
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .draggable(
                enabled = enabledDraggable,
                state = rememberDraggableState {
                    viewModel.relatedListDragHandle(it)
                },
                onDragStopped = {
                    viewModel.adjustRelatedListOffset()
                },
                orientation = Orientation.Vertical
            )
    ) {
        BlurBackgroundImage(
            bitmap = screenState.bitmap,
            isDrag = screenState.isDrag,
            isFullscreen = screenState.isFullscreen,
        )
        VideoArea(
            modifier = videoModifier.then(
                when {
                    isOrientationPortrait -> Modifier
                    else -> Modifier.align(Alignment.Center)
                }
            ),
            videoControlModifier = videoControlModifier,
            exoPlayer = viewModel.exoPlayer(),
            mediaState = mediaState,
            screenState = screenState,
            playerState = playerState,
            videoFrame = {
                viewModel.updateState(screenState.copy(bitmap = it))
            },
            fullscreen = {
                val aspectRatio = mediaState.width.toFloat() / mediaState.height
                val toLandscape = aspectRatio > 1f
                Log.d("TAG", "PlayerScreen: $aspectRatio")
                viewModel.fullscreenChanged(
                    it,
                    when {
                        toLandscape.not() && it -> screenState.screenHeight.dp
                        else -> screenState.originalVideoHeight
                    },
                    isOrientationPortrait
                )
                when {
                    (isOrientationPortrait && !it).not() && toLandscape -> {
                        activity?.toggleOrientation()
                    }
                }
            },
            uploadVideoHistory = viewModel::uploadVideoHistory,
            seekTo = viewModel::seekTo,
            setSpeed = viewModel::setSpeed,
            togglePlayPause = viewModel::togglePlayPause,
            backPressedClick = { backPressHandle() },
            onShowUIChanged = {
                viewModel.updateState(screenState.copy(isShowUI = it))
            },
            screenActionClick = {
                when {
                    it is ScreenAction.ShowReplyAction && screenState.isFullscreen
                            && isOrientationPortrait -> {
                        backPressHandle()
                    }
                }
                viewModel.screenActionHandle(
                    action = it,
                    isOrientationPortrait = isOrientationPortrait,
                    scope = scope,
                )
            },
            videoMenuClick = viewModel::videoMenuActionHandle
        )

        when {
            isOrientationPortrait -> {
                GetContent(
                    modifier = videoContentModifier,
                    playerState = playerState,
                    screenState = screenState,
                    bottomPadding = screenState.videoHeight + 80.dp,
                    screenActionClick = {
                        viewModel.screenActionHandle(
                            it, true, scope,
                            updateParamsCallback = { newParams ->
                                scope.launch { viewModel.updateParams(newParams) }
                            })
                    },
                    videoMenuActionClick = viewModel::videoMenuActionHandle,
                    videoPlayActionClick = viewModel::videoPlayActionHandle
                )

                Box(
                    modifier = contentModifier
                        .graphicsLayer { alpha = screenState.maskAlpha }
                        .background(Color.Black)
                )
            }

            !isOrientationPortrait && !screenState.isLockScreen -> {
                val relatedListModifier = Modifier
                    .zIndex(100f)
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(bottom = 12.dp)
                    .offset {
                        IntOffset(0, animatedOffset)
                    }
                when {
                    playerState.isVideo -> {
                        RelatedHorizontalList(
                            modifier = relatedListModifier,
                            related = playerState.videoDetail?.related ?: emptyList(),
                            onClick = {
                                scope.launch { viewModel.updateParams(it) }
                            }
                        )
                    }

                    else -> {
                        RelatedBangumiHorizontalList(
                            modifier = relatedListModifier,
                            related = playerState.relatedBangumis ?: emptyList(),
                            onClick = {
                                scope.launch { viewModel.updateParams(it) }
                            }
                        )
                    }
                }
            }
        }

        VideoReplySheet(
            isShowReply = screenState.isShowReplySheet,
            replies = videoReplies,
            onDismiss = { viewModel.updateState(screenState.copy(isShowReplySheet = false)) },
            modifier = replyModifier.then(
                when {
                    isOrientationPortrait -> Modifier
                    else -> Modifier.align(Alignment.TopEnd)
                }
            ),
            maskAlphaChanged = { viewModel.maskAlphaChanged(it) },
            bottomPadding = screenState.videoHeight + 80.dp
        )

        VideoDetailSheet(
            videoDetail = playerState.videoDetail,
            isShowDetail = screenState.isShowDetailSheet,
            onDismiss = { viewModel.updateState(screenState.copy(isShowDetailSheet = false)) },
            modifier = otherSheetModifier,
            maskAlphaChanged = { viewModel.maskAlphaChanged(it) },
            bottomPadding = screenState.videoHeight + 80.dp
        )

        ArchiveSheet(
            lazyListState = screenState.archiveListState,
            modifier = otherSheetModifier,
            currentArchiveIndex = playerState.currentArchiveIndex,
            archiveMeta = playerState.videoArchiveMeta,
            archives = playerState.videoArchives,
            isShowSheet = screenState.isShowArchiveSheet,
            maskAlphaChanged = { viewModel.maskAlphaChanged(it) },
            onDismiss = {
                viewModel.screenActionHandle(
                    ScreenAction.ShowArchiveSheetAction(false),
                    true
                )
            },
            onClick = {
                scope.launch { viewModel.updateParams(it) }
            },
            bottomPadding = screenState.videoHeight + 80.dp
        )

        VideoSettingsSheet(
            isShowSheet = screenState.isShowVideoSettingsSheet,
            speed = mediaState.speed,
            quality = mediaState.defaultQuality.second,
            onDismiss = {
                viewModel.screenActionHandle(
                    ScreenAction.ShowSettingsSheetAction(false),
                    isOrientationPortrait
                )
            },
            action = { action ->
                viewModel.screenActionHandle(
                    ScreenAction.ShowSettingsSheetAction(false),
                    isOrientationPortrait,
                )
                viewModel.screenActionHandle(
                    action,
                    isOrientationPortrait,
                    lockScreenCallback = {
                        val aspectRatio = mediaState.width.toFloat() / mediaState.height
                        val toLandscape = aspectRatio > 1f
                        viewModel.fullscreenChanged(
                            true,
                            when {
                                toLandscape.not() -> screenState.screenHeight.dp
                                else -> screenState.originalVideoHeight
                            },
                            isOrientationPortrait
                        )
                        if (isOrientationPortrait && toLandscape) {
                            activity?.toggleOrientation()
                        }
                    }
                )
            }
        )

        PlaySpeedSheet(
            isShowSheet = screenState.isShowSpeedSheet,
            speed = mediaState.speed,
            onSpeedChanged = {
                viewModel.setSpeed(it)
            },
            onDismiss = {
                viewModel.screenActionHandle(
                    ScreenAction.ShowSpeedSheetAction(false),
                    isOrientationPortrait
                )
            }
        )
        VideoQualitySheet(
            isShowSheet = screenState.isShowQualitySheet,
            quality = mediaState.quality,
            defaultQuality = mediaState.defaultQuality,
            onDismiss = {
                viewModel.screenActionHandle(
                    ScreenAction.ShowQualitySheetAction(false),
                    isOrientationPortrait
                )
            },
            onQualityChanged = {
                viewModel.screenActionHandle(
                    ScreenAction.ShowQualitySheetAction(false),
                    isOrientationPortrait
                )
                viewModel.switchQuality(it)
            }
        )

        DownloadSheet(
            isShowSheet = screenState.isShowDownloadSheet,
            quality = mediaState.quality,
            defaultQuality = mediaState.defaultQuality,
            onDismiss = {
                viewModel.screenActionHandle(
                    ScreenAction.ShowDownloadSheetAction(false),
                    isOrientationPortrait
                )
            },
            onDownloadClick = {
                scope.launch {
                    if (activity?.checkedPermissions(WRITE_STORAGE_PERMISSION) == false) {
                        EventBus.send(Event.AppEvent.PermissionRequestEvent(WRITE_STORAGE_PERMISSION))
                    } else {
                        viewModel.download(it)
                        viewModel.screenActionHandle(
                            ScreenAction.ShowDownloadSheetAction(false),
                            isOrientationPortrait
                        )
                    }
                }
            }
        )

        CoinSheet(
            isShowSheet = screenState.isShowCoinSheet,
            onDismiss = {
                viewModel.screenActionHandle(
                    ScreenAction.ShowCoinSheetAction(false),
                    isOrientationPortrait
                )
            },
            onClick = {
                viewModel.videoMenuActionHandle(it)
                viewModel.screenActionHandle(
                    ScreenAction.ShowCoinSheetAction(false),
                    isOrientationPortrait
                )
            }
        )

        FolderSheet(
            folders = playerState.folders,
            isShowSheet = screenState.isShowFolderSheet,
            onDismiss = {
                viewModel.screenActionHandle(
                    ScreenAction.ShowFolderSheetAction(false),
                    isOrientationPortrait
                )
            },
            onClick = {
                viewModel.videoMenuActionHandle(it)
                viewModel.screenActionHandle(
                    ScreenAction.ShowFolderSheetAction(false),
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
private fun GetContent(
    modifier: Modifier,
    playerState: PlayerState,
    screenState: ScreenState,
    bottomPadding: Dp = 0.dp,
    screenActionClick: (ScreenAction) -> Unit,
    videoMenuActionClick: (VideoAction.VideoMenuAction) -> Unit,
    videoPlayActionClick: (VideoAction.VideoPlayAction) -> Unit,
) {
    when {
        playerState.isVideo -> {
            playerState.videoDetail?.let {
                VideoContent(
                    modifier = modifier,
                    lazyListState = screenState.listState,
                    hasFavoured = playerState.hasFavoured,
                    hasCoin = playerState.hasCoin,
                    hasLike = playerState.hasLike,
                    isDownloaded = playerState.isDownloaded,
                    isShowLikeAnimation = screenState.isShowLikeAnimation,
                    isFullscreen = screenState.isFullscreen,
                    videoDetail = it,
                    videoArchiveMeta = playerState.videoArchiveMeta,
                    currentArchiveIndex = playerState.currentArchiveIndex + 1,
                    videoPageList = playerState.videoPageList,
                    currentPageListIndex = playerState.currentPageListIndex,
                    screenActionClick = screenActionClick,
                    videoMenuClick = videoMenuActionClick,
                    videoPlayClick = videoPlayActionClick
                )
            } ?: run {
                PlayerPlaceholder(modifier = modifier)
            }
        }

        else -> {
            playerState.bangumiDetail?.let {
                BangumiContent(
                    modifier = modifier,
                    lazyListState = screenState.listState,
                    bangumiDetailModel = it,
                    currentEpId = playerState.currentEpId,
                    initialEpisodeIndex = playerState.initialEpisodeIndex,
                    initialSeasonIndex = playerState.initialSeasonIndex,
                    hasFavoured = playerState.hasFavoured,
                    hasCoin = playerState.hasCoin,
                    hasLike = playerState.hasLike,
                    isDownloaded = playerState.isDownloaded,
                    isShowLikeAnimation = screenState.isShowLikeAnimation,
                    isFullscreen = screenState.isFullscreen,
                    relatedBangumis = playerState.relatedBangumis ?: emptyList(),
                    bottomPadding = bottomPadding,
                    videoPlayActionClick = videoPlayActionClick,
                    screenActionClick = screenActionClick,
                    videoMenuClick = videoMenuActionClick
                )
            } ?: run {
                PlayerPlaceholder(modifier = modifier)
            }
        }
    }
}


@OptIn(UnstableApi::class)
@Composable
private fun VideoArea(
    modifier: Modifier = Modifier,
    videoControlModifier: Modifier = Modifier,
    exoPlayer: ExoPlayer,
    playerState: PlayerState,
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
        title = playerState.videoDetail?.view?.title ?: playerState.bangumiDetail?.title ?: "",
        progress = mediaState.progress,
        bufferProgress = mediaState.bufferProgress,
        isShowUI = screenState.isShowUI,
        isShowRelatedList = screenState.isShowRelatedList,
        isFullscreen = screenState.isFullscreen,
        isLockScreen = screenState.isLockScreen,
        fullscreen = fullscreen,
        isPlaying = mediaState.isPlaying,
        isLoading = mediaState.isLoading,
        totalDuration = mediaState.totalDuration.formatTimeString(),
        currentDuration = mediaState.currentDuration.formatTimeString(),
        onPlayChanged = { togglePlayPause.invoke() },
        onProgressChanged = { seekTo.invoke(it) },
        onLongPressStart = { setSpeed.invoke(2f) },
        onLongPressEnd = { setSpeed.invoke(1f) },
        onShowUIChanged = onShowUIChanged,
        backPressedClick = backPressedClick,
        settingsClick = {
            screenActionClick.invoke(ScreenAction.ShowSettingsSheetAction(true))
        },
        actionContent = {
            FullscreenVideoActions(
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
                isShowLikeAnimation = screenState.isShowLikeAnimation,
                isFullscreen = screenState.isFullscreen,
                showLabel = screenState.isFullscreen && !isOrientationPortrait(),
                screenActionClick = screenActionClick,
                videoMenuClick = videoMenuClick,
            )
        },
        unlockScreenClick = {
            screenActionClick.invoke(ScreenAction.LockScreenAction(false))
        }
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

@Composable
private fun VideoContent(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    videoDetail: VideoDetailModel,
    videoArchiveMeta: ArchiveMeta?,
    videoPageList: List<VideoPageListModel>?,
    hasLike: Boolean,
    hasCoin: Boolean,
    hasFavoured: Boolean,
    isDownloaded: Boolean,
    isShowLikeAnimation: Boolean,
    isFullscreen: Boolean,
    currentPageListIndex: Int,
    currentArchiveIndex: Int,
    screenActionClick: (ScreenAction) -> Unit,
    videoMenuClick: (VideoAction.VideoMenuAction) -> Unit,
    videoPlayClick: (VideoAction.VideoPlayAction) -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                VideoSimpleInfo(
                    title = videoDetail.view.title,
                    view = videoDetail.view.stat.view.toViewString(),
                    date = videoDetail.view.pubdate.toTimeAgoString(),
                    tag = when {
                        videoDetail.tags.isNotEmpty() -> videoDetail.tags.first().tagName
                        else -> null
                    },
                    onClick = {
                        screenActionClick.invoke(ScreenAction.ShowVideoDetailAction)
                    }
                )
            }
            item {
                UserSimpleInfo(
                    face = videoDetail.view.owner.face,
                    name = videoDetail.view.owner.name,
                    fans = videoDetail.card.card.fans.toViewString(),
                    onClick = {}
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                VideoMenus(
                    great = videoDetail.view.stat.like.toViewString(),
                    coin = videoDetail.view.stat.coin.toViewString(),
                    star = videoDetail.view.stat.favorite.toViewString(),
                    share = videoDetail.view.stat.share.toViewString(),
                    hasLike = hasLike,
                    hasCoin = hasCoin,
                    hasFavoured = hasFavoured,
                    isDownloaded = isDownloaded,
                    isShowLikeAnimation = isShowLikeAnimation,
                    isFullscreen = isFullscreen,
                    onClick = videoMenuClick,
                    coinClick = { screenActionClick.invoke(ScreenAction.ShowCoinSheetAction(true)) },
                    favouredClick = {
                        screenActionClick.invoke(ScreenAction.ShowFolderSheetAction(true))
                    },
                    downloadClick = {
                        screenActionClick.invoke(ScreenAction.ShowDownloadSheetAction(true))
                    },
                    onAnimationEndCallback = {
                        screenActionClick.invoke(ScreenAction.ShowLikeAnimationAction(false))
                    }
                )
            }
            videoPageList?.let {
                item {
                    PageListWidget(
                        pageList = it,
                        currentPageListIndex = currentPageListIndex,
                        onClick = videoPlayClick
                    )
                }
            }
            videoArchiveMeta?.let {
                item {
                    ArchiveMetaItem(
                        archiveMeta = it,
                        currentArchiveIndex = currentArchiveIndex,
                        onClick = {
                            screenActionClick.invoke(ScreenAction.ShowArchiveSheetAction(true))
                        }
                    )
                }
            }
            item {
                CommentCard(
                    comments = videoDetail.view.stat.reply.toViewString(),
                    onClick = {
                        screenActionClick.invoke(ScreenAction.ShowReplyAction)
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
            items(videoDetail.related) { video ->
                VideoItem(
                    isSingleLayout = true,
                    key = video.bvid,
                    cover = video.pic,
                    title = video.title,
                    face = video.owner.face,
                    ownerName = video.owner.name,
                    view = video.stat.view.toViewString(),
                    date = video.pubdate.toTimeAgoString(),
                    duration = video.duration.formatTimeString(false),
                    onClick = {
                        val newParams = Route.Play(
                            width = video.dimension.width,
                            height = video.dimension.height,
                            aid = video.aid,
                            bvid = video.bvid,
                            cid = video.cid,
                        )
                        screenActionClick.invoke(ScreenAction.SwitchVideoAction(newParams))
                    }
                )
            }
        }
    }
}

@Composable
private fun BangumiContent(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    bangumiDetailModel: BangumiDetailModel,
    currentEpId: Long,
    initialSeasonIndex: Int,
    initialEpisodeIndex: Int,
    hasLike: Boolean,
    hasCoin: Boolean,
    hasFavoured: Boolean,
    isDownloaded: Boolean,
    isShowLikeAnimation: Boolean,
    isFullscreen: Boolean,
    relatedBangumis: List<RelatedBangumiItem>,
    bottomPadding: Dp = 0.dp,
    videoPlayActionClick: (VideoAction.VideoPlayAction) -> Unit,
    screenActionClick: (ScreenAction) -> Unit,
    videoMenuClick: (VideoAction.VideoMenuAction) -> Unit,
) {
    val seasonState = rememberLazyListState(initialFirstVisibleItemIndex = initialSeasonIndex)
    val episodeState = rememberLazyListState(initialFirstVisibleItemIndex = initialEpisodeIndex)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        val itemModifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
        LazyColumn(
            state = lazyListState,
        ) {
            item {
                Row(
                    modifier = itemModifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = bangumiDetailModel.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(
                        onClick = {},
                        color = Pink,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FavoriteBorder,
                                contentDescription = Icons.Outlined.FavoriteBorder.name,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(text = "追番", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            item {
                Row(
                    modifier = itemModifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconText(
                            leftIcon = Icons.Outlined.PlayCircleOutline,
                            text = bangumiDetailModel.stat.views.toViewString(),
                            leftIconSize = 12.dp,
                            leftIconColor = Color.LightGray,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.LightGray
                            )
                        )
                        IconText(
                            leftIcon = Icons.Outlined.FavoriteBorder,
                            text = bangumiDetailModel.stat.favorites.toViewString(),
                            leftIconSize = 12.dp,
                            leftIconColor = Color.LightGray,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.LightGray
                            )
                        )
                        IconText(
                            text = buildString {
                                bangumiDetailModel.rating?.let {
                                    append(it.score.toString())
                                    append(stringResource(R.string.str_score))
                                }?:run {
                                    append(stringResource(R.string.str_no_score))
                                }
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Red
                            )
                        )

                    }
                    IconText(
                        rightIcon = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        text = "详情",
                        rightIconSize = 14.dp,
                        rightIconColor = Color.LightGray,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.LightGray
                        )
                    )
                }
            }
            item {
                Spacer(Modifier.height(32.dp))
                VideoMenus(
                    great = bangumiDetailModel.stat.likes.toViewString(),
                    coin = bangumiDetailModel.stat.coins.toViewString(),
                    star = bangumiDetailModel.stat.favorites.toViewString(),
                    share = bangumiDetailModel.stat.share.toViewString(),
                    hasLike = hasLike,
                    hasCoin = hasCoin,
                    hasFavoured = hasFavoured,
                    isDownloaded = isDownloaded,
                    isShowLikeAnimation = isShowLikeAnimation,
                    isFullscreen = isFullscreen,
                    onClick = { videoMenuClick.invoke(it) },
                    coinClick = { screenActionClick.invoke(ScreenAction.ShowCoinSheetAction(true)) },
                    favouredClick = {
                        screenActionClick.invoke(ScreenAction.ShowFolderSheetAction(true))
                    },
                    downloadClick = {
                        screenActionClick.invoke(ScreenAction.ShowDownloadSheetAction(true))
                    },
                    onAnimationEndCallback = {
                        screenActionClick.invoke(ScreenAction.ShowLikeAnimationAction(false))
                    }
                )
                Spacer(Modifier.height(16.dp))
            }
            item {
                Row(
                    modifier = itemModifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.str_select_episode),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    IconText(
                        text = stringResource(
                            R.string.str_all_num_episode,
                            bangumiDetailModel.episodes.size
                        ),
                        rightIcon = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        rightIconSize = 14.dp,
                        rightIconColor = Color.LightGray,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.LightGray
                        )
                    )
                }
                LazyRow(
                    modifier = itemModifier.padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    state = seasonState
                ) {
                    items(bangumiDetailModel.seasons) {
                        Surface(
                            onClick = {
                                videoPlayActionClick.invoke(
                                    VideoAction.VideoPlayAction.SwitchSeasonAction(it.seasonId)
                                )
                            },
                            contentColor = when {
                                bangumiDetailModel.seasonId == it.seasonId -> Pink
                                else -> MaterialTheme.colorScheme.onBackground
                            },
                            shape = CircleShape
                        ) {
                            Text(
                                text = it.seasonTitle,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
                LazyRow(
                    modifier = Modifier.padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    state = episodeState
                ) {
                    item { Spacer(Modifier) }
                    items(bangumiDetailModel.episodes) {
                        Surface(
                            onClick = {
                                videoPlayActionClick.invoke(
                                    VideoAction.VideoPlayAction.SwitchEpisodeAction(
                                        episodeId = it.epId,
                                        aid = it.aid,
                                        cid = it.cid,
                                        bvid = it.bvid
                                    )
                                )
                            },
                            modifier = Modifier
                                .width(150.dp)
                                .height(60.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = when {
                                currentEpId == it.epId -> Pink
                                else -> MaterialTheme.colorScheme.onBackground
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (currentEpId == it.epId) {
                                        LottieIconPlaying(modifier = Modifier.size(16.dp))
                                    }
                                    val title = it.title.toDoubleOrNull()
                                    Text(
                                        text = when {
                                            title != null && it.title.contains(".") -> {
                                                stringResource(
                                                    R.string.str_episode_string,
                                                    it.title
                                                )
                                            }

                                            title != null -> {
                                                stringResource(R.string.str_episode, title.toInt())
                                            }

                                            else -> it.title
                                        },
                                        modifier = Modifier.wrapContentSize(),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = it.longTitle,
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .basicMarquee(),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    item { Spacer(Modifier) }
                }
            }
            item {
                CommentCard(
                    comments = "",
                    onClick = {
                        screenActionClick.invoke(ScreenAction.ShowReplyAction)
                    }
                )
                Spacer(Modifier.height(8.dp))
            }
            items(relatedBangumis, key = { it.seasonId }) {
                HorizontalVideoItem(
                    cover = it.cover,
                    title = it.title,
                    ownerName = "",
                    rcmdReason = it.rcmdReason,
                    view = it.stat.view.toViewString(),
                    publishDate = it.stat.follow.toViewString() + "追番",
                    leadingIcon = null,
                    onClick = {
                        screenActionClick.invoke(
                            ScreenAction.SwitchVideoAction(
                                Route.Play(
                                    seasonId = it.seasonId,
                                    isVideo = false
                                )
                            )
                        )
                    }
                )
            }
            item { Spacer(Modifier.height(bottomPadding)) }
        }
    }
}


@Preview
@Composable
private fun BangumiContentPreview() {
    BangumiContent(
        lazyListState = rememberLazyListState(),
        bangumiDetailModel = BangumiDetailModel(
            actors = """
                卫宫切嗣：小山力也\nSaber：川澄绫子\n爱丽丝菲尔：大原沙耶香\n久宇舞弥：恒松步\n言峰绮礼：中田让治\nAssassin：阿部彬名\n远坂时臣：速水奖\nArcher：关智一\n肯尼斯：山崎功\n索菈乌：..
            """.trimIndent(),
            cover = "",
            evaluate = """
                作为「Fate/stay night」的前传，「Fate/Zero」的故事舞台设定在第五次圣杯战争的10年前，即第四次圣杯战争；而在「Fate/stay night」中充满神秘感的卫宫切嗣则会成为「Fate/Zero」的主角。\n在虚渊玄优秀的创作能力之下，「Fate/Zero」无论在剧情还是在生动流畅的战斗场面等方面都显得非常优秀。\n本片于2011年7月开播，分两季播出。动画制作将继续沿用自「空之境界」系列剧场版便开始合作的ufotable，监督则由在AIC及ufotable两间制作公司中 均有活跃表现的あおきえい担任。另外，在武内崇的人物原案之下，人设将交由须藤友德及碇谷敦负责。
            """.trimIndent(),
            mediaId = -1,
            mode = 2,
            record = "",
            seasonId = 1,
            seasonTitle = "Fate/Zero 第一季",
            staff = """
                原作：虚渊玄/TYPE-MOON\n监督：あおきえい\n副监督：恒松圭\n脚本：桧山彬、实弥岛巧、吉田晃浩、佐藤和治、ufotable\n分镜：高桥タクロヲ、恒松圭、栖原隆史、宇田明彦、三浦贵博、あおきえい、须藤友德、近藤光\n演出：恒松圭、栖原隆史、宇田明彦、三浦贵博、あおきえい、须藤友德\n角色原案：武内崇\n角色设计：须藤友德、碇谷敦\n色彩设计：千叶绘美\n美术监督：卫藤功二\n摄影监督：寺尾优一\n3D监督：宍户幸次郎\n音乐：梶浦由记\n音响监督：岩浪美和\n剪辑：神野学\n动画制作：ufotable
            """.trimIndent(),
            stat = BangumiStat(),
            styles = emptyList(),
            subtitle = "",
            title = "Fate/Zero 第一季",
            total = 13,
            areas = emptyList(),
            episodes = emptyList(),
            publish = PublishModel(
                isFinish = 1,
                isStarted = 1,
                pubTime = "2011-10-01 00:00:00",
                pubTimeShow = "2011年10月01日00:00",
                weekday = 0
            ),
            rating = RatingModel(
                count = 27389,
                score = 9.6f
            ),
            seasons = emptyList(),
        ),
        currentEpId = 1,
        relatedBangumis = emptyList(),
        initialEpisodeIndex = 0,
        initialSeasonIndex = 0,
        videoPlayActionClick = {},
        screenActionClick = {},
        hasFavoured = false,
        hasLike = false,
        hasCoin = false,
        isFullscreen = false,
        isShowLikeAnimation = false,
        isDownloaded = false,
        videoMenuClick = {}
    )
}
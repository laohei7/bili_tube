package com.laohei.bili_tube.features.player.component

import android.content.Context
import android.view.TextureView
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.laohei.bili_sdk.model_v2.reply.ReplyItem
import com.laohei.bili_sdk.model_v2.user.UploadedVideoItem
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.features.main.component.VideoMenuSheet
import com.laohei.bili_tube.features.player.MediaPlayerUIState
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.features.player.component.archive.ArchiveSheet
import com.laohei.bili_tube.features.player.component.control.PlayerControl
import com.laohei.bili_tube.features.player.component.reply.VideoReplySheet
import com.laohei.bili_tube.features.player.component.setting.DownloadSheet
import com.laohei.bili_tube.features.player.state.media_v2.MediaUIState
import com.laohei.bili_tube.features.player.state.screen_v2.ScreenEvent
import com.laohei.bili_tube.features.player.state.screen_v2.ScreenState
import com.laohei.bili_tube.model.UserProfile
import com.laohei.bili_tube.model.play.MediaPlayConfig
import com.laohei.bili_tube.model.toUserProfile
import com.laohei.bili_tube.ui.component.animation.lottie.AnimatedPlayingIcon
import com.laohei.bili_tube.ui.theme.PaddingLg
import com.laohei.bili_tube.ui.util.SystemUtil
import com.laohei.bili_tube.ui.util.isOrientationPortrait
import com.laohei.bili_tube.util.toTimeString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
internal fun PortraitVideoPage(
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context,
    exoPlayer: ExoPlayer,
    mediaUIState: MediaUIState,
    nestedScrollConnection: NestedScrollConnection,
    playParam: MediaPlayConfig,
    screenState: ScreenState,
    playerState: MediaPlayerUIState,
    replies: LazyPagingItems<ReplyItem>,
    userVideos: LazyPagingItems<UploadedVideoItem>,
    onPostHistory: ((Long) -> Unit)? = null,
    onBackPress: () -> Unit,
    onProgressUpdate: (Float) -> Unit,
    onPlayChange: (Boolean) -> Unit,
    onFullscreenChange: (Boolean) -> Unit,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
    handleScreenEvent: (ScreenEvent) -> Unit,
    restartHideTimer: () -> Unit,
    onMaskAlphaChange: (Float) -> Unit,
    onSelectedAidChange: (Long) -> Unit,
    onSelectedBvidChange: (String) -> Unit,
    onDoubleSpeedChange: (Boolean) -> Unit
) {
    val statusBarHeight by rememberUpdatedState(SystemUtil.getStatusBarHeightDp())
    val isFullScreenActive by rememberUpdatedState(screenState.isFullScreenActive)
    val cover by rememberUpdatedState(
        playerState.videoDetail?.view?.pic
            ?: playerState.bangumiDetail?.episodes?.find { it.epId == playerState.currentEpId }
    )
    val animatedVideoHeight by animateDpAsState(
        targetValue = screenState.currentVideoHeight
    )
    val videoContainerColor by animateColorAsState(
        targetValue = if (isFullScreenActive) Color.Transparent else Color.Black
    )

    val animatedContentOffset by animateDpAsState(
        targetValue = screenState.currentVideoHeight + when {
            isFullScreenActive -> 0.dp
            else -> statusBarHeight
        }
    )
    val contentModifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
    val otherSheetModifier = contentModifier
        .offset { with(density) { IntOffset(0, animatedContentOffset.toPx().toInt()) } }
    val videoContentModifier = otherSheetModifier
        .nestedScroll(connection = nestedScrollConnection)
        .draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { },
        )

    val aspectRatio = mediaUIState.videoAspect

    val videoModifier = Modifier
        .height(animatedVideoHeight)
        .width(animatedVideoHeight * aspectRatio)

    val videoControlModifier = Modifier
        .then(
            if (isFullScreenActive) Modifier else Modifier.statusBarsPadding()
        )
        .fillMaxWidth()
        .height(IntrinsicSize.Min)

    val sheetBottomPadding = screenState.currentVideoHeight + 80.dp

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

    LaunchedEffect(mediaUIState.isPlaying) {
        while (mediaUIState.isPlaying) {
            val history = mediaUIState.currentPosition / 1000
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
            bitmap = screenState.backgroundImage,
            isDrag = screenState.isDragging,
            isFullscreen = screenState.isFullScreenActive,
        )

        PlayerControl(
            modifier = videoControlModifier.zIndex(99f),
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
            restartHideTimer = restartHideTimer
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
//                    .padding(top = topPadding)
                    .background(color = videoContainerColor),
                contentAlignment = Alignment.Center
            ) {
                if (mediaUIState.isCoverVisible && cover != null) {
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

        }

        GetContent(
            modifier = videoContentModifier,
            playerState = playerState,
            screenState = screenState,
            handleScreenEvent = {
                if (it is ScreenEvent.FolderModificationVisibility) {
                    onSelectedAidChange(playParam.aid)
                    onVideoMenuAction(VideoMenuAction.LoadSimpleFolders)
                }
                handleScreenEvent(it)
            },
            onVideoMenuAction = onVideoMenuAction,
            onSelectedAidChange = onSelectedAidChange,
            onSelectedBvidChange = onSelectedBvidChange
        )


        // Sheet Shadow Gradient Layer
        Box(
            modifier = contentModifier
                .graphicsLayer { alpha = screenState.maskOpacity }
                .background(Color.Black)
        )

        VideoReplySheet(
            isShowReplyUI = screenState.showReplyUI,
            shouldHideSystemBar = !isOrientationPortrait() && screenState.isFullScreenActive,
            replies = replies,
            onDismiss = { handleScreenEvent(ScreenEvent.ReplyVisibility(false)) },
            modifier = otherSheetModifier,
            onMaskAlphaChange = { onMaskAlphaChange(it) },
            bottomPadding = sheetBottomPadding
        )

        VideoDetailSheet(
            videoDetail = playerState.videoDetail,
            isShowVideoDetailUI = screenState.showVideoDetailUI,
            onDismiss = { handleScreenEvent(ScreenEvent.VideoDetailVisibility(false)) },
            modifier = otherSheetModifier,
            onMaskAlphaChange = { onMaskAlphaChange(it) },
            bottomPadding = screenState.currentVideoHeight + 80.dp
        )

        if (playerState.videoArchiveMeta != null && !screenState.isFullScreenActive) {
            val archive = playerState.videoArchiveMeta

            val currentArchiveIndex by rememberUpdatedState(playerState.currentArchiveIndex)

            val nextArchiveItem by rememberUpdatedState(
                playerState.videoArchives?.getOrNull(currentArchiveIndex + 1)
            )
            GroupInfoBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = PaddingLg * 2)
                    .padding(horizontal = PaddingLg)
                    .fillMaxWidth(),
                title = nextArchiveItem?.let {
                    stringResource(R.string.str_next_archive_item_template, it.title)
                } ?: stringResource(R.string.str_last_archive_item),
                subtitle = stringResource(
                    R.string.str_archive_item_template,
                    archive.name, currentArchiveIndex + 1, archive.total
                ),
                subcontent = {
                    AnimatedPlayingIcon(Modifier.size(12.dp))
                },
                onClick = {
                    handleScreenEvent(ScreenEvent.ArchiveVisibility(true))
                }
            )
            ArchiveSheet(
                lazyListState = screenState.archiveListScrollState,
                modifier = otherSheetModifier,
                currentArchiveIndex = playerState.currentArchiveIndex,
                archiveMeta = playerState.videoArchiveMeta,
                archives = playerState.videoArchives,
                isShowArchiveUI = screenState.showArchiveUI,
                onMaskAlphaChange = { onMaskAlphaChange(it) },
                onDismiss = { handleScreenEvent(ScreenEvent.ArchiveVisibility(false)) },
                onVideoMenuAction = onVideoMenuAction,
                bottomPadding = screenState.currentVideoHeight + 80.dp
            )
        }

        if (playerState.mediaPlayConfig is MediaPlayConfig.MediaFolderConfig && playerState.mediaPlayConfig.isToView
            && !screenState.isFullScreenActive
        ) {
            val playParam = playerState.mediaPlayConfig
            val currentIndex by remember(playParam.bvid) {
                derivedStateOf {
                    playParam.medias.indexOfFirst { it.bvid == playParam.bvid }
                }
            }
            val nextItem by rememberUpdatedState(playerState.watchLaterList.getOrNull(currentIndex + 1))
            GroupInfoBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = PaddingLg * 2)
                    .padding(horizontal = PaddingLg)
                    .fillMaxWidth(),
                title = nextItem?.let {
                    stringResource(R.string.str_next_archive_item_template, it.title)
                } ?: stringResource(R.string.str_last_archive_item),
                subtitle = stringResource(
                    R.string.str_archive_item_template,
                    playParam.title, currentIndex + 1, playParam.count
                ),
                subcontent = {
                    AnimatedPlayingIcon(Modifier.size(12.dp))
                },
                onClick = {
                    handleScreenEvent(ScreenEvent.WatchLaterVisibility(true))
                }
            )
            WatchLaterSheet(
                lazyListState = screenState.watchLaterListState,
                playParam = playerState.mediaPlayConfig,
                modifier = otherSheetModifier,
                isWatchLaterVisible = screenState.isWatchLaterVisible,
                watchLaterList = playerState.watchLaterList,
                currentWatchLaterIndex = currentIndex,
                onMaskAlphaChange = { onMaskAlphaChange(it) },
                onDismiss = { handleScreenEvent(ScreenEvent.WatchLaterVisibility(false)) },
                onVideoMenuAction = onVideoMenuAction,
                bottomPadding = screenState.currentVideoHeight + 80.dp
            )
        }

        if (playerState.mediaPlayConfig is MediaPlayConfig.MediaFolderConfig && !playerState.mediaPlayConfig.isToView
            && !screenState.isFullScreenActive
        ) {
            val playParam = playerState.mediaPlayConfig
            val folderMediaList = playerState.folderMediaFlow.collectAsLazyPagingItems()
            var lastValidIndex by remember { mutableIntStateOf(-1) }
            val currentIndex by remember(playParam.bvid, folderMediaList.itemCount) {
                derivedStateOf {
                    val idx = folderMediaList.itemSnapshotList
                        .indexOfFirst { it?.bvid == playParam.bvid }
                    if (idx != -1) {
                        lastValidIndex = idx
                    }
                    lastValidIndex
                }
            }
            val nextItem by rememberUpdatedState(
                folderMediaList.itemSnapshotList.getOrNull(currentIndex + 1)
            )
            GroupInfoBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = PaddingLg * 2)
                    .padding(horizontal = PaddingLg)
                    .fillMaxWidth(),
                title = nextItem?.let {
                    stringResource(R.string.str_next_archive_item_template, it.title)
                } ?: stringResource(R.string.str_last_archive_item),
                subtitle = stringResource(
                    R.string.str_archive_item_template,
                    playParam.title, currentIndex + 1, playParam.count
                ),
                subcontent = {
                    AnimatedPlayingIcon(Modifier.size(12.dp))
                },
                onClick = {
                    handleScreenEvent(ScreenEvent.FolderMediaVisibility(true))
                }
            )
            FolderMediaSheet(
                lazyListState = screenState.folderMediaListState,
                playParam = playerState.mediaPlayConfig,
                modifier = otherSheetModifier,
                isFolderMediaVisible = screenState.isFolderMediaVisible,
                folderMediaList = folderMediaList,
                currentFolderMediaIndex = currentIndex,
                onMaskAlphaChange = { onMaskAlphaChange(it) },
                onDismiss = { handleScreenEvent(ScreenEvent.FolderMediaVisibility(false)) },
                onVideoMenuAction = onVideoMenuAction,
                bottomPadding = screenState.currentVideoHeight + 80.dp
            )
        }

        AddCoinSheet(
            isShowAddCoinUI = screenState.showAddCoinUI,
            onDismiss = { handleScreenEvent(ScreenEvent.AddCoinVisibility(false)) },
            onVideoMenuAction = {
                handleScreenEvent(ScreenEvent.AddCoinVisibility(false))
                onVideoMenuAction(it)
            }
        )

        UserInfoCardSheet(
            isShowSheet = screenState.showUpInfoSheet,
            isLoading = playerState.infoCardModel == null,
            userProfile = playerState.infoCardModel?.toUserProfile() ?: UserProfile.Empty,
            uploadedVideos = userVideos,
            currentBvid = playParam.bvid,
            onDismiss = { handleScreenEvent(ScreenEvent.UpInfoVisibility(false)) },
            modifier = otherSheetModifier,
            onMaskAlphaChange = { onMaskAlphaChange(it) },
            onVideoMenuAction = onVideoMenuAction,
            bottomPadding = screenState.currentVideoHeight + 80.dp
        )

        DownloadSheet(
            isShowSheet = screenState.showDownloadSheet,
            quality = mediaUIState.supportQualities.map { it.id to it.label },
            defaultQuality = mediaUIState.activeQuality.id to mediaUIState.activeQuality.label,
            onDismiss = { handleScreenEvent(ScreenEvent.DownloadVisibility(false)) },
            onDownloadClick = {
                scope.launch {
                    EventBus.send(Event.AppEvent.ToastEvent(R.string.str_download_develop_hint))
                }
            }
        )

        VideoMenuSheet(
            isShowSheet = screenState.showVideoMenuUIAction,
            onDismiss = { handleScreenEvent(ScreenEvent.VideoMenuVisibility(false)) },
        ) {
            when (it) {
                R.string.str_save_playlist -> {
                    handleScreenEvent(ScreenEvent.VideoMenuVisibility(false))
                    onVideoMenuAction(VideoMenuAction.LoadSimpleFolders)
                    handleScreenEvent(ScreenEvent.FolderModificationVisibility(true))
                }

                R.string.str_save_watch_later -> {
                    onVideoMenuAction(VideoMenuAction.AddToView)
                }

                else -> {}
            }
        }
    }
}
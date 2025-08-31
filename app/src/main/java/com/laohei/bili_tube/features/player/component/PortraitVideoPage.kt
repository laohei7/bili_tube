package com.laohei.bili_tube.features.player.component

import android.content.Context
import android.util.Log
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
import coil3.Bitmap
import coil3.compose.AsyncImage
import com.laohei.bili_sdk.module_v2.reply.ReplyItem
import com.laohei.bili_sdk.module_v2.user.UploadedVideoItem
import com.laohei.bili_tube.model.play.PlayParam
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
import com.laohei.bili_tube.features.player.state.media.MediaState
import com.laohei.bili_tube.features.player.state.screen.ScreenAction
import com.laohei.bili_tube.features.player.state.screen.ScreenState
import com.laohei.bili_tube.model.UserProfile
import com.laohei.bili_tube.model.toUserProfile
import com.laohei.bili_tube.ui.component.animation.lottie.AnimatedPlayingIcon
import com.laohei.bili_tube.ui.theme.PaddingLg
import com.laohei.bili_tube.ui.util.SystemUtil
import com.laohei.bili_tube.util.toTimeString
import com.laohei.bili_tube.ui.util.isOrientationPortrait
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
internal fun PortraitVideoPage(
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
    onDoubleSpeedChange: (Boolean) -> Unit
) {
    val statusBarHeight by rememberUpdatedState(SystemUtil.getStatusBarHeightDp())
    val isFullscreen by rememberUpdatedState(screenState.isFullscreen)
    val cover by rememberUpdatedState(
        playerState.videoDetail?.view?.pic
            ?: playerState.bangumiDetail?.episodes?.find { it.epId == playerState.currentEpId }
    )
    val animatedVideoHeight by animateDpAsState(
        targetValue = screenState.videoHeight
    )
    val videoContainerColor by animateColorAsState(
        targetValue = if (isFullscreen) Color.Transparent else Color.Black
    )
    val topPadding by animateDpAsState(
        targetValue = if (isFullscreen) 0.dp else statusBarHeight
    )
    val animatedContentOffset by animateDpAsState(
        targetValue = screenState.videoHeight + when {
            screenState.isFullscreen -> 0.dp
            else -> statusBarHeight
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
            totalDuration = mediaState.totalDuration.toTimeString(),
            currentDuration = mediaState.currentDuration.toTimeString(),
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = topPadding)
                    .background(color = videoContainerColor),
                contentAlignment = Alignment.Center
            ) {
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

        if (playerState.videoArchiveMeta != null && !screenState.isFullscreen) {
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
                    onScreenAction(ScreenAction.SetArchiveVisible(true))
                }
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
        }

        if (playerState.playParam is PlayParam.MediaList && playerState.playParam.isToView
            && !screenState.isFullscreen
        ) {
            val playParam = playerState.playParam
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
                    onScreenAction(ScreenAction.SetWatchLaterVisible(true))
                }
            )
            WatchLaterSheet(
                lazyListState = screenState.watchLaterListState,
                playParam = playerState.playParam,
                modifier = otherSheetModifier,
                isWatchLaterVisible = screenState.isWatchLaterVisible,
                watchLaterList = playerState.watchLaterList,
                currentWatchLaterIndex = currentIndex,
                onMaskAlphaChange = { onMaskAlphaChange(it) },
                onDismiss = { onScreenAction(ScreenAction.SetWatchLaterVisible(false)) },
                onVideoMenuAction = onVideoMenuAction,
                bottomPadding = screenState.videoHeight + 80.dp
            )
        }

        if (playerState.playParam is PlayParam.MediaList && !playerState.playParam.isToView
            && !screenState.isFullscreen
        ) {
            val playParam = playerState.playParam
            val folderMediaList = playerState.folderMediaFlow.collectAsLazyPagingItems()
            var lastValidIndex by remember { mutableIntStateOf(-1) }
            val currentIndex by remember(playParam.bvid,folderMediaList.itemCount) {
                derivedStateOf {
                    val idx = folderMediaList.itemSnapshotList
                        .indexOfFirst { it?.bvid == playParam.bvid }
                    if (idx != -1) {
                        lastValidIndex = idx
                    }
                    lastValidIndex
                }
            }
            Log.d("TAG", "PortraitVideoPage: $currentIndex")
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
                    onScreenAction(ScreenAction.SetFolderMediaVisible(true))
                }
            )
            FolderMediaSheet(
                lazyListState = screenState.folderMediaListState,
                playParam = playerState.playParam,
                modifier = otherSheetModifier,
                isFolderMediaVisible = screenState.isFolderMediaVisible,
                folderMediaList = folderMediaList,
                currentFolderMediaIndex = currentIndex,
                onMaskAlphaChange = { onMaskAlphaChange(it) },
                onDismiss = { onScreenAction(ScreenAction.SetFolderMediaVisible(false)) },
                onVideoMenuAction = onVideoMenuAction,
                bottomPadding = screenState.videoHeight + 80.dp
            )
        }

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
            userProfile = playerState.infoCardModel?.toUserProfile() ?: UserProfile.Empty,
            uploadedVideos = userVideos,
            currentBvid = playParam.bvid,
            onDismiss = {
                onScreenAction(ScreenAction.SetUpInfoVisible(false))
            },
            modifier = otherSheetModifier,
            onMaskAlphaChange = { onMaskAlphaChange(it) },
            onVideoMenuAction = onVideoMenuAction,
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
//                    if (!context.checkedPermissions(WRITE_STORAGE_PERMISSION)) {
//                        EventBus.send(Event.AppEvent.PermissionRequestEvent(WRITE_STORAGE_PERMISSION))
//                    } else {
//                        onDownload(it)
//                    }
                    EventBus.send(Event.AppEvent.ToastEvent(R.string.str_download_develop_hint))
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
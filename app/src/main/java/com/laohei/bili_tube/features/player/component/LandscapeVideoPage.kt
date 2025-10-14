package com.laohei.bili_tube.features.player.component

import android.content.Context
import android.view.TextureView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.laohei.bili_sdk.model_v2.reply.ReplyItem
import com.laohei.bili_sdk.model_v2.user.InfoCardModel
import com.laohei.bili_sdk.model_v2.user.UploadedVideoItem
import com.laohei.bili_sdk.model_v2.video.BangumiDetailModel
import com.laohei.bili_sdk.model_v2.video.VideoDetailModel
import com.laohei.bili_tube.R
import com.laohei.bili_tube.features.player.MediaPlayerUIState
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.features.player.component.control.PlayerControl
import com.laohei.bili_tube.features.player.state.media_v2.MediaUIState
import com.laohei.bili_tube.features.player.state.screen_v2.ScreenEvent
import com.laohei.bili_tube.features.player.state.screen_v2.ScreenState
import com.laohei.bili_tube.model.play.MediaPlayConfig
import com.laohei.bili_tube.model.toUserProfile
import com.laohei.bili_tube.ui.component.chip.TagChip
import com.laohei.bili_tube.ui.component.widget.ChipTabRow
import com.laohei.bili_tube.ui.theme.PaddingLg
import com.laohei.bili_tube.ui.theme.PaddingMd
import com.laohei.bili_tube.ui.theme.PaddingNone
import com.laohei.bili_tube.ui.theme.PaddingSm
import com.laohei.bili_tube.util.toTimeString
import com.laohei.bili_tube.util.toViewString
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
internal fun LandscapeVideoPage(
    exoPlayer: ExoPlayer,
    playerUIState: MediaPlayerUIState,
    mediaUIState: MediaUIState,
    screenState: ScreenState,
    replies: LazyPagingItems<ReplyItem>,
    works: LazyPagingItems<UploadedVideoItem>,
    handleScreenEvent: (ScreenEvent) -> Unit,
    onBackPress: () -> Unit,
    onProgressUpdate: (Float) -> Unit,
    onPlayChange: (Boolean) -> Unit,
    onFullscreenChange: (Boolean) -> Unit,
    onDoubleSpeedChange: (Boolean) -> Unit,
    resetHideTimer: () -> Unit,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
            .statusBarsPadding()
            .padding(horizontal = PaddingSm),
        horizontalArrangement = Arrangement.spacedBy(PaddingSm)
    ) {
        LandscapeMainArea(
            exoPlayer = exoPlayer,
            playerUIState = playerUIState,
            mediaUIState = mediaUIState,
            screenState = screenState,
            onBackPress = onBackPress,
            onProgressChange = onProgressUpdate,
            onPlayChange = onPlayChange,
            onFullscreenChange = onFullscreenChange,
            handleScreenEvent = handleScreenEvent,
            onDoubleSpeedChange = onDoubleSpeedChange,
            resetHideTimer = resetHideTimer,
            onVideoMenuAction = onVideoMenuAction
        )
        OtherListArea(
            playerUIState = playerUIState,
            screenState = screenState,
            replies = replies,
            works = works,
            onVideoMenuAction = onVideoMenuAction
        )
    }
}

@Composable
private fun RowScope.LandscapeMainArea(
    exoPlayer: ExoPlayer,
    playerUIState: MediaPlayerUIState,
    mediaUIState: MediaUIState,
    screenState: ScreenState,
    handleScreenEvent: (ScreenEvent) -> Unit,
    onBackPress: () -> Unit,
    onProgressChange: (Float) -> Unit,
    onPlayChange: (Boolean) -> Unit,
    onFullscreenChange: (Boolean) -> Unit,
    onDoubleSpeedChange: (Boolean) -> Unit,
    resetHideTimer: () -> Unit,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .weight(2f)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(PaddingSm)
    ) {
        LandscapeVideoArea(
            exoPlayer = exoPlayer,
            playerUIState = playerUIState,
            mediaUIState = mediaUIState,
            screenState = screenState,
            onBackPress = onBackPress,
            onProgressChange = onProgressChange,
            onPlayChange = onPlayChange,
            onFullscreenChange = onFullscreenChange,
            onDoubleSpeedChange = onDoubleSpeedChange,
            handleScreenEvent = handleScreenEvent,
            resetHideTimer = resetHideTimer
        )
        LandscapeInfoArea(
            videoDetail = playerUIState.videoDetail,
            bangumiDetail = playerUIState.bangumiDetail,
            infoCard = playerUIState.infoCardModel,
            hasFavoured = playerUIState.hasFavoured,
            hasCoin = playerUIState.hasCoin,
            hasLike = playerUIState.hasLike,
            isDownloaded = playerUIState.isDownloaded,
            isShowLikeAnimation = screenState.showLikeAnimation,
            isFullscreen = screenState.isFullScreenActive,
            handleScreenEvent = handleScreenEvent,
            onVideoMenuAction = onVideoMenuAction
        )
    }
}

@Composable
private fun ColumnScope.LandscapeVideoArea(
    context: Context = LocalContext.current,
    exoPlayer: ExoPlayer,
    playerUIState: MediaPlayerUIState,
    mediaUIState: MediaUIState,
    screenState: ScreenState,
    onBackPress: () -> Unit,
    onProgressChange: (Float) -> Unit,
    onPlayChange: (Boolean) -> Unit,
    onFullscreenChange: (Boolean) -> Unit,
    handleScreenEvent: (ScreenEvent) -> Unit,
    onDoubleSpeedChange: (Boolean) -> Unit,
    resetHideTimer: () -> Unit,
) {
    val cover by rememberUpdatedState(
        playerUIState.videoDetail?.view?.pic
            ?: playerUIState.bangumiDetail?.episodes?.find { it.epId == playerUIState.currentEpId }
    )
    val aspectRatio by rememberUpdatedState(mediaUIState.videoAspect)

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
        Modifier
            .fillMaxWidth()
            .weight(2f)
            .clip(RoundedCornerShape(PaddingLg))
            .background(Color.Black)
    ) {
        BlurBackground(
            bitmap = screenState.backgroundImage,
            isDrag = false,
            isFullscreen = false,
        )

        PlayerControl(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(99f),
            title = playerUIState.title,
            progress = mediaUIState.progress,
            bufferProgress = mediaUIState.bufferProgress,
            isShowUI = screenState.showControlUI,
            isShowRelatedList = screenState.showRelatedList,
            isFullscreen = screenState.isFullScreenActive,
            isLockScreen = screenState.isScreenLocked,
            isPlaying = mediaUIState.isPlaying,
            isLoading = mediaUIState.isLoading,
            totalDuration = mediaUIState.duration.toTimeString(),
            currentDuration = mediaUIState.currentPosition.toTimeString(),
            onFullscreenToggle = onFullscreenChange,
            onPlaybackStateChanged = onPlayChange,
            onProgressUpdate = onProgressChange,
            onLongPressStart = { onDoubleSpeedChange(true) },
            onLongPressEnd = { onDoubleSpeedChange(false) },
            onControlUIVisibilityChange = { handleScreenEvent(ScreenEvent.ControlVisibility(it)) },
            onOpenSettings = { handleScreenEvent(ScreenEvent.SettingsVisibility(true)) },
            onBackPressed = onBackPress,
            hintContent = { SpeedHint(speed = mediaUIState.activeSpeed) },
            unlockScreen = {},
            restartHideTimer = resetHideTimer
        ) {
            if (mediaUIState.isCoverVisible && cover != null) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize(),
                    model = cover,
                    contentDescription = "cover",
                    contentScale = ContentScale.Crop
                )
            } else {
                AndroidView(
                    modifier = Modifier
                        .fillMaxHeight()
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
}


@Composable
private fun ColumnScope.LandscapeInfoArea(
    videoDetail: VideoDetailModel?,
    bangumiDetail: BangumiDetailModel?,
    infoCard: InfoCardModel?,
    hasLike: Boolean,
    hasCoin: Boolean,
    hasFavoured: Boolean,
    isDownloaded: Boolean,
    isShowLikeAnimation: Boolean,
    isFullscreen: Boolean,
    handleScreenEvent: (ScreenEvent) -> Unit,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
) {
    val title by rememberUpdatedState(videoDetail?.view?.title ?: bangumiDetail?.title ?: "")
    Column(
        Modifier
            .fillMaxWidth()
            .weight(1f)
            .clip(RoundedCornerShape(PaddingLg))
            .background(MaterialTheme.colorScheme.background)
            .padding(PaddingSm),
        verticalArrangement = Arrangement.spacedBy(PaddingSm)
    ) {
        bangumiDetail?.let { detail ->
            BangumiTitleWidget(
                title = title
            )
        } ?: run {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }

        videoDetail?.let { detail ->
            UserSubscriptionBar(
                face = detail.view.owner.face,
                name = detail.view.owner.name,
                fans = detail.card.card.fans.toViewString(),
                isSubscribed = infoCard?.following == true,
                onClick = {},
                onVideoMenuAction = { }
            )
        }

        bangumiDetail?.let { detail ->
            BangumiInfoBar(
                views = detail.stat.views,
                favorites = detail.stat.favorites,
                score = detail.rating?.score
            )
        }

        videoDetail?.let { detail ->
            VideoActionBar(
                great = videoDetail.view.stat.like.toViewString(),
                coin = videoDetail.view.stat.coin.toViewString(),
                star = videoDetail.view.stat.favorite.toViewString(),
                share = videoDetail.view.stat.share.toViewString(),
                hasLike = hasLike,
                hasCoin = hasCoin,
                hasFavoured = hasFavoured,
                isDownloaded = isDownloaded,
                showLikeAnimation = isShowLikeAnimation,
                isFullscreen = isFullscreen,
                onAnimationEndCallback = {
                    handleScreenEvent(ScreenEvent.LikeAnimationVisibility(false))
                },
                onLikeClick = {},
                onDislikeClick = {},
                onCoinClick = {},
                onStarClick = {},
                onShareClick = {},
                onDownloadClick = {}
            )
        } ?: bangumiDetail?.let { detail ->
            Spacer(Modifier.height(PaddingMd))
            VideoActionBar(
                great = detail.stat.likes.toViewString(),
                coin = detail.stat.coins.toViewString(),
                star = detail.stat.favorites.toViewString(),
                share = detail.stat.share.toViewString(),
                hasLike = hasLike,
                hasCoin = hasCoin,
                hasFavoured = hasFavoured,
                isDownloaded = isDownloaded,
                showLikeAnimation = isShowLikeAnimation,
                isFullscreen = isFullscreen,
                onAnimationEndCallback = {
                    handleScreenEvent(ScreenEvent.LikeAnimationVisibility(false))
                },
                onLikeClick = {},
                onDislikeClick = {},
                onCoinClick = {},
                onStarClick = {},
                onShareClick = {},
                onDownloadClick = {}
            )
        }

        videoDetail?.let { detail ->
            val tags = videoDetail.tags.fastMap { it.tagName }
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = PaddingSm),
                verticalArrangement = Arrangement.spacedBy(PaddingMd),
                horizontalArrangement = Arrangement.spacedBy(PaddingLg),
                maxLines = 2,
            ) {
                tags.fastForEach {
                    TagChip(it)
                }
            }
        }

    }
}

@Composable
private fun RowScope.OtherListArea(
    playerUIState: MediaPlayerUIState,
    screenState: ScreenState,
    replies: LazyPagingItems<ReplyItem>,
    works: LazyPagingItems<UploadedVideoItem>,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val menus by remember(
        playerUIState.videoPageList,
        playerUIState.videoArchives,
        playerUIState.videoDetail,
        playerUIState.relatedBangumis,
        playerUIState.mediaPlayConfig is MediaPlayConfig.MediaFolderConfig
    ) {
        derivedStateOf {
            val playParam = playerUIState.mediaPlayConfig
            buildList {
                playerUIState.videoPageList?.let { add(context.getString(R.string.str_video_page_list)) }
                    ?: playerUIState.bangumiDetail?.episodes?.let {
                        add(context.getString(R.string.str_video_page_list))
                    }
                when (playParam) {
                    is MediaPlayConfig.MediaFolderConfig -> {
                        add(playParam.title)
                    }

                    else -> {}
                }
                playerUIState.videoArchives?.let { add(context.getString(R.string.str_video_archive)) }
                playerUIState.videoDetail?.related?.let { add(context.getString(R.string.str_related_video)) }
                add(context.getString(R.string.str_reply))
                if (playerUIState.isVideo) {
                    add(context.getString(R.string.str_user_works))
                }
                playerUIState.relatedBangumis?.let { add(context.getString(R.string.str_related_bangumi)) }
            }
        }
    }
    var isInMainReplyList by remember { mutableStateOf(true) }
    val pager = rememberPagerState { menus.size }
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(PaddingSm))
            .background(MaterialTheme.colorScheme.background)
    ) {
        ChipTabRow(
            tabs = menus,
            selectedTabIndex = pager.currentPage,
            onTabClick = {
                scope.launch {
                    pager.animateScrollToPage(it)
                }
            }
        )

        HorizontalPager(
            state = pager
        ) { index ->
            when (menus[index]) {
                stringResource(R.string.str_video_page_list) -> {
                    if (playerUIState.isVideo) {
                        GridVideoPageList(
                            pageList = playerUIState.videoPageList ?: emptyList(),
                            currentPageListIndex = playerUIState.currentPageListIndex,
                            onVideoMenuAction = onVideoMenuAction
                        )
                    } else {
                        playerUIState.bangumiDetail?.let { detail ->
                            BangumiSeasonAndEpisodeWidget(
                                bangumiDetail = detail,
                                currentEpId = playerUIState.currentEpId,
                                initialEpisodeIndex = playerUIState.initialEpisodeIndex,
                                initialSeasonIndex = playerUIState.initialSeasonIndex,
                                onVideoMenuAction = onVideoMenuAction
                            )
                        }
                    }
                }

                stringResource(R.string.str_user_works) -> {
                    if (playerUIState.infoCardModel == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        UserWorkList(
                            works = works,
                            userProfile = playerUIState.infoCardModel.toUserProfile(),
                            currentBvid = playerUIState.mediaPlayConfig.bvid,
                            bottomPadding = PaddingNone,
                            onVideoMenuAction = onVideoMenuAction
                        )
                    }
                }

                stringResource(R.string.str_related_video) -> {
                    RelatedVideoList(
                        relatedList = playerUIState.videoDetail?.related ?: emptyList(),
                        onVideoMenuAction = onVideoMenuAction
                    )
                }

                stringResource(R.string.str_video_archive) -> {
                    ArchiveList(
                        modifier = Modifier
                            .fillMaxWidth(),
                        listState = screenState.archiveListScrollState,
                        bottomPadding = PaddingNone,
                        currentArchiveIndex = playerUIState.currentArchiveIndex,
                        archiveList = playerUIState.videoArchives ?: emptyList(),
                        onVideoMenuAction = onVideoMenuAction
                    )
                }

                stringResource(R.string.str_reply) -> {
                    ReplyList(
                        replyItems = replies,
                        isCloseButtonVisible = false,
                        isInMainReplyList = isInMainReplyList,
                        bottomPadding = PaddingNone,
                        onMainReplyListChange = { isInMainReplyList = it },
                        onBackClick = {
                            when {
                                isInMainReplyList -> {}

                                else -> {
                                    isInMainReplyList = true
                                }
                            }
                        }
                    )
                }

                stringResource(R.string.str_related_bangumi) -> {
                    RelatedBangumiList(
                        relatedList = playerUIState.relatedBangumis ?: emptyList(),
                        onVideoMenuAction = onVideoMenuAction
                    )
                }

                stringResource(R.string.str_watch_later) -> {
                    val playParam = playerUIState.mediaPlayConfig as MediaPlayConfig.MediaFolderConfig
                    val currentIndex by remember(playParam.bvid) {
                        derivedStateOf {
                            playParam.medias.indexOfFirst { it.bvid == playParam.bvid }
                        }
                    }
                    WatchLaterList(
                        listState = screenState.watchLaterListState,
                        playParam = playParam,
                        watchLaterList = playerUIState.watchLaterList,
                        bottomPadding = PaddingNone,
                        currentWatchLaterIndex = currentIndex,
                        onVideoMenuAction = onVideoMenuAction
                    )
                }

                else -> {
                    val playParam = playerUIState.mediaPlayConfig as MediaPlayConfig.MediaFolderConfig
                    val folderMediaList = playerUIState.folderMediaFlow.collectAsLazyPagingItems()
                    val currentIndex by remember(playParam.bvid, folderMediaList.itemCount) {
                        derivedStateOf {
                            folderMediaList.itemSnapshotList.indexOfFirst { it?.bvid == playParam.bvid }
                        }
                    }
                    FolderMediaList(
                        listState = screenState.folderMediaListState,
                        playParam = playParam,
                        folderMediaList = folderMediaList,
                        bottomPadding = PaddingNone,
                        currentFolderMediaIndex = currentIndex,
                        onVideoMenuAction = onVideoMenuAction
                    )
                }
            }
        }
    }
}

@Composable
private fun BangumiSeasonAndEpisodeWidget(
    bangumiDetail: BangumiDetailModel,
    initialSeasonIndex: Int,
    initialEpisodeIndex: Int,
    currentEpId: Long,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
) {
    val seasonState = rememberLazyListState(initialFirstVisibleItemIndex = initialSeasonIndex)
    val episodeState = rememberLazyGridState(initialFirstVisibleItemIndex = initialEpisodeIndex)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = PaddingSm),
        verticalArrangement = Arrangement.spacedBy(PaddingMd)
    ) {
        BangumiEpisodeTitleBar(
            size = bangumiDetail.episodes.size
        )
        BangumiSeasonList(
            listState = seasonState,
            currentSeasonId = bangumiDetail.seasonId,
            seasonList = bangumiDetail.seasons,
            onVideoMenuAction = onVideoMenuAction
        )
        GridBangumiEpisodeList(
            modifier = Modifier.padding(vertical = 4.dp),
            listState = episodeState,
            currentEpId = currentEpId,
            episodeList = bangumiDetail.episodes,
            onVideoMenuAction = onVideoMenuAction
        )
    }
}
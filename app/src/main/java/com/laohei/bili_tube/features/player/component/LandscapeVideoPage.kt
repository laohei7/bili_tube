package com.laohei.bili_tube.features.player.component

import android.content.Context
import android.view.TextureView
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.Bitmap
import coil3.compose.AsyncImage
import com.laohei.bili_sdk.module_v2.reply.ReplyItem
import com.laohei.bili_sdk.module_v2.user.InfoCardModel
import com.laohei.bili_sdk.module_v2.user.UploadedVideoItem
import com.laohei.bili_sdk.module_v2.video.BangumiDetailModel
import com.laohei.bili_sdk.module_v2.video.VideoDetailModel
import com.laohei.bili_tube.R
import com.laohei.bili_tube.features.player.MediaPlayerUIState
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.features.player.component.control.PlayerControl
import com.laohei.bili_tube.features.player.state.media.MediaState
import com.laohei.bili_tube.features.player.state.screen.ScreenAction
import com.laohei.bili_tube.features.player.state.screen.ScreenState
import com.laohei.bili_tube.model.toUserProfile
import com.laohei.bili_tube.ui.component.ScrollTabRow
import com.laohei.bili_tube.ui.component.TagItem
import com.laohei.bili_tube.ui.preview.FakePlayerState
import com.laohei.bili_tube.ui.theme.LargePadding
import com.laohei.bili_tube.ui.theme.MediumPadding
import com.laohei.bili_tube.ui.theme.NonePadding
import com.laohei.bili_tube.ui.theme.SmallPadding
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.toViewString
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
internal fun LandscapeVideoPage(
    exoPlayer: ExoPlayer,
    playerUIState: MediaPlayerUIState,
    mediaState: MediaState,
    screenState: ScreenState,
    replies: LazyPagingItems<ReplyItem>,
    works: LazyPagingItems<UploadedVideoItem>,
    onVideoFrameChange: (Bitmap) -> Unit,
    onControlUIChange: (Boolean) -> Unit,
    onBackPress: () -> Unit,
    onProgressChange: (Float) -> Unit,
    onPlayChange: (Boolean) -> Unit,
    onFullscreenChange: (Boolean) -> Unit,
    onScreenAction: (ScreenAction) -> Unit,
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
            .padding(horizontal = SmallPadding),
        horizontalArrangement = Arrangement.spacedBy(SmallPadding)
    ) {
        LandscapeMainArea(
            exoPlayer = exoPlayer,
            playerUIState = playerUIState,
            mediaState = mediaState,
            screenState = screenState,
            onVideoFrameChange = onVideoFrameChange,
            onControlUIChange = onControlUIChange,
            onBackPress = onBackPress,
            onProgressChange = onProgressChange,
            onPlayChange = onPlayChange,
            onFullscreenChange = onFullscreenChange,
            onScreenAction = onScreenAction,
            onDoubleSpeedChange = onDoubleSpeedChange,
            resetHideTimer = resetHideTimer
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
    mediaState: MediaState,
    screenState: ScreenState,
    onVideoFrameChange: (Bitmap) -> Unit,
    onControlUIChange: (Boolean) -> Unit,
    onBackPress: () -> Unit,
    onProgressChange: (Float) -> Unit,
    onPlayChange: (Boolean) -> Unit,
    onFullscreenChange: (Boolean) -> Unit,
    onScreenAction: (ScreenAction) -> Unit,
    onDoubleSpeedChange: (Boolean) -> Unit,
    resetHideTimer: () -> Unit,
) {
    Column(
        modifier = Modifier
            .weight(2f)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(SmallPadding)
    ) {
        LandscapeVideoArea(
            exoPlayer = exoPlayer,
            playerUIState = playerUIState,
            mediaState = mediaState,
            screenState = screenState,
            onVideoFrameChange = onVideoFrameChange,
            onControlUIChange = onControlUIChange,
            onBackPress = onBackPress,
            onProgressChange = onProgressChange,
            onPlayChange = onPlayChange,
            onFullscreenChange = onFullscreenChange,
            onScreenAction = onScreenAction,
            onDoubleSpeedChange = onDoubleSpeedChange,
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
            isShowLikeAnimation = screenState.isShowLikeAnimation,
            isFullscreen = screenState.isFullscreen,
        )
    }
}

@Composable
private fun ColumnScope.LandscapeVideoArea(
    context: Context = LocalContext.current,
    exoPlayer: ExoPlayer,
    playerUIState: MediaPlayerUIState,
    mediaState: MediaState,
    screenState: ScreenState,
    onVideoFrameChange: (Bitmap) -> Unit,
    onControlUIChange: (Boolean) -> Unit,
    onBackPress: () -> Unit,
    onProgressChange: (Float) -> Unit,
    onPlayChange: (Boolean) -> Unit,
    onFullscreenChange: (Boolean) -> Unit,
    onScreenAction: (ScreenAction) -> Unit,
    onDoubleSpeedChange: (Boolean) -> Unit,
    resetHideTimer: () -> Unit,
) {
    val cover by rememberUpdatedState(
        playerUIState.videoDetail?.view?.pic
            ?: playerUIState.bangumiDetail?.episodes?.find { it.epId == playerUIState.currentEpId }
    )
    val aspectRatio by rememberUpdatedState(mediaState.width.toFloat() / mediaState.height)

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
        Modifier
            .fillMaxWidth()
            .weight(2f)
            .clip(RoundedCornerShape(LargePadding))
            .background(Color.Black)
    ) {
        BlurBackground(
            bitmap = screenState.background,
            isDrag = false,
            isFullscreen = false,
        )

        PlayerControl(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(99f),
            title = playerUIState.title,
            progress = mediaState.progress,
            bufferProgress = mediaState.bufferProgress,
            isShowUI = screenState.isShowControlUI,
            isShowRelatedList = screenState.isShowRelatedList,
            isFullscreen = screenState.isFullscreen,
            isLockScreen = screenState.isLockScreen,
            isPlaying = mediaState.isPlaying,
            isLoading = mediaState.isLoading,
            totalDuration = mediaState.totalDuration.formatTimeString(),
            currentDuration = mediaState.currentDuration.formatTimeString(),
            onFullscreenChange = onFullscreenChange,
            onPlayChange = onPlayChange,
            onProgressChange = onProgressChange,
            onLongPressStart = { onDoubleSpeedChange(true) },
            onLongPressEnd = { onDoubleSpeedChange(false) },
            onControlUIChange = onControlUIChange,
            onSetting = { onScreenAction(ScreenAction.SetSettingVisible(true)) },
            onBackPress = onBackPress,
            hintContent = { SpeedHint(speed = mediaState.speed) },
            unlockScreen = {},
            resetHideTimer = resetHideTimer
        ) {
            if (mediaState.showCover && cover != null) {
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


@kotlin.OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColumnScope.LandscapeInfoArea(
    context: Context = LocalContext.current,
    videoDetail: VideoDetailModel?,
    bangumiDetail: BangumiDetailModel?,
    infoCard: InfoCardModel?,
    hasLike: Boolean,
    hasCoin: Boolean,
    hasFavoured: Boolean,
    isDownloaded: Boolean,
    isShowLikeAnimation: Boolean,
    isFullscreen: Boolean,
) {
    val title by rememberUpdatedState(videoDetail?.view?.title ?: bangumiDetail?.title ?: "")
    Column(
        Modifier
            .fillMaxWidth()
            .weight(1f)
            .clip(RoundedCornerShape(LargePadding))
            .background(MaterialTheme.colorScheme.background)
            .padding(SmallPadding),
        verticalArrangement = Arrangement.spacedBy(SmallPadding)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
        videoDetail?.let { detail ->
            UserSubscriptionBar(
                face = detail.view.owner.face,
                name = detail.view.owner.name,
                fans = detail.card.card.fans.toViewString(),
                isSubscribed = infoCard?.following == true,
                onScreenAction = {},
                onVideoMenuAction = { }
            )
        }

        videoDetail?.let { detail ->
            HorizontalVideoMenu(
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
                onScreenAction = {},
                onVideoMenuAction = {},
                onAnimationEndCallback = {

                }
            )
        }

        videoDetail?.let { detail ->
            val tags = videoDetail.tags.fastMap { it.tagName }
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = SmallPadding),
                verticalArrangement = Arrangement.spacedBy(MediumPadding),
                horizontalArrangement = Arrangement.spacedBy(LargePadding),
                maxLines = 2,
                overflow = FlowRowOverflow.Clip
            ) {
                tags.fastForEach {
                    TagItem(it)
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
    val scope = rememberCoroutineScope()
    val menus by remember(
        playerUIState.videoPageList,
        playerUIState.videoArchives,
        playerUIState.videoDetail,
        playerUIState.relatedBangumis
    ) {
        derivedStateOf {
            buildList {
                playerUIState.videoPageList?.let { add(R.string.str_video_page_list) }
                playerUIState.videoArchives?.let { add(R.string.str_video_archive) }
                playerUIState.videoDetail?.related?.let { add(R.string.str_related_video) }
                add(R.string.str_reply)
                add(R.string.str_user_works)
                playerUIState.relatedBangumis?.let { add(R.string.str_related_bangumi) }
            }
        }
    }
    var isInMainReplyList by remember { mutableStateOf(true) }
    val pager = rememberPagerState { menus.size }
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(SmallPadding))
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScrollTabRow(
            tabs = menus,
            selectedTabIndex = pager.currentPage
        ) {
            scope.launch {
                pager.animateScrollToPage(it)
            }
        }

        HorizontalPager(
            state = pager
        ) { index ->
            when (menus[index]) {
                R.string.str_video_page_list -> {
                    GridVideoPageList(
                        pageList = playerUIState.videoPageList ?: emptyList(),
                        currentPageListIndex = playerUIState.currentPageListIndex,
                        onVideoMenuAction = onVideoMenuAction
                    )
                }

                R.string.str_user_works -> {
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
                            currentBvid = playerUIState.playParam.bvid,
                            bottomPadding = NonePadding,
                            onVideoMenuAction = onVideoMenuAction
                        )
                    }
                }

                R.string.str_related_video -> {
                    RelatedVideoList(
                        relatedList = playerUIState.videoDetail?.related ?: emptyList()
                    )
                }

                R.string.str_video_archive -> {
                    ArchiveList(
                        modifier = Modifier
                            .fillMaxWidth(),
                        listState = screenState.archiveListState,
                        bottomPadding = NonePadding,
                        currentArchiveIndex = playerUIState.currentArchiveIndex,
                        archiveList = playerUIState.videoArchives ?: emptyList(),
                        onVideoMenuAction = onVideoMenuAction
                    )
                }

                R.string.str_reply -> {
                    ReplyList(
                        replyItems = replies,
                        isCloseButtonVisible = false,
                        isInMainReplyList = isInMainReplyList,
                        bottomPadding = NonePadding,
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

                R.string.str_related_bangumi -> {

                }
            }
        }
    }
}

@Composable
private fun VideoMenuList(
    menus: List<Int>,
    currentIndex: Int,
    onClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SmallPadding)
    ) {
        menus.fastForEachIndexed { index, it ->
            AssistChip(
                onClick = { onClick(index) },
                label = {
                    Text(
                        text = stringResource(it),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
        }

    }
}

@OptIn(UnstableApi::class)
@Preview(showBackground = true, device = "id:pixel_tablet")
@Composable
private fun LandscapeVideoPagePreview(
    @PreviewParameter(FakePlayerState::class) uiStates: Triple<MediaPlayerUIState, MediaState, ScreenState>
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }
    LandscapeVideoPage(
        exoPlayer = exoPlayer,
        playerUIState = uiStates.first,
        mediaState = uiStates.second,
        screenState = uiStates.third,
        replies = uiStates.first.repliesFlow.collectAsLazyPagingItems(),
        works = uiStates.first.uploadedVideosFlow.collectAsLazyPagingItems(),
        onVideoFrameChange = {},
        onControlUIChange = {},
        onBackPress = {},
        onProgressChange = {},
        onPlayChange = {},
        onFullscreenChange = {},
        onScreenAction = {},
        onDoubleSpeedChange = {},
        resetHideTimer = {},
        onVideoMenuAction = {}
    )
}
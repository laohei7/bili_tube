package com.laohei.bili_tube.presentation.player

import android.graphics.Bitmap
import android.view.TextureView
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.laohei.bili_sdk.module_v2.video.VideoDetailModel
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.component.video.VideoItem
import com.laohei.bili_tube.core.SystemUtil
import com.laohei.bili_tube.presentation.player.component.CommentCard
import com.laohei.bili_tube.presentation.player.component.RelatedHorizontalList
import com.laohei.bili_tube.presentation.player.component.UserSimpleInfo
import com.laohei.bili_tube.presentation.player.component.VideoDetailSheet
import com.laohei.bili_tube.presentation.player.component.VideoMenus
import com.laohei.bili_tube.presentation.player.component.VideoSimpleInfo
import com.laohei.bili_tube.presentation.player.component.control.PlayerControl
import com.laohei.bili_tube.presentation.player.component.reply.VideoReplySheet
import com.laohei.bili_tube.presentation.player.state.media.MediaState
import com.laohei.bili_tube.presentation.player.state.screen.DefaultScreenManager
import com.laohei.bili_tube.presentation.player.state.screen.ScreenAction
import com.laohei.bili_tube.utill.formatDateToString
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.hideSystemUI
import com.laohei.bili_tube.utill.isOrientationPortrait
import com.laohei.bili_tube.utill.showSystemUI
import com.laohei.bili_tube.utill.toTimeAgoString
import com.laohei.bili_tube.utill.toViewString
import com.laohei.bili_tube.utill.toggleOrientation
import com.laohei.bili_tube.utill.useLightSystemBarIcon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt


@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    params: Route.Play
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val activity = LocalActivity.current
    val configuration = LocalConfiguration.current
    val systemBarHeight = SystemUtil.getStatusBarHeightDp() + SystemUtil.getNavigateBarHeightDp()
//    val screenWidth = configuration.screenWidthDp
//    val screenHeight = configuration.screenHeightDp
//    val minLimitedHeight = screenWidth * 9f / 16f
//    val maxLimitedHeight = screenHeight * 2 / 3f

    val viewModel =
        koinViewModel<PlayerViewModel> {
            parametersOf(
                params,
                DefaultScreenManager(
                    density,
                    configuration.screenHeightDp + systemBarHeight.value.roundToInt(),
                    configuration.screenWidthDp,
                    params.width,
                    params.height
                )
            )
        }
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val videoReplies = viewModel.videoReplies.collectAsLazyPagingItems()
    val mediaState by viewModel.state.collectAsStateWithLifecycle()


    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    val lazyListState = screenState.listState


    val isOrientationPortrait = isOrientationPortrait()

    val nestedScrollConnection = viewModel.nestedScrollConnection

    val animatedVideoHeight by animateDpAsState(
        targetValue = screenState.videoHeight
    )
    val animatedContentOffset by animateDpAsState(
        targetValue = screenState.videoHeight + if (screenState.isFullscreen) 0.dp else SystemUtil.getStatusBarHeightDp()
    )

    val contentModifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
    val otherSheetModifier = contentModifier
        .offset { with(density) { IntOffset(0, animatedContentOffset.toPx().toInt()) } }
    val isSystemDarkTheme = isSystemInDarkTheme()
    BackHandler(enabled = screenState.isFullscreen) {
        viewModel.fullscreenChanged(false, screenState.originalVideoHeight, isOrientationPortrait)
        if (!isOrientationPortrait) {
            activity?.toggleOrientation()
        }
    }


    DisposableEffect (Unit) {
        activity?.useLightSystemBarIcon(false)
        onDispose {
            activity?.useLightSystemBarIcon(isSystemDarkTheme.not())
        }
    }

    // video size changed
    LaunchedEffect(mediaState.width, mediaState.height) {
        viewModel.caculateScreenSize(mediaState.width, mediaState.height)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .draggable(
                enabled = !isOrientationPortrait && screenState.isFullscreen,
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
            title = playerState.videoDetail?.view?.title ?: "",
            exoPlayer = viewModel.exoPlayer(),
            mediaState = mediaState,
            videoHeight = animatedVideoHeight,
            isShowUI = screenState.isShowUI,
            isShowRelatedList = screenState.isShowRelatedList,
            isFullscreen = screenState.isFullscreen,
            images = playerState.videoDetail?.related?.take(3)?.map { it.pic },
            videoFrame = {
                viewModel.updateState(screenState.copy(bitmap = it))
            },
            fullscreen = {
                viewModel.fullscreenChanged(
                    it,
                    screenState.originalVideoHeight,
                    isOrientationPortrait
                )
                when {
                    (isOrientationPortrait && !it).not() -> {
                        activity?.toggleOrientation()
                    }
                }
            },
            uploadVideoHistory = viewModel::uploadVideoHistory,
            seekTo = viewModel::seekTo,
            setSpeed = viewModel::setSpeed,
            togglePlayPause = viewModel::togglePlayPause,
            onShowUIChanged = {
                viewModel.updateState(screenState.copy(isShowUI = it))
            },
            actionClick = {
                viewModel.screenActionHandle(
                    action = it,
                    isOrientationPortrait = isOrientationPortrait,
                    scope = scope,
                )
            }
        )

        if (isOrientationPortrait) {
            VideoContent(
                modifier = contentModifier
                    .offset {
                        with(density) {
                            IntOffset(0, animatedContentOffset.toPx().roundToInt())
                        }
                    }
                    .nestedScroll(connection = nestedScrollConnection)
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { },
                    ),
                lazyListState = lazyListState,
                videoDetail = playerState.videoDetail,
                onClick = {
                    viewModel.screenActionHandle(
                        it, true, scope,
                        updateParamsCallback = { newParams ->
                            scope.launch { viewModel.updateParams(newParams) }
                        })
                }
            )

            Box(
                modifier = contentModifier
                    .graphicsLayer { alpha = screenState.maskAlpha }
                    .background(Color.Black)
            )


            VideoReplySheet(
                isShowReply = screenState.isShowReplySheet,
                replies = videoReplies,
                onDismiss = { viewModel.updateState(screenState.copy(isShowReplySheet = false)) },
                modifier = otherSheetModifier,
                maskAlphaChanged = { viewModel.maskAlphaChanged(it) },
                bottomPadding = screenState.videoHeight
            )

            val videoDetail = playerState.videoDetail
            VideoDetailSheet(
                like = videoDetail?.view?.stat?.like?.toViewString(),
                view = videoDetail?.view?.stat?.view?.toViewString(),
                title = videoDetail?.view?.title,
                publishDate = videoDetail?.view?.pubdate?.formatDateToString(false),
                tags = videoDetail?.tags?.fastMap { it.tagName } ?: emptyList(),
                isShowDetail = screenState.isShowDetailSheet,
                onDismiss = { viewModel.updateState(screenState.copy(isShowDetailSheet = false)) },
                modifier = otherSheetModifier,
                maskAlphaChanged = { viewModel.maskAlphaChanged(it) },
                bottomPadding = screenState.videoHeight
            )

        }
        val animatedOffset by animateIntAsState(
            targetValue = screenState.relatedListOffset.toInt()
        )
        if (!isOrientationPortrait) {
            RelatedHorizontalList(
                modifier = Modifier
                    .zIndex(100f)
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(bottom = 12.dp)
                    .offset {
                        IntOffset(0, animatedOffset)
                    },
                playerState.videoDetail?.related ?: emptyList(),
                onClick = {
                    scope.launch { viewModel.updateParams(it) }
                }
            )
        }

    }
}


@OptIn(UnstableApi::class)
@Composable
private fun BoxScope.VideoArea(
    title: String = "",
    exoPlayer: ExoPlayer,
    mediaState: MediaState,
    videoHeight: Dp,
    isShowUI: Boolean,
    isShowRelatedList: Boolean,
    isFullscreen: Boolean,
    images: List<String>?,
    videoFrame: (Bitmap) -> Unit = {},
    fullscreen: (Boolean) -> Unit = {},
    uploadVideoHistory: ((Long) -> Unit)? = null,
    seekTo: (Float) -> Unit,
    setSpeed: (Float) -> Unit,
    togglePlayPause: () -> Unit,
    onShowUIChanged: (Boolean) -> Unit,
    actionClick: (ScreenAction) -> Unit
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


    val videoModifier = if (isOrientationPortrait()) {
        Modifier
            .height(videoHeight)
            .width(videoHeight * mediaState.width.toFloat() / mediaState.height)
    } else {
        Modifier
            .fillMaxHeight()
            .align(Alignment.Center)
    }
    val videoControlModifier = if (isOrientationPortrait()) {
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    } else videoModifier

    PlayerControl(
        title = title,
        modifier = videoControlModifier.zIndex(99f),
        progress = mediaState.progress,
        bufferProgress = mediaState.bufferProgress,
        isShowUI = isShowUI,
        isShowRelatedList = isShowRelatedList,
        isFullscreen = isFullscreen,
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
        actionContent = {
            VideoActions(
                images = images,
                showLabel = isFullscreen && !isOrientationPortrait(),
                onClick = actionClick
            )
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

@Composable
private fun VideoContent(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    videoDetail: VideoDetailModel?,
    onClick: (ScreenAction) -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            videoDetail?.let { detail ->
                item {
                    VideoSimpleInfo(
                        title = detail.view.title,
                        view = detail.view.stat.view.toViewString(),
                        date = detail.view.pubdate.toTimeAgoString(),
                        tag = when {
                            detail.tags.isNotEmpty() -> detail.tags.first().tagName
                            else -> null
                        },
                        onClick = {
                            onClick.invoke(ScreenAction.ShowVideoDetailAction)
                        }
                    )
                }
                item {
                    UserSimpleInfo(
                        face = detail.view.owner.face,
                        name = detail.view.owner.name,
                        fans = detail.card.card.fans.toViewString(),
                        onClick = {}
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    VideoMenus(
                        great = detail.view.stat.like.toViewString(),
                        coin = detail.view.stat.coin.toViewString(),
                        star = detail.view.stat.favorite.toViewString(),
                        share = detail.view.stat.share.toViewString(),
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    CommentCard(
                        comments = detail.view.stat.reply.toViewString(),
                        onClick = {
                            onClick.invoke(ScreenAction.ShowReplyAction)
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
                items(detail.related) { video ->
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
                                aid = video.aid,
                                bvid = video.bvid,
                                cid = video.cid,
                                width = video.dimension.width,
                                height = video.dimension.height
                            )
                            onClick.invoke(ScreenAction.SwitchVideoAction(newParams))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BlurBackgroundImage(
    bitmap: Bitmap? = null,
    isDrag: Boolean,
    isFullscreen: Boolean,
) {
    AnimatedVisibility(
        visible = isDrag.not() && bitmap != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        AnimatedContent(
            targetState = bitmap!!,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(durationMillis = 5000)
                ).togetherWith(
                    fadeOut(
                        animationSpec = tween(durationMillis = 5000)
                    )
                )
            }
        ) { targetState ->
            Image(
                painter = BitmapPainter(targetState.asImageBitmap()),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(
                        80.dp,
                        edgeTreatment = BlurredEdgeTreatment.Unbounded
                    ),
                contentScale = ContentScale.FillBounds,
            )
        }

        // Mask Color
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = when {
                        isOrientationPortrait() -> {
                            Brush.verticalGradient(
                                colors = when {
                                    isFullscreen -> portraitAndFullscreenGradient
                                    else -> portraitAndNotFullscreenGradient
                                }
                            )
                        }

                        else -> {
                            Brush.horizontalGradient(colors = notPortraitGradient)
                        }
                    }
                )
        )
    }
}

@Composable
private fun VideoActions(
    images: List<String>?,
    showLabel: Boolean = false,
    onClick: (ScreenAction) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        IconButton(
            onClick = {},
            colors = getIconButtonColor()
        ) {
            Icon(
                imageVector = Icons.Outlined.ThumbUp,
                contentDescription = Icons.Outlined.ThumbUp.name,
            )
        }

        IconButton(
            onClick = {},
            colors = getIconButtonColor()
        ) {
            Icon(
                imageVector = Icons.Outlined.ThumbDown,
                contentDescription = Icons.Outlined.ThumbDown.name,
            )
        }

        IconButton(
            onClick = {},
            colors = getIconButtonColor()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Comment,
                contentDescription = Icons.AutoMirrored.Outlined.Comment.name,
            )
        }

        IconButton(
            onClick = {},
            colors = getIconButtonColor()
        ) {
            Icon(
                imageVector = Icons.Outlined.StarOutline,
                contentDescription = Icons.Outlined.StarOutline.name,
            )
        }

        IconButton(
            onClick = {},
            colors = getIconButtonColor()
        ) {
            Icon(
                imageVector = Icons.Outlined.MoreHoriz,
                contentDescription = Icons.Outlined.MoreHoriz.name,
            )
        }

        Spacer(Modifier.weight(1f))

        images?.let {
            if (showLabel) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                ) {
                    Text(
                        text = stringResource(R.string.str_more_video),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(R.string.str_more_video_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                }
            }
            MoreVideoButton(
                images = it,
                onClick = { onClick.invoke(ScreenAction.ShowRelatedAction) }
            )
        }
    }
}

@Composable
private fun MoreVideoButton(
    images: List<String>,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.clickable {
            onClick.invoke()
        }
    ) {
        images.fastForEachIndexed { index, url ->
            AsyncImage(
                model = url,
                contentDescription = "",
                modifier = Modifier
                    .zIndex(images.size - index.toFloat())
                    .offset {
                        IntOffset(
                            0,
                            -10 * index
                        )
                    }
                    .graphicsLayer {
                        alpha = 1f - 0.15f * index
                        scaleX = 1f - 0.05f * index
                        scaleY = 1f - 0.05f * index
                    }
                    .width(60.dp)
                    .border(
                        1.dp, Color.White,
                        RoundedCornerShape(4.dp)
                    )
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun getIconButtonColor(): IconButtonColors {
    return IconButtonDefaults.iconButtonColors(
        contentColor = Color.White
    )
}


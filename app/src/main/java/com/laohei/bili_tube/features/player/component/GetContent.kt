package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.laohei.bili_sdk.module_v2.bangumi.RelatedBangumiItem
import com.laohei.bili_sdk.module_v2.user.InfoCardModel
import com.laohei.bili_sdk.module_v2.video.BangumiDetailModel
import com.laohei.bili_sdk.module_v2.video.VideoDetailModel
import com.laohei.bili_sdk.module_v2.video.VideoPageModel
import com.laohei.bili_tube.model.play.PlayParam
import com.laohei.bili_tube.R
import com.laohei.bili_tube.features.player.MediaPlayerUIState
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.features.player.state.screen.ScreenAction
import com.laohei.bili_tube.features.player.state.screen.ScreenState
import com.laohei.bili_tube.ui.component.video.HorizontalVideoItem
import com.laohei.bili_tube.ui.component.video.VerticalVideoItem
import com.laohei.bili_tube.ui.theme.PaddingMd
import com.laohei.bili_tube.util.toTimeString
import com.laohei.bili_tube.util.toTimeAgoString
import com.laohei.bili_tube.util.toViewString


@Composable
internal fun GetContent(
    modifier: Modifier,
    playerState: MediaPlayerUIState,
    screenState: ScreenState,
    bottomPadding: Dp = 0.dp,
    onScreenAction: (ScreenAction) -> Unit,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
    onSelectedAidChange: (Long) -> Unit,
    onSelectedBvidChange: (String) -> Unit,
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
                    infoCardModel = playerState.infoCardModel,
                    videoPageList = playerState.videoPageList,
                    currentPageListIndex = playerState.currentPageListIndex,
                    onScreenAction = onScreenAction,
                    onVideoMenuAction = onVideoMenuAction,
                    onSelectedAidChange = onSelectedAidChange,
                    onSelectedBvidChange = onSelectedBvidChange
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
                    onScreenAction = onScreenAction,
                    onVideoMenuAction = onVideoMenuAction,
                )
            } ?: run {
                PlayerPlaceholder(modifier = modifier)
            }
        }
    }
}


@Composable
private fun VideoContent(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    videoDetail: VideoDetailModel,
    infoCardModel: InfoCardModel?,
    videoPageList: List<VideoPageModel>?,
    hasLike: Boolean,
    hasCoin: Boolean,
    hasFavoured: Boolean,
    isDownloaded: Boolean,
    isShowLikeAnimation: Boolean,
    isFullscreen: Boolean,
    currentPageListIndex: Int,
    onScreenAction: (ScreenAction) -> Unit,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
    onSelectedAidChange: (Long) -> Unit,
    onSelectedBvidChange: (String) -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(PaddingMd),
        ) {
            item {
                VideoSimpleInfoBar(
                    title = videoDetail.view.title,
                    view = videoDetail.view.stat.view.toViewString(),
                    date = videoDetail.view.pubdate.toTimeAgoString(),
                    tag = when {
                        videoDetail.tags.isNotEmpty() -> videoDetail.tags.first().tagName
                        else -> null
                    },
                    onClick = {
                        onScreenAction(ScreenAction.SetVideoDetailVisible(true))
                    }
                )
            }
            item {
                UserSubscriptionBar(
                    face = videoDetail.view.owner.face,
                    name = videoDetail.view.owner.name,
                    fans = videoDetail.card.card.fans.toViewString(),
                    isSubscribed = infoCardModel?.following == true,
                    onScreenAction = onScreenAction,
                    onVideoMenuAction = onVideoMenuAction
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
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
                    onScreenAction = onScreenAction,
                    onVideoMenuAction = onVideoMenuAction,
                    onAnimationEndCallback = {
                        onScreenAction(ScreenAction.SetLikeAnimationVisible(false))
                    }
                )
            }
            videoPageList?.let {
                item {
                    HorizontalVideoPageList(
                        pageList = it,
                        currentPageListIndex = currentPageListIndex,
                        onVideoMenuAction = onVideoMenuAction
                    )
                }
            }
            item {
                CommentCard(
                    comments = videoDetail.view.stat.reply.toViewString(),
                    onScreenAction = onScreenAction
                )
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
            items(videoDetail.related) { video ->
                VerticalVideoItem(
                    bvid = video.bvid,
                    cover = video.pic,
                    title = video.title,
                    ownerFace = video.owner.face,
                    ownerName = video.owner.name,
                    view = video.stat.view.toViewString(),
                    pubDate = video.pubdate.toTimeAgoString(),
                    duration = video.duration.toTimeString(false),
                    trailingIcon = Icons.Outlined.MoreVert,
                    onClick = {
                        onVideoMenuAction(
                            VideoMenuAction.SwitchVideo(
                                PlayParam.VideoParam(
                                    width = video.dimension.width,
                                    height = video.dimension.height,
                                    aid = video.aid,
                                    bvid = video.bvid,
                                    cid = video.cid,
                                )
                            )
                        )
                    },
                    onTrailingClick = {
                        onSelectedAidChange(video.aid)
                        onSelectedBvidChange(video.bvid)
                        onScreenAction(ScreenAction.SetVideoMenuVisible(true))
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
    onScreenAction: (ScreenAction) -> Unit,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(PaddingMd)
        ) {
            item {
                BangumiTitleWidget(
                    modifier = itemModifier,
                    title = bangumiDetailModel.title
                )
            }
            item {
                BangumiInfoBar(
                    modifier = itemModifier,
                    views = bangumiDetailModel.stat.views,
                    favorites = bangumiDetailModel.stat.favorites,
                    score = bangumiDetailModel.rating?.score
                )
            }
            item {
                Spacer(Modifier.height(32.dp))
                VideoActionBar(
                    great = bangumiDetailModel.stat.likes.toViewString(),
                    coin = bangumiDetailModel.stat.coins.toViewString(),
                    star = bangumiDetailModel.stat.favorites.toViewString(),
                    share = bangumiDetailModel.stat.share.toViewString(),
                    hasLike = hasLike,
                    hasCoin = hasCoin,
                    hasFavoured = hasFavoured,
                    isDownloaded = isDownloaded,
                    showLikeAnimation = isShowLikeAnimation,
                    isFullscreen = isFullscreen,
                    onScreenAction = onScreenAction,
                    onVideoMenuAction = onVideoMenuAction,
                    onAnimationEndCallback = {
                        onScreenAction(ScreenAction.SetLikeAnimationVisible(false))
                    },
                )
                Spacer(Modifier.height(16.dp))
            }
            item {
                BangumiEpisodeTitleBar(
                    modifier = itemModifier.padding(vertical = 8.dp),
                    size = bangumiDetailModel.episodes.size
                )
                BangumiSeasonList(
                    modifier = itemModifier.padding(vertical = 4.dp),
                    listState = seasonState,
                    currentSeasonId = bangumiDetailModel.seasonId,
                    seasonList = bangumiDetailModel.seasons,
                    onVideoMenuAction = onVideoMenuAction
                )
                HorizontalBangumiEpisodeList(
                    modifier = Modifier.padding(vertical = 4.dp),
                    listState = episodeState,
                    currentEpId = currentEpId,
                    episodeList = bangumiDetailModel.episodes,
                    onVideoMenuAction = onVideoMenuAction
                )
            }
            item {
                CommentCard(
                    comments = "",
                    onScreenAction = {
                        onScreenAction(ScreenAction.SetReplyVisible(true))
                    }
                )
                Spacer(Modifier.height(8.dp))
            }
            items(relatedBangumis, key = { it.seasonId }) {
                HorizontalVideoItem(
                    cover = it.cover,
                    title = it.title,
                    ownerName = "",
                    rcmdReason = it.rcmdReason.ifBlank {
                        it.rating?.score?.run { "$this" + stringResource(R.string.str_score) }
                            ?: stringResource(R.string.str_no_score)
                    },
                    view = it.stat.view.toViewString(),
                    publishDate = it.stat.follow.toViewString() + "追番",
                    leadingIcon = null,
                    onClick = {
                        onVideoMenuAction(
                            VideoMenuAction.SwitchVideo(
                                PlayParam.BangumiParam(
                                    seasonId = it.seasonId,
                                    bvid = "",
                                    aid = -1,
                                    cid = -1
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
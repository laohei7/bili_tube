package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.laohei.bili_sdk.module_v2.bangumi.RelatedBangumiItem
import com.laohei.bili_sdk.module_v2.user.InfoCardModel
import com.laohei.bili_sdk.module_v2.video.BangumiDetailModel
import com.laohei.bili_sdk.module_v2.video.VideoDetailModel
import com.laohei.bili_sdk.module_v2.video.VideoPageModel
import com.laohei.bili_tube.PlayParam
import com.laohei.bili_tube.R
import com.laohei.bili_tube.features.player.MediaPlayerUIState
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.features.player.state.screen.ScreenAction
import com.laohei.bili_tube.features.player.state.screen.ScreenState
import com.laohei.bili_tube.ui.component.lottie.LottieIconPlaying
import com.laohei.bili_tube.ui.component.text.IconText
import com.laohei.bili_tube.ui.component.video.HorizontalVideoItem
import com.laohei.bili_tube.ui.component.video.VerticalVideoItem
import com.laohei.bili_tube.ui.theme.MediumPadding
import com.laohei.bili_tube.ui.theme.Pink
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.toTimeAgoString
import com.laohei.bili_tube.utill.toViewString


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
            verticalArrangement = Arrangement.spacedBy(MediumPadding),
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
                    onScreenAction = onScreenAction,
                    onVideoMenuAction = onVideoMenuAction,
                    onAnimationEndCallback = {
                        onScreenAction(ScreenAction.SetLikeAnimationVisible(false))
                    }
                )
            }
            videoPageList?.let {
                item {
                    VideoPageList(
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
                    duration = video.duration.formatTimeString(false),
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
            verticalArrangement = Arrangement.spacedBy(MediumPadding)
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
                                } ?: run {
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
                HorizontalVideoMenu(
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
                                onVideoMenuAction(VideoMenuAction.SwitchSeason(it.seasonId))
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
                                onVideoMenuAction(
                                    VideoMenuAction.SwitchEpisode(
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
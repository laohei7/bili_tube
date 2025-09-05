package com.laohei.bili_tube.features.player.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.laohei.bili_sdk.module_v2.reply.ReplyItem
import com.laohei.bili_tube.R
import com.laohei.bili_tube.features.player.component.reply.VideoReplyAction
import com.laohei.bili_tube.ui.component.animation.slideFadeHorizontal
import com.laohei.bili_tube.ui.component.state.LoadingStatePlaceholder
import com.laohei.bili_tube.ui.component.text.rich_text.ExpandableRichText
import com.laohei.bili_tube.util.toTimeAgoString
import com.laohei.bili_tube.util.toViewString

@Composable
internal fun ReplyList(
    replyItems: LazyPagingItems<ReplyItem>,
    isCloseButtonVisible: Boolean,
    isInMainReplyList: Boolean,
    mainListState: LazyListState = rememberLazyListState(),
    childListState: LazyListState = rememberLazyListState(),
    bottomPadding: Dp,
    onMainReplyListChange: (Boolean) -> Unit,
    onBackClick: () -> Unit,
) {
    var currentReplyItem by remember { mutableStateOf<ReplyItem?>(null) }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AnimatedContent(
            targetState = isInMainReplyList,
            transitionSpec = {
                slideFadeHorizontal(isInMainReplyList.not())
            },
        ) { target ->
            when {
                target -> {
                    MainReplyList(
                        lazyListState = mainListState,
                        replies = replyItems,
                        bottomPadding = bottomPadding,
                        onClick = { action ->
                            when (action) {
                                is VideoReplyAction.ToChildReplyListAction -> {
                                    currentReplyItem = action.item
                                    onMainReplyListChange(false)
                                }
                            }
                        }
                    )
                }

                else -> {
                    ChildReplyList(
                        lazyListState = childListState,
                        item = currentReplyItem!!,
                        bottomPadding = bottomPadding,
                    )
                }
            }

        }

        ReplyTopBar(
            isCloseButtonVisible = isCloseButtonVisible,
            isMainReplyList = isInMainReplyList,
            onClick = onBackClick
        )
    }
}


@Composable
private fun ReplyTopBar(
    isCloseButtonVisible: Boolean,
    isMainReplyList: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AnimatedContent(
            modifier = Modifier.weight(1f),
            transitionSpec = {
                slideFadeHorizontal(isMainReplyList.not())
            },
            targetState = isMainReplyList,
            contentAlignment = Alignment.CenterStart
        ) { target ->
            when {
                target -> {
                    Text(
                        text = stringResource(R.string.str_reply),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                else -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = onClick
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name,
                            )
                        }
                        Text(
                            text = stringResource(R.string.str_reply),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        if (isCloseButtonVisible) {
            IconButton(
                onClick = onClick
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = Icons.Default.Close.name,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainReplyList(
    lazyListState: LazyListState = rememberLazyListState(),
    bottomPadding: Dp = 0.dp,
    replies: LazyPagingItems<ReplyItem>,
    onClick: (VideoReplyAction) -> Unit
) {
    val refreshState = rememberPullToRefreshState()
    val isRefreshing = replies.loadState.refresh is LoadState.Loading
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        state = refreshState,
        onRefresh = { replies.refresh() },
        indicator = {
            Indicator(
                modifier = Modifier
                    .align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                state = refreshState,
            )
        }
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxHeight()
        ) {
            item { Spacer(Modifier.height(60.dp)) }
            items(replies.itemCount) { index ->
                val reply = replies[index]
                reply?.let {
                    VideoCommentItem(
                        item = it,
                        onClick = {
                            onClick.invoke(VideoReplyAction.ToChildReplyListAction(it))
                        }
                    )
                }
            }
            item {
                LoadingStatePlaceholder(replies.loadState.append)
            }
            item { Spacer(Modifier.height(bottomPadding)) }
        }
    }

}

@Composable
private fun ChildReplyList(
    lazyListState: LazyListState = rememberLazyListState(),
    item: ReplyItem,
    bottomPadding: Dp = 0.dp,
) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxHeight()
    ) {
        item { Spacer(Modifier.height(60.dp)) }
        item {
            VideoCommentItem(
                item = item,
                showReplyNumber = false
            ) {

            }
        }
        item.replies?.let {
            items(it) {
                Box(
                    modifier = Modifier.padding(start = 18.dp)
                ) {
                    VideoCommentItem(
                        item = it,
                        showReplyNumber = false
                    ) { }
                }
            }
        }
        item { Spacer(Modifier.height(bottomPadding)) }
    }
}

@Composable
private fun VideoCommentItem(
    item: ReplyItem,
    showReplyNumber: Boolean = true,
    onClick: () -> Unit
) {
    val name = item.member.uname
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .clickable {
                onClick.invoke()
            }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 头像
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.member.avatar)
                    .crossfade(true)
                    .build(),
                contentDescription = name,
                modifier = Modifier
                    .offset { IntOffset(0, 12) }
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.Green, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name[0].toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 用户名 & 时间
            Text(
                text = stringResource(
                    R.string.str_user_and_date,
                    item.member.uname,
                    item.ctime.toTimeAgoString(false)
                ),
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )

        }

        Spacer(modifier = Modifier.height(4.dp))

        // 评论内容
        ExpandableRichText(
            text = item.content.message,
            style = MaterialTheme.typography.bodyMedium,
            emote = item.content.emote?.map { it.key to it.value.url }?.toMap() ?: emptyMap(),
            modifier = Modifier.padding(start = 48.dp)
        )


        // 点赞、回复
        Row(
            modifier = Modifier
                .padding(start = 48.dp)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ThumbUp,
                    contentDescription = Icons.Outlined.ThumbUp.name,
                    modifier = Modifier.size(16.dp)
                )
                if (item.like > 0) {
                    Text(
                        text = item.like.toViewString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Icon(
                imageVector = Icons.Outlined.ThumbDown,
                contentDescription = Icons.Outlined.ThumbDown.name,
                modifier = Modifier.size(16.dp)
            )

            Icon(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                contentDescription = Icons.Outlined.ChatBubbleOutline.name,
                modifier = Modifier.size(16.dp)
            )
        }

        if (showReplyNumber) {
            item.replies?.let {
                if (it.isNotEmpty()) {
                    Text(
                        text = buildAnnotatedString {
                            append(it.size.toViewString())
                            append("条回复 >")
                        },
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 48.dp)
                    )
                }
            }
        }
    }
}
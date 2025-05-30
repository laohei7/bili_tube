package com.laohei.bili_tube.presentation.player.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.laohei.bili_sdk.module_v2.folder.FolderMediaItem
import com.laohei.bili_sdk.module_v2.video.VideoDimension
import com.laohei.bili_sdk.module_v2.video.VideoOwner
import com.laohei.bili_sdk.module_v2.video.VideoStat
import com.laohei.bili_sdk.module_v2.video.VideoView
import com.laohei.bili_tube.component.sheet.ModalBottomSheet
import com.laohei.bili_tube.component.sheet.rememberModalBottomSheet
import com.laohei.bili_tube.component.video.HorizontalVideoItem2
import com.laohei.bili_tube.component.video.VideoAction
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.toTimeAgoString
import com.laohei.bili_tube.utill.toViewString
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun PlaylistBar(
    modifier: Modifier = Modifier,
    nextTitle: String,
    folderTitle: String,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onClick.invoke()
            }
            .background(
                MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.9f)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.PlaylistPlay,
            contentDescription = Icons.AutoMirrored.Outlined.PlaylistPlay.name,
            tint = MaterialTheme.colorScheme.onBackground,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = nextTitle,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .basicMarquee()
            )
            Text(
                text = folderTitle,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .basicMarquee()
            )
        }

        Icon(
            imageVector = Icons.Outlined.KeyboardArrowUp,
            contentDescription = Icons.Outlined.KeyboardArrowUp.name,
            tint = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PlaylistSheet(
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp,
    folderTitle: String,
    playlistIndex: Int,
    playlistCount: Int,
    isShowSheet: Boolean,
    toViewList: List<VideoView>,
    folderResources: LazyPagingItems<FolderMediaItem>,
    onDismiss: () -> Unit = {},
    maskAlphaChanged: (Float) -> Unit = { _ -> },
    videoPlayActionClick: (VideoAction.VideoPlayAction) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheet(
        skipPartiallyExpanded = true
    )
    BackHandler(enabled = isShowSheet) {
        onDismiss.invoke()
    }
    if (isShowSheet) {
        LaunchedEffect(sheetState) {
            snapshotFlow { sheetState.requireOffset() }
                .collect { offset ->
                    maskAlphaChanged.invoke(offset)
                }
        }
        ModalBottomSheet(
            modifier = modifier,
            sheetState = sheetState,
            scrimColor = Color.Transparent,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            containerColor = MaterialTheme.colorScheme.background,
            onDismissRequest = { onDismiss.invoke() },
        ) {
            LazyColumn {
                stickyHeader {
                    FolderInfoBar(
                        folderTitle = folderTitle,
                        playlistIndex = playlistIndex + 1,
                        playlistCount = playlistCount,
                        onClick = {
                            scope.launch {
                                sheetState.hide()
                                onDismiss.invoke()
                            }
                        }
                    )
                }
                if (toViewList.isNotEmpty()) {
                    itemsIndexed(toViewList) { index, it ->
                        HorizontalVideoItem2(
                            cover = it.pic,
                            title = it.title,
                            duration = it.duration.formatTimeString(false),
                            view = it.stat.view.toViewString(),
                            progress = 0f,
                            pubdate = it.pubdate.toTimeAgoString(),
                            isCurrentPlaying = playlistIndex == index,
                            onClick = {
                                videoPlayActionClick(
                                    VideoAction.VideoPlayAction.SwitchPlaylistAction(
                                        bvid = it.bvid,
                                    )
                                )
                            }
                        )
                    }
                } else {
                    items(folderResources.itemCount) { index ->
                        folderResources[index]?.let {
                            HorizontalVideoItem2(
                                cover = it.cover,
                                title = it.title,
                                duration = it.duration.formatTimeString(false),
                                view = it.cntInfo.play.toViewString(),
                                progress = 0f,
                                pubdate = it.pubtime.toTimeAgoString(),
                                isCurrentPlaying = playlistIndex == index,
                                onClick = {
                                    videoPlayActionClick(
                                        VideoAction.VideoPlayAction.SwitchPlaylistAction(
                                            bvid = it.bvid,
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
                item { Spacer(Modifier.height(bottomPadding)) }
            }
        }
    }
}

@Composable
private fun FolderInfoBar(
    folderTitle: String,
    playlistIndex: Int,
    playlistCount: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                append(folderTitle)
                withStyle(
                    style = SpanStyle(
                        fontSize =
                            MaterialTheme.typography.bodyMedium.fontSize,
                        color = Color.Gray
                    )
                ) {
                    append(" · ")
                    append(playlistIndex.toString())
                    append("/")
                    append(playlistCount.toString())
                }

            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = Icons.Outlined.Close.name
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PlaylistBarPreview() {
    PlaylistBar(
        nextTitle = "下一个: Recomposition - Jetpack Compose",
        folderTitle = "Jetpack Compose · 1/10"
    )
}

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistSheetPreview() {
    PlaylistSheet(
        isShowSheet = true,
        folderTitle = "Jetpack Compose",
        playlistIndex = 1,
        playlistCount = 110,
        toViewList = List(20) {
            VideoView(
                bvid = "BV1xx411c7mD",
                aid = 12345678L,
                cid = 98765432L,
                pic = "https://example.com/cover.jpg",
                title = "示例视频标题",
                pubdate = 1685000000L,
                ctime = 1684900000L,
                desc = "这是一个视频描述。",
                duration = 360L,
                owner = VideoOwner(
                    mid = 10001L,
                    name = "up主名称",
                    face = "https://example.com/avatar.jpg"
                ),
                stat = VideoStat(
                    aid = 12345678L,
                    view = 100000L,
                    danmaku = 5000L,
                    reply = 300L,
                    favorite = 2000L,
                    coin = 1500L,
                    share = 800L,
                    like = 25000L,
                    dislike = 0L
                ),
                dimension = VideoDimension(
                    width = 1920,
                    height = 1080,
                    rotate = 0
                ),
                seasonId = null
            )
        },
        folderResources = flowOf(PagingData.empty<FolderMediaItem>()).collectAsLazyPagingItems(),
        videoPlayActionClick = {}
    )
}
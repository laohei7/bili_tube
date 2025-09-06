package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.paging.compose.LazyPagingItems
import com.laohei.bili_sdk.model_v2.folder.FolderMediaItem
import com.laohei.bili_tube.model.play.PlayParam
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.ui.component.video.HorizontalVideoCard
import com.laohei.bili_tube.ui.theme.PaddingMd
import com.laohei.bili_tube.util.toTimeString
import com.laohei.bili_tube.util.toTimeAgoString
import com.laohei.bili_tube.util.toViewString

@Composable
internal fun FolderMediaList(
    folderMediaList: LazyPagingItems<FolderMediaItem>,
    playParam: PlayParam.MediaList,
    listState: LazyListState,
    currentFolderMediaIndex: Int,
    bottomPadding: Dp,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
) {
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(PaddingMd)
    ) {
        items(folderMediaList.itemCount) { index ->
            val item = folderMediaList[index] ?: return@items
            HorizontalVideoCard(
                coverUrl = item.cover,
                title = item.title,
                ownerName = item.upper.name,
                duration = item.duration.toTimeString(false),
                viewCount = item.cntInfo.play.toViewString(),
                publishDate = item.pubtime.toTimeAgoString(false),
                isPlaying = currentFolderMediaIndex == index,
                leadingIcon = null,
                onClick = {
                    onVideoMenuAction(
                        VideoMenuAction.SwitchVideo(
                            playParam.copy(
                                bvid = item.bvid,
                                cid = -1L,
                                aid = item.id
                            )
                        )
                    )
                }
            )
        }
        item { Spacer(Modifier.height(bottomPadding)) }
    }
}
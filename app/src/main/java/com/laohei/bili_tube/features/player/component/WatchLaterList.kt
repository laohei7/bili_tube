package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.laohei.bili_sdk.module_v2.video.VideoView
import com.laohei.bili_tube.PlayParam
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.ui.component.video.HorizontalVideoItem
import com.laohei.bili_tube.ui.theme.MediumPadding
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.toTimeAgoString
import com.laohei.bili_tube.utill.toViewString

@Composable
internal fun WatchLaterList(
    playParam: PlayParam.MediaList,
    listState: LazyListState,
    watchLaterList: List<VideoView>,
    currentWatchLaterIndex: Int,
    bottomPadding: Dp,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
) {
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(MediumPadding)
    ) {
        itemsIndexed(watchLaterList) { index, item ->
            HorizontalVideoItem(
                cover = item.pic,
                title = item.title,
                ownerName = item.owner.name,
                duration = item.duration.formatTimeString(false),
                view = item.stat.view.toViewString(),
                publishDate = item.pubdate.toTimeAgoString(),
                isCurrentPlaying = currentWatchLaterIndex == index,
                leadingIcon = null,
                onClick = {
                    onVideoMenuAction(
                        VideoMenuAction.SwitchVideo(
                            playParam.copy(
                                bvid = item.bvid,
                                cid = item.cid,
                                aid = item.aid
                            )
                        )
                    )
                }
            )
        }
        item { Spacer(Modifier.height(bottomPadding)) }
    }
}
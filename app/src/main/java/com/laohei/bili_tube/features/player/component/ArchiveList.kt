package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.laohei.bili_sdk.model_v2.video.ArchiveItem
import com.laohei.bili_tube.model.play.PlayParam
import com.laohei.bili_tube.R
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.ui.component.video.HorizontalVideoCompactCard
import com.laohei.bili_tube.util.toTimeString
import com.laohei.bili_tube.util.toTimeAgoString
import com.laohei.bili_tube.util.toViewString

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ArchiveList(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    archiveList: List<ArchiveItem>,
    currentArchiveIndex: Int,
    bottomPadding: Dp,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        stickyHeader {
            Text(
                text = stringResource(R.string.str_select_episode),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 8.dp)
            )
        }
        itemsIndexed(archiveList) { index, item ->
            HorizontalVideoCompactCard(
                coverUrl = item.pic,
                title = item.title,
                viewCount = item.stat.view.toViewString(),
                duration = item.duration.toTimeString(false),
                progress = item.playbackPosition.toFloat() / item.duration,
                publishDate = item.pubdate.toTimeAgoString(false),
                isPlaying = index == currentArchiveIndex,
                onClick = {
                    onVideoMenuAction(
                        VideoMenuAction.SwitchVideo(
                            PlayParam.VideoParam(
                                aid = item.aid,
                                bvid = item.bvid,
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
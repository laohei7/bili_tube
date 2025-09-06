package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.laohei.bili_sdk.model_v2.video.VideoView
import com.laohei.bili_tube.model.play.PlayParam
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.ui.component.video.HorizontalVideoCard
import com.laohei.bili_tube.ui.theme.PaddingMd
import com.laohei.bili_tube.util.toTimeString
import com.laohei.bili_tube.util.toTimeAgoString
import com.laohei.bili_tube.util.toViewString

@Composable
internal fun RelatedVideoList(
    relatedList: List<VideoView>,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(PaddingMd)
    ) {
        items(relatedList) {
            HorizontalVideoCard(
                coverUrl = it.pic,
                title = it.title,
                ownerName = it.owner.name,
                duration = it.duration.toTimeString(false),
                viewCount = it.stat.view.toViewString(),
                publishDate = it.pubdate.toTimeAgoString(false),
                onClick = {
                    onVideoMenuAction(
                        VideoMenuAction.SwitchVideo(
                            PlayParam.VideoParam(
                                aid = it.aid,
                                bvid = it.bvid,
                                cid = it.cid
                            )
                        )
                    )
                },
                onMoreClick = {

                },
                leadingIcon = null
            )
        }
    }
}
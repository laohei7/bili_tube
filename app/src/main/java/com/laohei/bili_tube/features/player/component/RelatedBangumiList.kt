package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.laohei.bili_sdk.model_v2.bangumi.RelatedBangumiItem
import com.laohei.bili_tube.model.play.MediaPlayConfig
import com.laohei.bili_tube.R
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.ui.component.video.HorizontalVideoCard
import com.laohei.bili_tube.ui.theme.PaddingMd
import com.laohei.bili_tube.util.toViewString

@Composable
internal fun RelatedBangumiList(
    relatedList: List<RelatedBangumiItem>,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(PaddingMd)
    ) {
        items(relatedList) {
            HorizontalVideoCard(
                coverUrl = it.cover,
                title = it.title,
                ownerName = "",
                recommendation = it.rcmdReason.ifBlank {
                    it.rating?.score?.run { "$this" + stringResource(R.string.str_score) }
                        ?: stringResource(R.string.str_no_score)
                },
                viewCount = it.stat.view.toViewString(),
                publishDate = it.stat.follow.toViewString() + "追番",
                leadingIcon = null,
                onClick = {
                    onVideoMenuAction(
                        VideoMenuAction.SwitchVideo(
                            MediaPlayConfig.BangumiPlayConfig(
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
    }
}
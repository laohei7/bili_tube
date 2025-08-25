package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.laohei.bili_sdk.module_v2.video.VideoView
import com.laohei.bili_tube.ui.component.video.HorizontalVideoItem
import com.laohei.bili_tube.ui.theme.MediumPadding
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.toTimeAgoString
import com.laohei.bili_tube.utill.toViewString

@Composable
internal fun RelatedVideoList(relatedList: List<VideoView>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(MediumPadding)
    ) {
        items(relatedList) {
            HorizontalVideoItem(
                cover = it.pic,
                title = it.title,
                ownerName = it.owner.name,
                duration = it.duration.formatTimeString(false),
                view = it.stat.view.toViewString(),
                publishDate = it.pubdate.toTimeAgoString(),
                onClick = {

                },
                trailingOnClick = {

                },
                leadingIcon = null
            )
        }
    }
}
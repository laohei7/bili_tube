package com.laohei.bili_tube.presentation.player.component.archive

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laohei.bili_sdk.module_v2.common.Dimension
import com.laohei.bili_sdk.module_v2.video.VideoPageListModel
import com.laohei.bili_tube.component.lottie.LottieIconPlaying
import com.laohei.bili_tube.component.video.VideoAction

@Composable
internal fun PageListWidget(
    pageList: List<VideoPageListModel>,
    currentPageListIndex: Int,
    onClick: (VideoAction.VideoPlayAction) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Spacer(modifier = Modifier) }
        itemsIndexed(pageList) { index, it ->
            PageListItem(
                modifier = Modifier
                    .padding(end = 8.dp),
                item = it,
                isSelected = index == currentPageListIndex,
                onClick = { cid ->
                    onClick.invoke(VideoAction.VideoPlayAction.SwitchPlayListAction(cid = cid))
                }
            )
        }
        item { Spacer(modifier = Modifier) }
    }
}

@Composable
private fun PageListItem(
    modifier: Modifier = Modifier,
    item: VideoPageListModel,
    isSelected: Boolean,
    onClick: (cid: Long) -> Unit
) {
    Surface(
        modifier = modifier,
        onClick = { onClick.invoke(item.cid) },
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = when {
            isSelected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onBackground
        },
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(vertical = 16.dp)
                .width(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                LottieIconPlaying(
                    Modifier
                        .padding(end = 4.dp)
                        .size(16.dp)
                )
            }

            Text(
                text = item.part,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
                modifier = when {
                    isSelected -> Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                    else -> Modifier
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PageListItemPreview() {
    val item = remember {
        VideoPageListModel(
            cid = -1L,
            page = 1,
            from = "",
            part = "第一季 一百三情，飞龙侍极！！！",
            duration = 1,
            vid = "1",
            weblink = "",
            firstFrame = "",
            ctime = 1,
            dimension = Dimension(0, 0, 0)
        )
    }
    PageListItem(
        item = item,
        isSelected = true,
        onClick = {}
    )
}
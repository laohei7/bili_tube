package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import com.laohei.bili_sdk.module_v2.video.VideoPageModel
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.ui.component.lottie.LottieIconPlaying
import com.laohei.bili_tube.ui.theme.SmallPadding

@Composable
internal fun GridVideoPageList(
    pageList: List<VideoPageModel>,
    currentPageListIndex: Int,
    onVideoMenuAction: (VideoMenuAction) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth(),
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy(SmallPadding),
        horizontalArrangement = Arrangement.spacedBy(SmallPadding)
    ) {

        itemsIndexed(pageList) { index, it ->
            VideoPageItem(
                modifier = Modifier
                    .padding(end = 8.dp),
                item = it,
                isSelected = index == currentPageListIndex,
                onClick = {
                    onVideoMenuAction(VideoMenuAction.SwitchVideoPage(it.cid))
                }
            )
        }
    }
}

@Composable
internal fun HorizontalVideoPageList(
    pageList: List<VideoPageModel>,
    currentPageListIndex: Int,
    onVideoMenuAction: (VideoMenuAction) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = SmallPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SmallPadding)
    ) {
        item { Spacer(modifier = Modifier) }
        itemsIndexed(pageList) { index, it ->
            VideoPageItem(
                modifier = Modifier
                    .padding(end = 8.dp),
                item = it,
                isSelected = index == currentPageListIndex,
                onClick = {
                    onVideoMenuAction(VideoMenuAction.SwitchVideoPage(it.cid))
                }
            )
        }
        item { Spacer(modifier = Modifier) }
    }
}

@Composable
private fun VideoPageItem(
    modifier: Modifier = Modifier,
    item: VideoPageModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        onClick = { onClick() },
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
private fun MediaSeriesItemPreview() {
    val item = remember {
        VideoPageModel(
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
    VideoPageItem(
        item = item,
        isSelected = true,
        onClick = {}
    )
}
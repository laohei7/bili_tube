package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.laohei.bili_sdk.model_v2.video.SeasonModel
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.ui.theme.Pink

@Composable
internal fun BangumiSeasonList(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    currentSeasonId: Long,
    seasonList: List<SeasonModel>,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        state = listState
    ) {
        items(seasonList) {
            Surface(
                onClick = {
                    onVideoMenuAction(VideoMenuAction.SwitchSeason(it.seasonId))
                },
                contentColor = when {
                    currentSeasonId == it.seasonId -> Pink
                    else -> MaterialTheme.colorScheme.onBackground
                },
                shape = CircleShape
            ) {
                Text(
                    text = it.seasonTitle,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
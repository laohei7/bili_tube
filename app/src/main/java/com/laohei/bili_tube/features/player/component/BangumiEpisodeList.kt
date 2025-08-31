package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.laohei.bili_sdk.module_v2.video.EpisodeModel
import com.laohei.bili_tube.R
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.ui.component.animation.lottie.AnimatedPlayingIcon
import com.laohei.bili_tube.ui.theme.PaddingMd
import com.laohei.bili_tube.ui.theme.Pink

@Composable
internal fun GridBangumiEpisodeList(
    modifier: Modifier = Modifier,
    listState: LazyGridState,
    currentEpId: Long,
    episodeList: List<EpisodeModel>,
    onVideoMenuAction: (VideoMenuAction) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        state = listState,
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy(PaddingMd),
        horizontalArrangement = Arrangement.spacedBy(PaddingMd)
    ) {
        items(episodeList) {
            EpisodeItem(
                episode = it,
                selected = currentEpId == it.epId,
                onClick = {
                    onVideoMenuAction(
                        VideoMenuAction.SwitchEpisode(
                            episodeId = it.epId,
                            aid = it.aid,
                            cid = it.cid,
                            bvid = it.bvid
                        )
                    )
                }
            )
        }
    }
}

@Composable
internal fun HorizontalBangumiEpisodeList(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    currentEpId: Long,
    episodeList: List<EpisodeModel>,
    onVideoMenuAction: (VideoMenuAction) -> Unit
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        state = listState
    ) {
        item { Spacer(Modifier) }
        items(episodeList) {
            EpisodeItem(
                episode = it,
                selected = currentEpId == it.epId,
                onClick = {
                    onVideoMenuAction(
                        VideoMenuAction.SwitchEpisode(
                            episodeId = it.epId,
                            aid = it.aid,
                            cid = it.cid,
                            bvid = it.bvid
                        )
                    )
                }
            )
        }
        item { Spacer(Modifier) }
    }
}

@Composable
private fun EpisodeItem(
    episode: EpisodeModel,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .width(150.dp)
            .height(60.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = when {
            selected -> Pink
            else -> MaterialTheme.colorScheme.onBackground
        },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (selected) {
                    AnimatedPlayingIcon(modifier = Modifier.size(16.dp))
                }
                val title = episode.title.toDoubleOrNull()
                Text(
                    text = when {
                        title != null && episode.title.contains(".") -> {
                            stringResource(
                                R.string.str_episode_string,
                                episode.title
                            )
                        }

                        title != null -> {
                            stringResource(R.string.str_episode, title.toInt())
                        }

                        else -> episode.title
                    },
                    modifier = Modifier.wrapContentSize(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = episode.longTitle,
                modifier = Modifier
                    .wrapContentSize()
                    .basicMarquee(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
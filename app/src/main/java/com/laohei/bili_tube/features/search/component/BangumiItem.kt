package com.laohei.bili_tube.features.search.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.theme.Pink
import com.laohei.bili_tube.util.toViewString
import com.laohei.bili_tube.util.toYearString


@Composable
internal fun BangumiItem(
    title: String,
    cover: String,
    areas: String,
    date: String,
    styles: String,
    score: Float,
    userCount: String,
    episodes: List<String>?,
    onClick: () -> Unit
) {
    val coverRequest = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(cover)
            .crossfade(false)
            .placeholder(R.drawable.icon_loading_16_9)
            .error(R.drawable.bg)
            .build()
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(MaterialTheme.colorScheme.background)
            .clickable { onClick.invoke() }
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = coverRequest,
                contentDescription = title,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(3f / 4f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier.weight(2.5f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.str_date_and_area, date, areas),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    color = Color.LightGray
                )
                Text(
                    text = styles,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    color = Color.LightGray
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(score.toString())
                        }
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(stringResource(R.string.str_score))
                        }
                        append(" ")
                        withStyle(
                            SpanStyle(
                                fontSize = MaterialTheme.typography.labelSmall.fontSize
                            )
                        ) {
                            append(stringResource(R.string.str_vote, userCount))
                        }
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                )
            }
            Surface(
                onClick = {},
                shape = CircleShape,
                color = Pink,
                contentColor = Color.White
            ) {
                Text(
                    text = "立即观看",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        episodes?.let { list ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when {
                    list.size > 6 -> {
                        list.take(2).fastForEach {
                            EpisodeWidget(title = it, modifier = Modifier.weight(1f))
                        }
                        EpisodeWidget(title = "...", modifier = Modifier.weight(1f))
                        list.takeLast(3).fastForEach {
                            EpisodeWidget(title = it, modifier = Modifier.weight(1f))
                        }
                    }

                    else -> {
                        list.fastForEach {
                            EpisodeWidget(title = it, modifier = Modifier.weight(1f))
                        }
                        repeat(6 - list.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EpisodeWidget(
    title: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        onClick = {},
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .wrapContentSize()
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BangumiItemPreview() {
    BangumiItem(
        title = "Fate/Zero 第一季Fate/Zero 第一季Fate/Zero 第一季Fate/Zero 第一季",
        cover = "",
        areas = "日本",
        date = (1317398400).toYearString(isMill = false),
        styles = "时泪/奇幻/战斗/热血",
        score = 9.6f,
        userCount = (27386).toViewString(),
        episodes = List(8) { "$it" },
        onClick = {}
    )
}
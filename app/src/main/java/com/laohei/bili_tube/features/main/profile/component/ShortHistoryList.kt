package com.laohei.bili_tube.features.main.profile.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.laohei.bili_sdk.model_v2.history.HistoryItem
import com.laohei.bili_tube.R
import com.laohei.bili_tube.model.play.PlayParam
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.component.widget.VideoCoverOverlay
import com.laohei.bili_tube.ui.theme.CornerRadiusMd
import com.laohei.bili_tube.ui.theme.PaddingSm
import com.laohei.bili_tube.ui.viewmodel.SharedViewModel
import com.laohei.bili_tube.util.toTimeString
import org.koin.compose.koinInject


@Composable
internal fun ShortHistoryList(
    histories: List<HistoryItem>,
    navigateToAppRoute: (AppRoute) -> Unit,
    onMoreClick: (String) -> Unit,
) {
    val sharedViewModel = koinInject<SharedViewModel>()
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.str_history),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        trailingContent = {
            TextButton(onClick = { navigateToAppRoute(AppRoute.History) }) {
                Text(text = stringResource(R.string.str_see_all))
            }
        }
    )
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(Modifier) }
        items(histories) {
            val progress = when {
                it.duration > 0L -> (it.progress.toFloat() / it.duration)
                    .coerceIn(0f, 1f)

                else -> 0f
            }
            HistoryVideoCard(
                coverUrl = it.cover,
                title = it.title,
                ownerName = it.authorName,
                duration = it.duration.toTimeString(false),
                watchProgress = progress,
                onMoreClick = {
                    onMoreClick("${it.history.business}_${it.history.oid}")
                },
                onClick = {
                    sharedViewModel.setPlayParam(
                        PlayParam.VideoParam(
                            aid = it.history.oid,
                            bvid = it.history.bvid,
                            cid = it.history.cid
                        )
                    )
                    navigateToAppRoute(AppRoute.Play)
                }
            )
        }
        item { Spacer(Modifier) }
    }
}


@Composable
private fun HistoryVideoCard(
    coverUrl: String,
    title: String,
    ownerName: String,
    duration: String,
    watchProgress: Float,
    shape: Shape = RoundedCornerShape(CornerRadiusMd),
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(180.dp)
            .clip(shape)
            .clickable { onClick.invoke() },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        VideoCoverOverlay(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape),
            coverUrl = coverUrl,
            contentDescription = title,
            duration = duration,
            watchProgress = watchProgress
        )

        HistoryItemHeaderWithMenu(
            title = title,
            ownerName = ownerName,
            onMoreClick = onMoreClick
        )

    }
}

@Composable
private fun HistoryItemHeaderWithMenu(
    modifier: Modifier = Modifier,
    title: String,
    ownerName: String,
    maxTitleLines: Int = 2,
    maxOwnerLines: Int = 1,
    onMoreClick: () -> Unit,
) {
    Layout(
        modifier = modifier.fillMaxWidth(),
        content = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = maxTitleLines,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.layoutId("title")
            )
            Text(
                text = ownerName,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                maxLines = maxOwnerLines,
                modifier = Modifier.layoutId("owner")
            )
            IconButton(
                onClick = { onMoreClick() },
                modifier = Modifier
                    .layoutId("more")
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "More",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    ) { measurables, constraints ->
        val layoutWidth = constraints.maxWidth

        val morePlaceable = measurables.first { it.layoutId == "more" }.measure(Constraints())
        val titlePlaceable = measurables.first { it.layoutId == "title" }.measure(
            constraints.copy(
                minWidth = layoutWidth - morePlaceable.width,
                maxWidth = layoutWidth - morePlaceable.width
            )
        )
        val ownerPlaceable = measurables.first { it.layoutId == "owner" }.measure(
            constraints.copy(
                minWidth = layoutWidth - morePlaceable.width,
                maxWidth = layoutWidth - morePlaceable.width
            )
        )

        val columnHeight = titlePlaceable.height + ownerPlaceable.height

        val layoutHeight = maxOf(columnHeight, morePlaceable.height) + PaddingSm.roundToPx()

        layout(layoutWidth, layoutHeight) {
            titlePlaceable.place(0, 0)
            ownerPlaceable.place(0, titlePlaceable.height)

            morePlaceable.place(
                x = layoutWidth - morePlaceable.width,
                y = 0
            )
        }
    }
}
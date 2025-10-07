package com.laohei.bili_tube.ui.component.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.ui.theme.PaddingSm


@Composable
fun HeaderWithMenu(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
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
                text = subtitle,
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
package com.laohei.bili_tube.component.video

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun ViewAndPubDateWidget(
    view: String,
    publishDate: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {

        Icon(
            imageVector = Icons.Outlined.PlayCircleOutline,
            contentDescription = Icons.Outlined.PlayCircleOutline.name,
            modifier = Modifier
                .size(14.dp),
            tint = Color.Red.copy(alpha = 0.5f)
        )

        Text(
            text = "${view}观看 · $publishDate",
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}
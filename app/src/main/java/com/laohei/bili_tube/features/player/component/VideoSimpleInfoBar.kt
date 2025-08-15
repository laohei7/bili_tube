package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.features.player.MAX_LENGTH


@Composable
internal fun VideoSimpleInfoBar(
    title: String,
    view: String,
    date: String,
    tag: String? = null,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable { onClick.invoke() }
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 2,
            color = MaterialTheme.colorScheme.onBackground,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${view}次观看", style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Text(
                text = date, style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            tag?.let {
                Text(
                    text = it.run {
                        if (length > MAX_LENGTH) {
                            substring(0, MAX_LENGTH) + "..."
                        } else {
                            this
                        }
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "...展开", style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }

    }
}
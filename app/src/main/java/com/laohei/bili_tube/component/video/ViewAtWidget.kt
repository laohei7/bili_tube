package com.laohei.bili_tube.component.video

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
internal fun ViewAtWidget(
    viewAt: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.AccessTime,
            contentDescription = Icons.Outlined.AccessTime.name,
            modifier = Modifier
                .size(14.dp),
            tint = Color.Gray
        )

        Text(
            text = viewAt,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall
                .copy(fontSize = 10.sp),
            color = Color.Gray
        )
    }
}
package com.laohei.bili_tube.component.video

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun RcmdWidget(rcmdReason: String) {
    Surface(
        color = Color.Transparent,
        contentColor = if (rcmdReason.isNotBlank()) Color.Red else Color.Transparent,
        shape = RoundedCornerShape(3.dp),
        border = BorderStroke(
            width = 0.5.dp,
            color = if (rcmdReason.isNotBlank()) Color.Red else Color.Transparent,
        )
    ) {
        Text(
            text = rcmdReason,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 8.sp
            ),
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 6.dp)
        )
    }
}
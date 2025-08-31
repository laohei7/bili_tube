package com.laohei.bili_tube.ui.component.chip

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
fun RecommendationTag(reason: String) {
    if (reason.isBlank()) return
    Surface(
        color = Color.Transparent,
        contentColor = Color.Red,
        shape = RoundedCornerShape(3.dp),
        border = BorderStroke(0.5.dp, Color.Red)
    ) {
        Text(
            text = reason,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}
package com.laohei.bili_tube.component.placeholder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CirclePlaceholder(
    radius: Dp = 28.dp,
    color: Color = Color.Gray
) {
    Box(
        modifier = Modifier
            .size(radius)
            .clip(CircleShape)
            .background(color)
    )
}
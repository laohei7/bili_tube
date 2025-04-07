package com.laohei.bili_tube.component.video

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun VideoDurationWidget(
    modifier: Modifier=Modifier,
    duration: String = "00:17"
){
    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.5f),
        contentColor = Color.White,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = duration,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .wrapContentSize()
                .padding(3.dp)
        )
    }
}
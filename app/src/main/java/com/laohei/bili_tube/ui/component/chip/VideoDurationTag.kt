package com.laohei.bili_tube.ui.component.chip

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.laohei.bili_tube.ui.theme.CornerRadiusXs
import com.laohei.bili_tube.ui.theme.PaddingXs
import com.laohei.bili_tube.util.toTimeString

@Composable
fun VideoDurationTag(
    modifier: Modifier = Modifier,
    duration: String
) {
    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.5f),
        contentColor = Color.White,
        shape = RoundedCornerShape(CornerRadiusXs / 2)
    ) {
        Text(
            text = duration,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = PaddingXs, vertical = PaddingXs / 2),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VideoDurationTagPreview() {
    VideoDurationTag(
        duration = 485692L.toTimeString(true)
    )
}
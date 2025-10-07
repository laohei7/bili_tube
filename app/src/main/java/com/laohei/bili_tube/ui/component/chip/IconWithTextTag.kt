package com.laohei.bili_tube.ui.component.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.ui.theme.CornerRadiusXs
import com.laohei.bili_tube.ui.theme.PaddingSm
import com.laohei.bili_tube.ui.theme.PaddingXs
import com.laohei.bili_tube.util.toViewString

@Composable
fun IconWithTextTag(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    contentColor: Color = Color.White
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(PaddingXs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = icon.name,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun IconWithTextTagPreview() {
    IconWithTextTag(
        modifier = Modifier
            .clip(RoundedCornerShape(CornerRadiusXs))
            .background(
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(CornerRadiusXs)
            )
            .padding(vertical = PaddingXs, horizontal = PaddingSm),
        icon = Icons.AutoMirrored.Rounded.PlaylistPlay,
        label = 34.toViewString()
    )
}
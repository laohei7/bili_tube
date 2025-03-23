package com.laohei.bili_tube.component.button

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.utill.toViewString

@Preview
@Composable
fun ExtendedIconButton(
    icon: ImageVector = Icons.Rounded.Star,
    color: Color = MaterialTheme.colorScheme.onBackground,
    labelColor:Color = MaterialTheme.colorScheme.onBackground,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    label: String = 999999.toViewString(),
    onClick:(()->Unit)?=null
) {
    Surface(
        color = containerColor,
        shape = CircleShape,
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick?.invoke() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = icon.name,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                color = labelColor,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun ExtendedIconButton(
    icon1: ImageVector,
    icon2: ImageVector,
    icon1Color:Color = MaterialTheme.colorScheme.onBackground,
    color: Color = MaterialTheme.colorScheme.onBackground,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    label: String = 999999.toViewString(),
    onIcon1Click:(()->Unit)?=null,
    onIcon2Click:(()->Unit)?=null,
) {
    Surface(
        color = containerColor,
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            ExtendedIconButton(
                icon = icon1, label = label,
                color = icon1Color,
                onClick = onIcon1Click
            )

            VerticalDivider(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .clip(CircleShape),
                color = Color.LightGray.copy(alpha = 0.9f)
            )

            Surface(
                color = Color.Transparent,
                shape = CircleShape,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onIcon2Click?.invoke() }
            ) {
                Icon(
                    imageVector = icon2,
                    contentDescription = icon2.name,
                    tint = color,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .size(18.dp)
                )
            }
        }
    }
}
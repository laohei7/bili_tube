package com.laohei.bili_tube.component.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconText(
    modifier: Modifier = Modifier,
    leftIcon: ImageVector? = null,
    rightIcon: ImageVector? = null,
    text: String,
    leftIconSize: Dp = 22.dp,
    rightIconSize: Dp = 22.dp,
    leftIconColor: Color = MaterialTheme.colorScheme.onBackground,
    rightIconColor: Color = MaterialTheme.colorScheme.onBackground,
    style: TextStyle = LocalTextStyle.current,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        leftIcon?.let {
            Icon(
                imageVector = it,
                contentDescription = it.name,
                modifier = Modifier.size(leftIconSize),
                tint = leftIconColor
            )
        }
        Text(
            text = text,
            style = style,
            maxLines = 1,
            modifier = Modifier.wrapContentSize()
        )
        rightIcon?.let {
            Icon(
                imageVector = it,
                contentDescription = it.name,
                modifier = Modifier.size(rightIconSize),
                tint = rightIconColor
            )
        }
    }
}

@Preview
@Composable
private fun IconTextPreview() {
    Column {
        IconText(
            leftIcon = Icons.Outlined.PlayCircleOutline,
            text = "3849.2万",
            leftIconSize = 12.dp,
            leftIconColor = Color.LightGray,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.LightGray
            )
        )
        IconText(
            rightIcon = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            text = "详情",
            rightIconSize = 14.dp,
            rightIconColor = Color.LightGray,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.LightGray
            )
        )
    }
}
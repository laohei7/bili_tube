package com.laohei.bili_tube.component.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.R

@Composable
fun SubscribeButton(
    isSubscribed: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.onBackground,
        contentColor = MaterialTheme.colorScheme.background,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = when {
                    isSubscribed -> Icons.Outlined.Check
                    else -> Icons.Outlined.Notifications
                },
                contentDescription = "icon_subscription",
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = when {
                    isSubscribed -> stringResource(R.string.str_subscribed)
                    else -> stringResource(R.string.str_subscription)
                },
                style = MaterialTheme.typography.labelSmall,
            )
        }

    }
}
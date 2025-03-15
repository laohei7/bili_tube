package com.laohei.bili_tube.presentation.player.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.component.button.ExtendedIconButton


@Composable
internal fun VideoMenus(
    great: String,
    coin: String,
    star: String,
    share: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier)
        ExtendedIconButton(
            icon1 = Icons.Outlined.ThumbUp,
            icon2 = Icons.Outlined.ThumbDown,
            label = great,
            onIcon1Click = {},
            onIcon2Click = {}
        )
        ExtendedIconButton(
            icon = Icons.Outlined.Paid,
            label = coin,
            onClick = {}
        )
        ExtendedIconButton(
            icon = Icons.Outlined.StarOutline,
            label = star,
            onClick = {}
        )
        ExtendedIconButton(
            icon = Icons.Outlined.Share,
            label = share,
            onClick = {}
        )
    }
}
package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.component.text.IconText

@Composable
internal fun BangumiEpisodeTitleBar(
    modifier: Modifier = Modifier,
    size: Int
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.str_select_episode),
            style = MaterialTheme.typography.bodyMedium
        )
        IconText(
            text = stringResource(R.string.str_all_num_episode, size),
            rightIcon = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            rightIconSize = 14.dp,
            rightIconColor = Color.LightGray,
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color.LightGray
            )
        )
    }
}
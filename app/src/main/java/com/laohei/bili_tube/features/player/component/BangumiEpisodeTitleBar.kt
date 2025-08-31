package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.component.widget.DetailLabel

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
        DetailLabel(
            label = stringResource(R.string.str_all_num_episode, size),
        )
    }
}
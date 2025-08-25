package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.component.text.IconText
import com.laohei.bili_tube.utill.toViewString

@Composable
internal fun BangumiInfoBar(
    modifier: Modifier = Modifier,
    views: Long,
    favorites: Long,
    score: Float?
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconText(
                leftIcon = Icons.Outlined.PlayCircleOutline,
                text = views.toViewString(),
                leftIconSize = 12.dp,
                leftIconColor = Color.LightGray,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.LightGray
                )
            )
            IconText(
                leftIcon = Icons.Outlined.FavoriteBorder,
                text = favorites.toViewString(),
                leftIconSize = 12.dp,
                leftIconColor = Color.LightGray,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.LightGray
                )
            )
            IconText(
                text = buildString {
                    score?.let {
                        append(it.toString())
                        append(stringResource(R.string.str_score))
                    } ?: run {
                        append(stringResource(R.string.str_no_score))
                    }
                },
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.Red
                )
            )

        }
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
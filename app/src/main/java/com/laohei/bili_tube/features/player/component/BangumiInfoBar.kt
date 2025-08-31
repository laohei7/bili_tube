package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.component.widget.DetailLabel
import com.laohei.bili_tube.ui.component.widget.FavoriteLabel
import com.laohei.bili_tube.ui.component.widget.IconPosition
import com.laohei.bili_tube.ui.component.widget.IconWithText
import com.laohei.bili_tube.ui.component.widget.ViewLabel
import com.laohei.bili_tube.util.toViewString

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
            ViewLabel(view = views.toViewString())
            FavoriteLabel(favorites = favorites.toViewString())
            IconWithText(
                icon = {},
                iconPosition = IconPosition.None,
                label = buildString {
                    score?.let {
                        append(it.toString())
                        append(stringResource(R.string.str_score))
                    } ?: run {
                        append(stringResource(R.string.str_no_score))
                    }
                },
                textColor = Color.Red
            )
        }
        DetailLabel(label = stringResource(R.string.str_detail))
    }
}
package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.core.AlphaFraction
import com.laohei.bili_tube.ui.component.animation.lottie.AnimatedPlayingIcon
import com.laohei.bili_tube.ui.theme.PaddingMd
import com.laohei.bili_tube.ui.theme.PaddingSm

@Composable
internal fun GroupInfoBar(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    subcontent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = AlphaFraction),
        shape = RoundedCornerShape(PaddingMd),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingMd, vertical = PaddingSm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.PlaylistPlay,
                contentDescription = Icons.AutoMirrored.Outlined.PlaylistPlay.name,
                tint = MaterialTheme.colorScheme.onBackground,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .basicMarquee()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(PaddingMd)
                ) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray,
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                            .basicMarquee()
                    )
                    subcontent?.invoke()
                }
            }

            Icon(
                imageVector = Icons.Outlined.KeyboardArrowUp,
                contentDescription = Icons.Outlined.KeyboardArrowUp.name,
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupInfoBarPreview() {
    GroupInfoBar(
        title = "下一个: Recomposition - Jetpack Compose",
        subtitle = "Jetpack Compose · 1/10",
        onClick = {

        }
    )
}

@Preview(showBackground = true)
@Composable
private fun GroupInfoBarPreview2() {
    GroupInfoBar(
        modifier = Modifier
            .fillMaxWidth(),
        title = "下一个: Recomposition - Jetpack Compose",
        subtitle = "合集 · 我要被这群人吓死 · 1/16",
        subcontent = {
            AnimatedPlayingIcon(Modifier.size(12.dp))
        },
        onClick = {

        }
    )
}
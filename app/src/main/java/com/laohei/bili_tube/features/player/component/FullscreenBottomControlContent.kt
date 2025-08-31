package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.component.animation.lottie.AnimatedLikeIcon
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.features.player.state.screen.ScreenAction
import com.laohei.bili_tube.ui.theme.Pink


@Composable
internal fun FullscreenBottomControlContent(
    images: List<String>?,
    showLabel: Boolean = false,
    hasLike: Boolean,
    hasFavoured: Boolean,
    showLikeAnimation: Boolean,
    isFullscreen: Boolean,
    onScreenAction: (ScreenAction) -> Unit,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
) {
    var localHasLike by remember { mutableStateOf(hasLike) }
    LaunchedEffect(hasLike) {
        localHasLike = hasLike
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box {
            IconButton(
                onClick = {
                    onVideoMenuAction.invoke(
                        VideoMenuAction.Like(if (localHasLike) 2 else 1)
                    )
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = when {
                        localHasLike -> Pink
                        else -> Color.White
                    }
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.ThumbUp,
                    contentDescription = Icons.Outlined.ThumbUp.name,
                )
            }
            if (showLikeAnimation && isFullscreen) {
                Popup(
                    offset = IntOffset(10, -120)
                ) {
                    AnimatedLikeIcon(
                        modifier = Modifier.size(46.dp),
                        onAnimationEndCallback = {
                            onScreenAction.invoke(
                                ScreenAction.SetLikeAnimationVisible(false)
                            )
                        }
                    )
                }
            }
        }

        IconButton(
            onClick = {
                onScreenAction.invoke(
                    ScreenAction.SetReplyVisible(true)
                )
            },
            colors = getIconButtonColor()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Comment,
                contentDescription = Icons.AutoMirrored.Outlined.Comment.name,
            )
        }

        IconButton(
            onClick = {
                onScreenAction.invoke(
                    ScreenAction.SetModifyFolderVisible(true)
                )
            },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = when {
                    hasFavoured -> Pink
                    else -> Color.White
                }
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.StarOutline,
                contentDescription = Icons.Outlined.StarOutline.name,
            )
        }

        Spacer(Modifier.weight(1f))

        images?.let {
            if (showLabel) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                ) {
                    Text(
                        text = stringResource(R.string.str_more_video),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(R.string.str_more_video_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                }
            }
            MoreVideoButton(
                images = it,
                onClick = { onScreenAction.invoke(ScreenAction.ShowRelated) }
            )
        }
    }
}


@Composable
private fun getIconButtonColor(): IconButtonColors {
    return IconButtonDefaults.iconButtonColors(
        contentColor = Color.White
    )
}
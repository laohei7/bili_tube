package com.laohei.bili_tube.presentation.player.component

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
import com.laohei.bili_tube.component.lottie.LottieIconLike
import com.laohei.bili_tube.component.video.VideoAction
import com.laohei.bili_tube.presentation.player.state.screen.ScreenAction
import com.laohei.bili_tube.ui.theme.Pink


@Composable
internal fun FullscreenVideoActions(
    images: List<String>?,
    showLabel: Boolean = false,
    hasLike: Boolean,
    hasFavoured: Boolean,
    isShowLikeAnimation: Boolean,
    isFullscreen: Boolean,
    screenActionClick: (ScreenAction) -> Unit,
    videoMenuClick: (VideoAction.VideoMenuAction) -> Unit,
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
                    videoMenuClick.invoke(
                        VideoAction.VideoMenuAction.VideoLikeAction(
                            if (localHasLike) 2 else 1
                        )
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
            if (isShowLikeAnimation && isFullscreen) {
                Popup(
                    offset = IntOffset(10, -120)
                ) {
                    LottieIconLike(
                        modifier = Modifier.size(46.dp),
                        iterateForever = false,
                        onAnimationEndCallback = {
                            screenActionClick.invoke(
                                ScreenAction.ShowLikeAnimationAction(false)
                            )
                        }
                    )
                }
            }
        }

//        IconButton(
//            onClick = {},
//            colors = getIconButtonColor()
//        ) {
//            Icon(
//                imageVector = Icons.Outlined.ThumbDown,
//                contentDescription = Icons.Outlined.ThumbDown.name,
//            )
//        }

        IconButton(
            onClick = {
                screenActionClick.invoke(
                    ScreenAction.ShowReplyAction
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
                screenActionClick.invoke(
                    ScreenAction.ShowFolderSheetAction(true)
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

//        IconButton(
//            onClick = {},
//            colors = getIconButtonColor()
//        ) {
//            Icon(
//                imageVector = Icons.Outlined.MoreHoriz,
//                contentDescription = Icons.Outlined.MoreHoriz.name,
//            )
//        }

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
                onClick = { screenActionClick.invoke(ScreenAction.ShowRelatedAction) }
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
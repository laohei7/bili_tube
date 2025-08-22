package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.component.button.ExtendedIconButton
import com.laohei.bili_tube.ui.component.lottie.LottieIconLike
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.features.player.state.screen.ScreenAction


@Composable
internal fun HorizontalVideoMenu(
    great: String,
    coin: String,
    star: String,
    share: String,
    hasLike: Boolean,
    hasCoin: Boolean,
    hasFavoured: Boolean,
    isDownloaded: Boolean,
    showLikeAnimation: Boolean,
    isFullscreen: Boolean,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
    onScreenAction: (ScreenAction) -> Unit,
    onAnimationEndCallback: (() -> Unit)? = null
) {
    var localHasLike by remember { mutableStateOf(hasLike) }
    var localHasCoin by remember { mutableStateOf(hasCoin) }
    var localHasFavoured by remember { mutableStateOf(hasFavoured) }
    LaunchedEffect(hasLike, hasCoin, hasFavoured) {
        localHasLike = hasLike
        localHasCoin = hasCoin
        localHasFavoured = hasFavoured
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier)
        Box {
            ExtendedIconButton(
                icon1 = Icons.Outlined.ThumbUp,
                icon2 = Icons.Outlined.ThumbDown,
                label = great,
                icon1Color = if (localHasLike) Color.Red else MaterialTheme.colorScheme.onBackground,
                onIcon1Click = {
                    onVideoMenuAction(VideoMenuAction.Like(if (localHasLike) 2 else 1))
                },
                onIcon2Click = {}
            )
            if (showLikeAnimation && !isFullscreen) {
                Popup(
                    offset = IntOffset(10, -120)
                ) {
                    LottieIconLike(
                        modifier = Modifier.size(46.dp),
                        iterateForever = false,
                        onAnimationEndCallback = onAnimationEndCallback
                    )
                }
            }
        }
        Spacer(modifier = Modifier)
        ExtendedIconButton(
            icon = Icons.Outlined.Paid,
            label = coin,
            color = if (localHasCoin) Color.Red else MaterialTheme.colorScheme.onBackground,
            onClick = {
                if (localHasCoin.not()) {
                    onScreenAction(ScreenAction.SetAddCoinVisible(true))
                }
            }
        )
        Spacer(modifier = Modifier)
        ExtendedIconButton(
            icon = Icons.Outlined.StarOutline,
            color = if (localHasFavoured) Color.Red else MaterialTheme.colorScheme.onBackground,
            label = star,
            onClick = { onScreenAction(ScreenAction.SetModifyFolderVisible(true)) }
        )
        Spacer(modifier = Modifier)
        ExtendedIconButton(
            icon = Icons.Outlined.Share,
            label = share,
            onClick = {}
        )
        Spacer(modifier = Modifier)
        ExtendedIconButton(
            enabled = isDownloaded.not(),
            icon = Icons.Outlined.Download,
            label = when {
                isDownloaded -> stringResource(R.string.str_downloaded)
                else -> stringResource(R.string.str_download)
            },
            onClick = { onScreenAction(ScreenAction.SetDownloadVisible(true))}
        )
        Spacer(modifier = Modifier)
    }
}


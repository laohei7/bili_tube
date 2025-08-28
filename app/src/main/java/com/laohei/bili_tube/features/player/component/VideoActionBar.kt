package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Paid
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.rounded.ThumbDown
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.laohei.bili_tube.R
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.features.player.state.screen.ScreenAction
import com.laohei.bili_tube.ui.component.chip.StateChip
import com.laohei.bili_tube.ui.component.lottie.LottieIconLike
import com.laohei.bili_tube.ui.theme.SmallPadding


@Composable
internal fun VideoActionBar(
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
    val localHasLike by rememberUpdatedState(hasLike)
    var localHasCoin by remember { mutableStateOf(hasCoin) }
    var localHasFavoured by remember { mutableStateOf(hasFavoured) }
    LaunchedEffect(hasLike, hasCoin, hasFavoured) {
        localHasCoin = hasCoin
        localHasFavoured = hasFavoured
    }

    val chipModifier = Modifier
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceContainer)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SmallPadding)
    ) {
        Spacer(modifier = Modifier)
        Box {
            StateChip(
                modifier = chipModifier,
                icon = Icons.Rounded.ThumbUp,
                trailingIcon = Icons.Rounded.ThumbDown,
                label = great,
                iconColor = if (localHasLike) Color.Red else MaterialTheme.colorScheme.onBackground,
                onClick = {
                    onVideoMenuAction(VideoMenuAction.Like(if (localHasLike) 2 else 1))
                },
                onTrailingClick = {}
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
//        Spacer(modifier = Modifier)
        StateChip(
            modifier = chipModifier,
            icon = Icons.Rounded.Paid,
            label = coin,
            iconColor = if (localHasCoin) Color.Red else MaterialTheme.colorScheme.onBackground,
            onClick = {
                if (localHasCoin.not()) {
                    onScreenAction(ScreenAction.SetAddCoinVisible(true))
                }
            },
        )
//        Spacer(modifier = Modifier)
        StateChip(
            modifier = chipModifier,
            icon = if (localHasFavoured) Icons.Rounded.Star else Icons.Rounded.StarOutline,
            label = star,
            iconColor = if (localHasFavoured) Color.Red else MaterialTheme.colorScheme.onBackground,
            onClick = {
                onScreenAction(ScreenAction.SetModifyFolderVisible(true))
            },
        )
//        Spacer(modifier = Modifier)
        StateChip(
            modifier = chipModifier,
            icon = Icons.Rounded.Share,
            label = share,
            onClick = {
            },
        )
//        Spacer(modifier = Modifier)
        StateChip(
            modifier = chipModifier,
            enabled = isDownloaded.not(),
            icon = Icons.Rounded.Download,
            label = when {
                isDownloaded -> stringResource(R.string.str_downloaded)
                else -> stringResource(R.string.str_download)
            },
            onClick = {
                onScreenAction(ScreenAction.SetDownloadVisible(true))
            },
        )
        Spacer(modifier = Modifier)
    }
}


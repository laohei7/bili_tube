package com.laohei.bili_tube.features.player.component.control

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilledTonalIconButton
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.component.animation.lottie.AnimatedLoadingIcon
import com.laohei.bili_tube.ui.util.SystemUtil
import com.laohei.bili_tube.ui.util.isOrientationPortrait
import com.laohei.bili_tube.ui.util.rememberHasDisplayCutout
import com.laohei.bili_tube.util.toTimeString
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private const val TAG = "PlayerControl"

@Composable
fun PlayerControl(
    modifier: Modifier = Modifier,
    isShowRelatedList: Boolean = false,
    isShowUI: Boolean = false,
    title: String = "",
    isPlaying: Boolean = false,
    isLoading: Boolean = true,
    isLockScreen: Boolean = false,
    progress: Float = 0f,
    bufferProgress: Float = 0f,
    totalDuration: String = 0.toTimeString(),
    currentDuration: String = 0.toTimeString(),
    isFullscreen: Boolean,
    onFullscreenToggle: (Boolean) -> Unit,
    onPlaybackStateChanged: (Boolean) -> Unit,
    onProgressUpdate: ((Float) -> Unit)? = null,
    onLongPressStart: (() -> Unit)? = null,
    onLongPressEnd: (() -> Unit)? = null,
    onControlUIVisibilityChange: (Boolean) -> Unit,
    onOpenSettings: (() -> Unit)? = null,
    onBackPressed: (() -> Unit)? = null,
    unlockScreen: () -> Unit,
    restartHideTimer: () -> Unit,
    hintContent: (@Composable () -> Unit)? = null,
    bottomControls: (@Composable () -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {

    val localIsFullscreen by rememberUpdatedState(isFullscreen)
    var localIsPlaying by remember { mutableStateOf(isPlaying) }
    var localIsShowUI by remember { mutableStateOf(isShowUI) }
    var localIsLockScreen by remember { mutableStateOf(isLockScreen) }
    var isLongPress by remember { mutableStateOf(false) }
    var isShowUnlockHint by remember { mutableStateOf(false) }

    LaunchedEffect(isFullscreen, isPlaying, isShowUI, isLockScreen) {
        localIsPlaying = isPlaying
//        localIsFullscreen = isFullscreen
        localIsShowUI = isShowUI
        localIsLockScreen = isLockScreen
    }

    LaunchedEffect(isShowUnlockHint) {
        if (isShowUnlockHint) {
            delay(1000)
            isShowUnlockHint = false
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        restartHideTimer()
                        if (localIsLockScreen) {
                            if (!isShowUnlockHint) {
                                isShowUnlockHint = true
                            }
                        } else {
                            onControlUIVisibilityChange.invoke(localIsShowUI.not())
                        }
                    },
                    onPress = {
                        restartHideTimer()
                        if (localIsLockScreen) {
                            return@detectTapGestures
                        }
                        awaitRelease()
                        if (isLongPress) {
                            isLongPress = false
                            onLongPressEnd?.invoke()
                        }
                    },
                    onLongPress = {
                        restartHideTimer()
                        if (localIsLockScreen) {
                            return@detectTapGestures
                        }
                        isLongPress = true
                        onLongPressStart?.invoke()
                    },
                    onDoubleTap = {
                        restartHideTimer()
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        )

        content()

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = if (isFullscreen) SystemUtil.getSystemBarHeightDp() else 4.dp),
            visible = isLongPress,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            hintContent?.invoke()
        }

        // Mask
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = localIsShowUI,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Mask(modifier = Modifier.fillMaxSize())
        }

        // Top Menus
        TopBar(
            title = title,
            isShowUI = localIsShowUI,
            isShowRelatedList = isShowRelatedList,
            isFullscreen = localIsFullscreen,
            settingsClick = {
                restartHideTimer()
                onOpenSettings?.invoke()
            },
            backPressedClick = onBackPressed
        )

        // Center Play or Pause Button
        CenterButtons(
            isShowUI = localIsShowUI,
            isPlaying = localIsPlaying,
            onPlayChanged = {
                restartHideTimer()
                onPlaybackStateChanged(localIsPlaying.not())
            }
        )

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AnimatedLoadingIcon(
                modifier = Modifier.size(88.dp)
            )
        }

        // Bottom Indicator
        BottomBar(
            isShowUI = localIsShowUI,
            isFullscreen = localIsFullscreen,
            isShowRelatedList = isShowRelatedList,
            totalDuration = totalDuration,
            currentDuration = currentDuration,
            progress = progress,
            bufferProgress = bufferProgress,
            onFullscreenClick = {
                restartHideTimer()
                onFullscreenToggle(localIsFullscreen.not())
            },
            progressChanged = {
                restartHideTimer()
                onProgressUpdate?.invoke(it)
            },
            actionContent = bottomControls
        )

        UnlockButton(
            isLockScreen = localIsLockScreen,
            isShowUnlockHint = isShowUnlockHint,
            onClick = {
                restartHideTimer()
                unlockScreen.invoke()
                isShowUnlockHint = true
            }
        )
    }
}

@Composable
private fun BoxScope.UnlockButton(
    isLockScreen: Boolean,
    isShowUnlockHint: Boolean,
    onClick: () -> Unit
) {
    if (isLockScreen) {
        Box(
            modifier = Modifier
                .padding(bottom = 60.dp)
                .width(120.dp)
                .height(40.dp)
                .align(Alignment.BottomCenter)
                .clip(CircleShape)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onClick.invoke() },
        )
    }
    AnimatedVisibility(
        modifier = Modifier
            .padding(bottom = 80.dp)
            .align(Alignment.BottomCenter),
        visible = isShowUnlockHint,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        AssistChip(
            onClick = { onClick.invoke() },
            shape = CircleShape,
            border = AssistChipDefaults.assistChipBorder(
                enabled = true,
                borderWidth = 0.dp,
                borderColor = Color.Transparent
            ),
            colors = AssistChipDefaults.assistChipColors(
                containerColor = Color.White,
                leadingIconContentColor = Color.Black,
                labelColor = Color.Black,
            ),
            leadingIcon = {
                if (isLockScreen) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = Icons.Outlined.Lock.name,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.LockOpen,
                        contentDescription = Icons.Outlined.LockOpen.name,
                        modifier = Modifier.size(16.dp)
                    )
                }
            },
            label = {
                if (isLockScreen) {
                    Text(
                        text = stringResource(R.string.str_screen_lock_hint),
                        style = MaterialTheme.typography.labelMedium
                    )
                } else {
                    Text(
                        text = stringResource(R.string.str_screen_unlock_hint),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        )
    }


}

@Composable
private fun Mask(
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .background(color = Color.Black.copy(alpha = 0.5f))
    )
}

@Composable
private fun BoxScope.TopBar(
    title: String,
    isShowUI: Boolean,
    isShowRelatedList: Boolean,
    isFullscreen: Boolean,
    backPressedClick: (() -> Unit)? = null,
    settingsClick: (() -> Unit)? = null
) {
    val isOrientationPortrait = isOrientationPortrait()
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.TopStart),
        visible = isShowUI && !isShowRelatedList,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        val paddingModifier = when {
            isOrientationPortrait && (isFullscreen && rememberHasDisplayCutout()) -> {
                Modifier.padding(top = SystemUtil.getStatusBarHeightDp())
            }

            !isOrientationPortrait && isFullscreen && rememberHasDisplayCutout() -> {
                Modifier.padding(
                    start = SystemUtil.getStatusBarHeightDp(),
                    end = SystemUtil.getNavigateBarHeightDp()
                )
            }

            else -> Modifier
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(paddingModifier),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { backPressedClick?.invoke() }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = Icons.Default.KeyboardArrowDown.name,
                    tint = Color.White
                )
            }


            Text(
                text = if (isFullscreen) title else "",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()
                    .weight(1f),
            )

            Row {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Cast,
                        contentDescription = Icons.Default.Cast.name,
                        tint = Color.White
                    )
                }
                IconButton(onClick = { settingsClick?.invoke() }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = Icons.Default.Settings.name,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun BoxScope.CenterButtons(
    isShowUI: Boolean,
    isPlaying: Boolean,
    onPlayChanged: () -> Unit,
    onPreviousChanged: (() -> Unit)? = null,
    onNextChanged: (() -> Unit)? = null,
) {
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.Center),
        visible = isShowUI,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            onPreviousChanged?.let {
                FilledTonalIconButton(
                    modifier = Modifier.size(42.dp),
                    onClick = { it.invoke() },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.5f),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = Icons.Default.SkipPrevious.name
                    )
                }
            }

            FilledTonalIconButton(
                modifier = Modifier.size(52.dp),
                onClick = { onPlayChanged.invoke() },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.5f),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "play and pause",
                    modifier = Modifier.size(32.dp)
                )
            }

            onNextChanged?.let {
                FilledTonalIconButton(
                    modifier = Modifier.size(42.dp),
                    onClick = { it.invoke() },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.5f),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = Icons.Default.SkipNext.name
                    )
                }
            }
        }
    }
}

@Composable
private fun BoxScope.BottomBar(
    density: Density = LocalDensity.current,
    isShowUI: Boolean,
    isFullscreen: Boolean,
    isShowRelatedList: Boolean,
    totalDuration: String,
    currentDuration: String,
    progress: Float,
    bufferProgress: Float,
    onFullscreenClick: () -> Unit,
    progressChanged: (Float) -> Unit,
    actionContent: (@Composable () -> Unit)? = null
) {
    val isPortrait = isOrientationPortrait()

    val shouldShowProgress = remember(isPortrait, isFullscreen, isShowUI, isShowRelatedList) {
        ((isPortrait && !isFullscreen) || (isFullscreen && isShowUI) || (!isPortrait && !isFullscreen)) && !isShowRelatedList
    }

    val isLandscapeNormal = !isPortrait && !isFullscreen
    val shouldAlignmentCenter = isFullscreen || isLandscapeNormal

    val progressOffsetY = animateIntAsState(
        targetValue = if (shouldAlignmentCenter) 0 else with(density) {
            7.dp.toPx().roundToInt()
        },
        animationSpec = tween(durationMillis = 500)
    )
    val barBottomPadding = animateDpAsState(
        targetValue = if (isFullscreen) SystemUtil.getNavigateBarHeightDp() * 2 else 0.dp,
        animationSpec = tween(durationMillis = 500)
    )
    val barOffsetY = animateDpAsState(
        targetValue = if (isFullscreen) 0.dp else 16.dp,
        animationSpec = tween(durationMillis = 500)
    )
    val paddingModifier = when {
        isOrientationPortrait() -> {
            Modifier.padding(bottom = barBottomPadding.value)
        }

        rememberHasDisplayCutout() -> {
            Modifier.padding(
                start = SystemUtil.getStatusBarHeightDp(),
                end = SystemUtil.getNavigateBarHeightDp()
            )
        }

        else -> Modifier
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomStart)
            .then(paddingModifier),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // duration time  fullscreen btn
        AnimatedVisibility(
            visible = isShowUI && !isShowRelatedList,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .offset(y = barOffsetY.value)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$currentDuration/$totalDuration",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
                IconButton(
                    onClick = { onFullscreenClick() }
                ) {
                    Icon(
                        imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = "fullscreen",
                        modifier = Modifier.padding(3.dp),
                        tint = Color.White
                    )
                }
            }
        }



        // progress indicator
        AnimatedVisibility(
            visible = shouldShowProgress,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            VideoProgressIndicator(
                isShowThumb = isShowUI,
                progress = progress,
                bufferProgress = bufferProgress,
                modifier = Modifier.offset { IntOffset(0, progressOffsetY.value) },
                onProgressChanged = {
                    progressChanged.invoke(it)
                }
            )
        }

        AnimatedVisibility(
            visible = isFullscreen && isShowUI && !isShowRelatedList,
            enter = fadeIn(tween(durationMillis = 300)) + expandVertically(tween(durationMillis = 300)),
            exit = fadeOut(tween(durationMillis = 300)) + shrinkVertically(tween(durationMillis = 300))
        ) {
            actionContent?.invoke()
                ?: DefaultBottomBarAction()
        }
    }
}

@Composable
private fun DefaultBottomBarAction() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = {},
            colors = getIconButtonColor()
        ) {
            Icon(
                imageVector = Icons.Outlined.Speed,
                contentDescription = Icons.Outlined.Speed.name,
            )
        }

        IconButton(
            onClick = {},
            colors = getIconButtonColor()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.VolumeOff,
                contentDescription = Icons.Outlined.Speed.name,
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



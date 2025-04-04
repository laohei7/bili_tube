package com.laohei.bili_tube.presentation.player.component.control

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.R
import com.laohei.bili_tube.component.lottie.LottieIconLoading
import com.laohei.bili_tube.component.lottie.LottieIconSpeed
import com.laohei.bili_tube.core.util.SystemUtil
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.isOrientationPortrait
import com.laohei.bili_tube.utill.rememberHasDisplayCutout
import kotlinx.coroutines.delay

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
    totalDuration: String = 0.formatTimeString(),
    currentDuration: String = 0.formatTimeString(),
    isFullscreen: Boolean,
    fullscreen: (Boolean) -> Unit = {},
    onPlayChanged: (Boolean) -> Unit = {},
    onProgressChanged: (Float) -> Unit = {},
    onLongPressStart: () -> Unit = {},
    onLongPressEnd: () -> Unit = {},
    onShowUIChanged: (Boolean) -> Unit = {},
    settingsClick: (() -> Unit)? = null,
    backPressedClick: (() -> Unit)? = null,
    longPressHint: (@Composable () -> Unit)? = null,
    actionContent: (@Composable () -> Unit)? = null,
    unlockScreenClick: () -> Unit,
    content: @Composable () -> Unit
) {
    var localIsFullscreen by remember { mutableStateOf(isFullscreen) }
    var localIsPlaying by remember { mutableStateOf(isPlaying) }
    var localIsShowUI by remember { mutableStateOf(isShowUI) }
    var localIsLockScreen by remember { mutableStateOf(isLockScreen) }
    var isLongPress by remember { mutableStateOf(false) }
    var isShowUnlockHint by remember { mutableStateOf(false) }

    val videoContainerColor by animateColorAsState(
        targetValue = if (localIsFullscreen) Color.Transparent else Color.Black
    )

    LaunchedEffect(isFullscreen, isPlaying, isShowUI, isLockScreen) {
        localIsPlaying = isPlaying
        localIsFullscreen = isFullscreen
        localIsShowUI = isShowUI
        localIsLockScreen = isLockScreen
    }

    LaunchedEffect(isShowUnlockHint) {
        if (isShowUnlockHint) {
            Log.d("TAG", "PlayerControl: click")
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
                        if (localIsLockScreen) {
                            if (!isShowUnlockHint) {
                                isShowUnlockHint = true
                            }
                        } else {
                            onShowUIChanged.invoke(localIsShowUI.not())
                        }
                    },
                    onPress = {
                        if (localIsLockScreen) {
                            return@detectTapGestures
                        }
                        awaitRelease()
                        if (isLongPress) {
                            isLongPress = false
                            onLongPressEnd.invoke()
                        }
                    },
                    onLongPress = {
                        if (localIsLockScreen) {
                            return@detectTapGestures
                        }
                        isLongPress = true
                        onLongPressStart.invoke()
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(
                    top = if (isFullscreen) 0.dp else SystemUtil.getStatusBarHeightDp()
                )
                .background(color = videoContainerColor),
            contentAlignment = Alignment.Center
        ) {
            content()
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = SystemUtil.getStatusBarHeightDp() * 1.5f),
            visible = isLongPress,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            longPressHint?.invoke()
                ?: DefaultLongPressHint()
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
            settingsClick = settingsClick,
            backPressedClick = backPressedClick
        )

        // Center Play or Pause Button
        CenterButtons(
            isShowUI = localIsShowUI,
            isPlaying = localIsPlaying,
            onPlayChanged = {
                onPlayChanged(localIsPlaying.not())
            }
        )

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LottieIconLoading(
                modifier=Modifier.size(88.dp)
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
            fullscreenClick = {
                fullscreen(localIsFullscreen.not())
            },
            progressChanged = onProgressChanged,
            actionContent = actionContent
        )

        UnlockButton(
            isLockScreen = localIsLockScreen,
            isShowUnlockHint = isShowUnlockHint,
            onClick = {
                unlockScreenClick.invoke()
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
            isOrientationPortrait && (!isFullscreen || rememberHasDisplayCutout()) -> {
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
    isShowUI: Boolean,
    isFullscreen: Boolean,
    isShowRelatedList: Boolean,
    totalDuration: String,
    currentDuration: String,
    progress: Float,
    bufferProgress: Float,
    fullscreenClick: () -> Unit,
    progressChanged: (Float) -> Unit,
    actionContent: (@Composable () -> Unit)? = null
) {
    val paddingModifier = when {
        isFullscreen && isOrientationPortrait() -> {
            Modifier.padding(bottom = SystemUtil.getNavigateBarHeightDp())
        }

        isOrientationPortrait() -> Modifier
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
                Icon(
                    imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                    contentDescription = "fullscreen",
                    tint = Color.White,
                    modifier = Modifier.clickable { fullscreenClick.invoke() }
                )
            }
        }

        // progress indicator
        AnimatedVisibility(
            visible = ((isOrientationPortrait() && isFullscreen.not()) || (isFullscreen && isShowUI)) && !isShowRelatedList,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            VideoProgressIndicator(
                isShowThumb = isShowUI,
                progress = progress,
                bufferProgress = bufferProgress,
                onProgressChanged = {
                    progressChanged.invoke(it)
                }
            )
        }

        AnimatedVisibility(
            visible = isFullscreen && isShowUI && !isShowRelatedList,
            enter = if (isFullscreen) fadeIn() else fadeIn() + expandVertically(),
            exit = if (isFullscreen) fadeOut() else fadeOut() + shrinkVertically()
        ) {
            actionContent?.invoke()
                ?: DefaultBottomBarAction()
        }
    }
}

@Composable
private fun DefaultLongPressHint() {
    Surface(
        color = Color.Black.copy(alpha = 0.5f),
        contentColor = Color.White,
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
        ) {
            Text(
                text = stringResource(R.string.str_double_speed_hint),
                style = MaterialTheme.typography.labelMedium
            )

            LottieIconSpeed(
                modifier = Modifier
                    .size(20.dp)
                    .rotate(180f)
            )
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



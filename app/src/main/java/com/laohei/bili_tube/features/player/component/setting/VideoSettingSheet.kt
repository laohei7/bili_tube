package com.laohei.bili_tube.features.player.component.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SlowMotionVideo
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.features.player.state.screen_v2.ScreenEvent
import com.laohei.bili_tube.ui.component.icons.SleepTimer
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VideoSettingSheet(
    isShowSheet: Boolean = true,
    quality: String = "自动",
    speed: Float = 1.0f,
    sleepTimer: String = "关闭",
    handleScreenEvent: (ScreenEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    fun closeSheet() {
        scope.launch {
            sheetState.hide()
            onDismiss.invoke()
        }
    }
    if (isShowSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(8.dp)
                .navigationBarsPadding(),
            containerColor = MaterialTheme.colorScheme.background,
            onDismissRequest = { closeSheet() }
        ) {
            Column(
                modifier = Modifier.verticalScroll(
                    state = rememberScrollState()
                )
            ) {
                ListItem(
                    modifier = Modifier.clickable {
                        handleScreenEvent(ScreenEvent.QualitySettingsVisibility(true))
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Tune,
                            contentDescription = Icons.Outlined.Tune.name,
                        )
                    },
                    headlineContent = { Text(text = stringResource(R.string.str_quality)) },
                    trailingContent = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = quality)
                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = Icons.Outlined.ChevronRight.name,
                            )
                        }
                    }
                )
                ListItem(
                    modifier = Modifier.clickable {
                        handleScreenEvent(ScreenEvent.SpeedSettingsVisibility(true))
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.SlowMotionVideo,
                            contentDescription = Icons.Outlined.SlowMotionVideo.name,
                        )
                    },
                    headlineContent = { Text(text = stringResource(R.string.str_play_speed)) },
                    trailingContent = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = "${speed}x")
                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = Icons.Outlined.ChevronRight.name,
                            )
                        }
                    }
                )
                ListItem(
                    modifier = Modifier.clickable {
//                        onScreenAction.invoke(ScreenAction.SetLockScreen(true))
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = Icons.Outlined.Lock.name,
                        )
                    },
                    headlineContent = { Text(text = stringResource(R.string.str_screen_lock)) },
                )
                ListItem(
                    modifier = Modifier.clickable {
                        scope.launch {
                            EventBus.send(Event.AppEvent.ToastTextEvent(context.getString(R.string.str_under_development)))
                        }
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.SleepTimer,
                            contentDescription = Icons.Outlined.SleepTimer.name,
                        )
                    },
                    headlineContent = { Text(text = stringResource(R.string.str_sleep_timer)) },
                    trailingContent = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = sleepTimer)
                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = Icons.Outlined.ChevronRight.name,
                            )
                        }
                    }
                )
                ListItem(
                    modifier = Modifier.clickable {
                        handleScreenEvent(ScreenEvent.OtherSettingsVisibility(true))
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = Icons.Outlined.Settings.name,
                        )
                    },
                    headlineContent = { Text(text = stringResource(R.string.str_other_settings)) },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = Icons.Outlined.ChevronRight.name,
                        )
                    }
                )
            }
        }
    }
}
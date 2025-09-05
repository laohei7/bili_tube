package com.laohei.bili_tube.features.setting

import android.os.Environment
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CallMerge
import androidx.compose.material.icons.automirrored.outlined.Input
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Hd
import androidx.compose.material.icons.outlined.Output
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.CarCrash
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.core.WRITE_STORAGE_PERMISSION
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.ui.component.widget.ListItemWithSwitch
import com.laohei.bili_tube.core.extension.checkedPermissions
import kotlinx.coroutines.launch

private val VideoAndAudioSettings = listOf(
    Triple(Icons.Outlined.Hd, R.string.str_video_quality, Route.Settings.VideoSetting),
    Triple(Icons.Outlined.Audiotrack, R.string.str_audio_quality, Route.Settings.AudioSetting),
    Triple(Icons.Outlined.PlayArrow, R.string.str_play, Route.Settings.PlaySetting),
)

private val DownloadSettings = listOf(
    Triple(Icons.Outlined.Folder, R.string.str_download_folder, null),
    Triple(Icons.AutoMirrored.Outlined.CallMerge, R.string.str_merge_source, null),
)

private val SharedSettings = listOf(
    Triple(Icons.Outlined.Share, R.string.str_shared_super_quality_source, null),
    Triple(Icons.Outlined.Output, R.string.str_export_shared_source, null),
    Triple(Icons.AutoMirrored.Outlined.Input, R.string.str_input_shared_source, null),
)

private val CrashSettings = listOf(
    Triple(Icons.Rounded.CarCrash, R.string.str_export_crash_doc, null),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainSettingScreen(
    state: SettingUIState,
    navigateToSettingRoute: (Route) -> Unit,
    onSettingsActionClick: (SettingAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        stickyHeader {
            ListItem(
                headlineContent = {
                    Text(
                        stringResource(R.string.str_video_audio_settings),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
        items(VideoAndAudioSettings) {
            ListItem(
                modifier = Modifier.clickable { navigateToSettingRoute(it.third) },
                leadingContent = {
                    Icon(
                        imageVector = it.first,
                        contentDescription = it.first.name
                    )
                },
                headlineContent = {
                    Text(
                        text = stringResource(it.second),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        }
        item { HorizontalDivider(color = Color.LightGray) }
        stickyHeader {
            ListItem(
                headlineContent = {
                    Text(
                        stringResource(R.string.str_download_settings),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
        items(DownloadSettings) {
            GetDownloadSettingsItem(
                item = it,
                mergeSource = state.mergeSource,
                onSettingsActionClick = onSettingsActionClick
            )
        }
        item { HorizontalDivider(color = Color.LightGray) }
        stickyHeader {
            ListItem(
                headlineContent = {
                    Text(
                        stringResource(R.string.str_shared_setting),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
        items(SharedSettings) {
            GetSharedSettingsItem(
                item = it, sharedSource = state.sharedSource,
                onSettingsActionClick = onSettingsActionClick
            )
        }
        stickyHeader {
            ListItem(
                headlineContent = {
                    Text(
                        stringResource(R.string.str_log_setting),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
        items(CrashSettings) {
            GetLogSettingsItem(
                item = it,
                exportCrashDoc = state.exportCrashDoc,
                onSettingsActionClick = onSettingsActionClick
            )
        }
    }
}

@Composable
private fun GetLogSettingsItem(
    item: Triple<ImageVector, Int, Route?>,
    exportCrashDoc: Boolean,
    onSettingsActionClick: (SettingAction) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    when (item.second) {
        R.string.str_export_crash_doc -> {
            ListItemWithSwitch(
                leadingContent = {
                    Icon(
                        imageVector = item.first,
                        contentDescription = item.first.name
                    )
                },
                headlineContent = {
                    Text(
                        text = stringResource(item.second),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                checked = exportCrashDoc,
                onCheckedChange = {
                    scope.launch {
                        if (!context.checkedPermissions(WRITE_STORAGE_PERMISSION)) {
                            EventBus.send(
                                Event.AppEvent.PermissionRequestEvent(WRITE_STORAGE_PERMISSION)
                            )
                        } else {
                            onSettingsActionClick.invoke(SettingAction.ExportCrashDoc(it))
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun GetSharedSettingsItem(
    item: Triple<ImageVector, Int, Route?>,
    sharedSource: Boolean,
    onSettingsActionClick: (SettingAction) -> Unit
) {
    when (item.second) {
        R.string.str_shared_super_quality_source -> {
            ListItemWithSwitch(
                leadingContent = {
                    Icon(
                        imageVector = item.first,
                        contentDescription = item.first.name
                    )
                },
                headlineContent = {
                    Text(
                        text = stringResource(item.second),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                checked = sharedSource,
                onCheckedChange = {
                    onSettingsActionClick.invoke(SettingAction.SharedSourceAction(it))
                }
            )
        }

        else -> {
            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = item.first,
                        contentDescription = item.first.name
                    )
                },
                headlineContent = {
                    Text(text = stringResource(item.second))
                },
                trailingContent = {
                    TextButton(onClick = {}) {
                        Text(
                            text = when (item.second) {
                                R.string.str_export_shared_source -> stringResource(R.string.str_export)
                                else -> stringResource(R.string.str_import)
                            }
                        )
                    }
                }
            )
        }
    }
}


@Composable
private fun GetDownloadSettingsItem(
    item: Triple<ImageVector, Int, Route?>,
    mergeSource: Boolean,
    onSettingsActionClick: (SettingAction) -> Unit
) {
    when (item.second) {
        R.string.str_merge_source -> {
            ListItemWithSwitch(
                leadingContent = {
                    Icon(
                        imageVector = item.first,
                        contentDescription = item.first.name
                    )
                },
                headlineContent = {
                    Text(
                        text = stringResource(item.second),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                supportingContent = {
                    Text(
                        text = stringResource(R.string.str_merge_source_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Red
                    )
                },
                checked = mergeSource,
                onCheckedChange = {
                    onSettingsActionClick.invoke(SettingAction.MergeSourceAction(it))
                }
            )
        }

        else -> {
            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = item.first,
                        contentDescription = item.first.name
                    )
                },
                headlineContent = {
                    Text(
                        text = stringResource(item.second),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                supportingContent = when {
                    item.second == R.string.str_download_folder -> {
                        {
                            Text(
                                text = buildAnnotatedString {
                                    append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath)
                                    append("/BiliTube")
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }

                    else -> null
                }
            )
        }
    }
}
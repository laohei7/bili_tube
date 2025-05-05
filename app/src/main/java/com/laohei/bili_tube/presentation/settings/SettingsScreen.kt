package com.laohei.bili_tube.presentation.settings

import android.os.Environment
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.CallMerge
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Hd
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.laohei.bili_tube.R
import com.laohei.bili_tube.component.icons.AutoSkip
import com.laohei.bili_tube.component.list.SwitchListItem
import com.laohei.bili_tube.core.AudioQualities
import com.laohei.bili_tube.core.VideoQualities
import com.laohei.bili_tube.core.VideoSuperQualities
import org.koin.androidx.compose.koinViewModel

private val VideoAndAudioSettings = listOf(
    Triple(Icons.Outlined.Hd, R.string.str_video_quality, SettingsScreenType.Video),
    Triple(Icons.Outlined.Audiotrack, R.string.str_audio_quality, SettingsScreenType.Audio),
    Triple(Icons.Outlined.PlayArrow, R.string.str_play, SettingsScreenType.Play),
)

private val DownloadSettings = listOf(
    Triple(Icons.Outlined.Folder, R.string.str_download_folder, null),
    Triple(Icons.AutoMirrored.Outlined.CallMerge, R.string.str_merge_source, null),
)

private enum class SettingsScreenType(@StringRes val title: Int) {
    Main(R.string.str_settings), Video(R.string.str_video_quality),
    Audio(R.string.str_audio_quality), Play(R.string.str_play)
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    upPress: () -> Unit
) {
    var settingType by remember { mutableStateOf(SettingsScreenType.Main) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackHandler(enabled = settingType != SettingsScreenType.Main) {
        settingType = SettingsScreenType.Main
    }

    Scaffold(
        topBar = {
            SettingsTopBar(
                title = stringResource(settingType.title)
            ) {
                when {
                    settingType != SettingsScreenType.Main -> settingType = SettingsScreenType.Main
                    else -> upPress.invoke()
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        AnimatedContent(
            targetState = settingType,
            transitionSpec = {
                expandHorizontally() togetherWith shrinkHorizontally()
            }
        ) { target ->
            when (target) {
                SettingsScreenType.Main -> {
                    MainSettings(
                        modifier = modifier,
                        mergeSource = state.mergeSource,
                        onClick = { settingType = it },
                        onSettingsActionClick = viewModel::handleSettingsAction
                    )
                }

                SettingsScreenType.Video -> {
                    VideoQualitySettings(
                        modifier = modifier,
                        mobileQuality = state.mobileNetVideoQuality,
                        wlanQuality = state.wlanVideoQuality,
                        onClick = {},
                        onSettingsActionClick = viewModel::handleSettingsAction
                    )
                }

                SettingsScreenType.Audio -> {
                    AudioQualitySettings(
                        modifier = modifier,
                        mobileQuality = state.mobileNetAudioQuality,
                        wlanQuality = state.wlanAudioQuality,
                        onClick = {},
                        onSettingsActionClick = viewModel::handleSettingsAction
                    )
                }

                SettingsScreenType.Play -> {
                    PlaySettings(
                        modifier = modifier,
                        autoSkipOpEnd = state.autoSkipOpEnd,
                        onClick = {},
                        onSettingsActionClick = viewModel::handleSettingsAction
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainSettings(
    modifier: Modifier = Modifier,
    mergeSource: Boolean,
    onSettingsActionClick: (SettingsAction) -> Unit,
    onClick: (SettingsScreenType) -> Unit
) {
    LazyColumn(
        modifier = modifier
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
                modifier = Modifier.clickable { onClick.invoke(it.third) },
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
                mergeSource = mergeSource,
                onSettingsActionClick = onSettingsActionClick
            )
        }
    }
}

@Composable
private fun GetDownloadSettingsItem(
    item: Triple<ImageVector, Int, SettingsScreenType?>,
    mergeSource: Boolean,
    onSettingsActionClick: (SettingsAction) -> Unit
) {
    when (item.second) {
        R.string.str_merge_source -> {
            SwitchListItem(
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
                    onSettingsActionClick.invoke(SettingsAction.MergeSourceAction(it))
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

@Composable
private fun VideoQualitySettings(
    modifier: Modifier = Modifier,
    mobileQuality: Int,
    wlanQuality: Int,
    onClick: (SettingsScreenType) -> Unit,
    onSettingsActionClick: (SettingsAction) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            ListItem(
                headlineContent = {
                    Text(
                        stringResource(R.string.str_video_quality_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            )
        }
        item { HorizontalDivider(color = Color.LightGray) }
        item {
            ListItem(
                headlineContent = {
                    Text(
                        stringResource(R.string.str_using_mobile_net_video_quality),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            )
        }
        items(VideoQualities) {
            ListItem(
                modifier = Modifier.clickable {
                    onSettingsActionClick.invoke(
                        SettingsAction.ChangeVideoQuality(NetworkType.Mobile, it.first)
                    )
                },
                headlineContent = {
                    Text(
                        text = it.second,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                supportingContent = when {
                    it.first == Int.MAX_VALUE -> {
                        {
                            Text(
                                text = buildAnnotatedString {
                                    VideoSuperQualities.fastForEachIndexed { index, item ->
                                        append(item.second)
                                        if (index < VideoSuperQualities.size - 1) {
                                            append(" > ")
                                        }
                                    }
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }

                    else -> null
                },
                trailingContent = {
                    RadioButton(
                        selected = it.first == mobileQuality,
                        onClick = {
                            onSettingsActionClick.invoke(
                                SettingsAction.ChangeVideoQuality(NetworkType.Mobile, it.first)
                            )
                        }
                    )
                }
            )
        }
        item { HorizontalDivider(color = Color.LightGray) }
        item {
            ListItem(
                headlineContent = {
                    Text(
                        stringResource(R.string.str_using_wifi_video_quality),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            )
        }
        items(VideoQualities) {
            ListItem(
                modifier = Modifier.clickable {
                    onSettingsActionClick.invoke(
                        SettingsAction.ChangeVideoQuality(NetworkType.Wlan, it.first)
                    )
                },
                headlineContent = {
                    Text(
                        text = it.second,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                supportingContent = when {
                    it.first == Int.MAX_VALUE -> {
                        {
                            Text(
                                text = buildAnnotatedString {
                                    VideoSuperQualities.fastForEachIndexed { index, item ->
                                        append(item.second)
                                        if (index < VideoSuperQualities.size - 1) {
                                            append(" > ")
                                        }
                                    }
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }

                    else -> null
                },
                trailingContent = {
                    RadioButton(
                        selected = wlanQuality == it.first,
                        onClick = {
                            onSettingsActionClick.invoke(
                                SettingsAction.ChangeVideoQuality(NetworkType.Wlan, it.first)
                            )
                        }
                    )
                }
            )
        }
    }
}


@Composable
private fun AudioQualitySettings(
    modifier: Modifier = Modifier,
    mobileQuality: Int,
    wlanQuality: Int,
    onClick: (SettingsScreenType) -> Unit,
    onSettingsActionClick: (SettingsAction) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            ListItem(
                headlineContent = {
                    Text(
                        stringResource(R.string.str_audio_quality_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            )
        }
        item { HorizontalDivider(color = Color.LightGray) }
        item {
            ListItem(
                headlineContent = {
                    Text(
                        stringResource(R.string.str_using_mobile_net_audio_quality),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            )
        }
        items(AudioQualities) {
            ListItem(
                modifier = Modifier.clickable {
                    onSettingsActionClick.invoke(
                        SettingsAction.ChangeAudioQuality(NetworkType.Mobile, it.first)
                    )
                },
                headlineContent = {
                    Text(
                        text = it.second,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                trailingContent = {
                    RadioButton(
                        selected = mobileQuality == it.first,
                        onClick = {
                            onSettingsActionClick.invoke(
                                SettingsAction.ChangeAudioQuality(NetworkType.Mobile, it.first)
                            )
                        }
                    )
                }
            )
        }
        item { HorizontalDivider(color = Color.LightGray) }
        item {
            ListItem(
                headlineContent = {
                    Text(
                        stringResource(R.string.str_using_wifi_audio_quality),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            )
        }
        items(AudioQualities) {
            ListItem(
                modifier = Modifier.clickable {
                    onSettingsActionClick.invoke(
                        SettingsAction.ChangeAudioQuality(NetworkType.Wlan, it.first)
                    )
                },
                headlineContent = {
                    Text(
                        text = it.second,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                trailingContent = {
                    RadioButton(
                        selected = wlanQuality == it.first,
                        onClick = {
                            onSettingsActionClick.invoke(
                                SettingsAction.ChangeAudioQuality(NetworkType.Wlan, it.first)
                            )
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun PlaySettings(
    modifier: Modifier = Modifier,
    autoSkipOpEnd: Boolean,
    onClick: (SettingsScreenType) -> Unit,
    onSettingsActionClick: (SettingsAction) -> Unit
) {

    LazyColumn(
        modifier = modifier
    ) {
        item {
            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.AutoSkip,
                        contentDescription = Icons.Outlined.AutoSkip.name,
                    )
                },
                headlineContent = { Text(text = stringResource(R.string.str_auto_skip_op_end)) },
                trailingContent = {
                    Switch(
                        checked = autoSkipOpEnd,
                        onCheckedChange = {
                            onSettingsActionClick.invoke(
                                SettingsAction.AutoSkipAction(it)
                            )
                        }
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(
    title: String,
    onNavigationClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = Icons.AutoMirrored.Outlined.ArrowBack.name,
                )
            }
        },
        title = {
            AnimatedContent(
                targetState = title,
                transitionSpec = {
                    expandHorizontally() togetherWith shrinkHorizontally()
                }
            ) { target ->
                Text(text = target)
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(upPress = {})
}
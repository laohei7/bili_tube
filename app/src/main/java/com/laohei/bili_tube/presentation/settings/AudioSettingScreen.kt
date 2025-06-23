package com.laohei.bili_tube.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.AudioQualities


@Composable
fun AudioSettingScreen(
    mobileQuality: Int,
    wlanQuality: Int,
    onSettingsActionClick: (SettingsAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
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
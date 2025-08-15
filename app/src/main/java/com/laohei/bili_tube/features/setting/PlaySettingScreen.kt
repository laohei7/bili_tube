package com.laohei.bili_tube.features.setting

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.component.icons.AutoSkip


@Composable
fun PlaySettingScreen(
    autoSkipOpEnd: Boolean,
    onSettingsActionClick: (SettingAction) -> Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize()
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
                                SettingAction.AutoSkipAction(it)
                            )
                        }
                    )
                }
            )
        }
    }
}
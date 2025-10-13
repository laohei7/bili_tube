package com.laohei.bili_tube.features.player.component.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.action.VideoSettingAction
import com.laohei.bili_tube.ui.component.icons.AutoSkip
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun OtherSettingsSheet(
    isShowSheet: Boolean = true,
    autoSkip: Boolean = false,
    onDismiss: () -> Unit = {},
    videoSettingActionClick: (VideoSettingAction) -> Unit = {}
) {
    if (!isShowSheet) return
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var localAutoSkip by remember(autoSkip) { mutableStateOf(autoSkip) }

    fun closeSheet() {
        scope.launch {
            sheetState.hide()
            onDismiss.invoke()
        }
    }
    ModalBottomSheet(
        sheetState = sheetState,
        shape = MaterialTheme.shapes.medium,
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
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.AutoSkip,
                        contentDescription = Icons.Outlined.AutoSkip.name,
                    )
                },
                headlineContent = { Text(text = stringResource(R.string.str_auto_skip_op_end)) },
                trailingContent = {
                    Switch(
                        checked = localAutoSkip,
                        onCheckedChange = {
                            videoSettingActionClick.invoke(
                                VideoSettingAction.AutoSkip(it)
                            )
                        }
                    )
                }
            )
        }
    }
}
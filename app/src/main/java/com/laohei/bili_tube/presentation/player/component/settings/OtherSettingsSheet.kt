package com.laohei.bili_tube.presentation.player.component.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.R
import com.laohei.bili_tube.component.icons.AutoSkip
import com.laohei.bili_tube.component.sheet.ModalBottomSheet
import com.laohei.bili_tube.component.sheet.ModalBottomSheetProperties
import com.laohei.bili_tube.component.sheet.rememberModalBottomSheet
import com.laohei.bili_tube.component.video.VideoAction
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun OtherSettingsSheet(
    isShowSheet: Boolean = true,
    autoSkip: Boolean = false,
    onDismiss: () -> Unit = {},
    videoSettingActionClick: (VideoAction.VideoSettingAction) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheet(skipPartiallyExpanded = true)
    var localAutoSkip by remember { mutableStateOf(autoSkip) }

    LaunchedEffect(autoSkip) { localAutoSkip = autoSkip }

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
            properties = ModalBottomSheetProperties(shouldDispatcherEvent = false),
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
                                    VideoAction.VideoSettingAction.AutoSkipAction(it)
                                )
                            }
                        )
                    }
                )
            }
        }
    }
}
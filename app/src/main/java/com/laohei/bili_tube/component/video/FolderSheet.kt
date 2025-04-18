package com.laohei.bili_tube.component.video

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.laohei.bili_sdk.module_v2.folder.FolderSimpleItem
import com.laohei.bili_tube.R
import com.laohei.bili_tube.component.sheet.ModalBottomSheet
import com.laohei.bili_tube.component.sheet.ModalBottomSheetProperties
import com.laohei.bili_tube.component.sheet.rememberModalBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FolderSheet(
    isShowSheet: Boolean = true,
    folders: List<FolderSimpleItem>,
    onDismiss: () -> Unit = {},
    onClick: (VideoAction.VideoMenuAction) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheet(skipPartiallyExpanded = true)
    val checkedAidSet = remember { mutableStateListOf<Long>() }
    val deletedAidSet = remember { mutableStateListOf<Long>() }
    fun closeSheet() {
        scope.launch {
            sheetState.hide()
            onDismiss.invoke()
        }
    }

    LaunchedEffect(folders) {
        val list = folders.filter { it.favState == 1 }.map { it.id }
        checkedAidSet.clear()
        checkedAidSet.addAll(list)
        Log.d("PlayerViewModel", "FolderSheet1: $list")
        Log.d("PlayerViewModel", "FolderSheet2: $checkedAidSet")
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
                modifier = Modifier.fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.str_save_to),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    trailingContent = {
                        TextButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = Icons.Outlined.Add.name,
                            )
                            Text(
                                text = stringResource(R.string.str_new_playlist),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                )
                folders.fastForEach {
                    GetFolderCheckItem(
                        label = it.title,
                        checked = checkedAidSet.contains(it.id),
                        onClick = { checked ->
                            if (checked) {
                                checkedAidSet.add(it.id)
                            } else {
                                checkedAidSet.remove(it.id)
                                if (it.favState == 1) {
                                    deletedAidSet.add(it.id)
                                }
                            }
                        }
                    )
                }
                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(0.6f),
                    onClick = {
                        scope.launch {
                            val alreadyAdd =
                                folders.filter { it.favState == 1 }.map { it.id }.toSet()
                            val needAdd =
                                checkedAidSet.filter { alreadyAdd.contains(it).not() }.toSet()
                            onClick.invoke(
                                VideoAction.VideoMenuAction.CollectAction(
                                    addAids = needAdd,
                                    delAids = deletedAidSet.toSet()
                                )
                            )
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.str_ok))
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }

}

@Composable
private fun GetFolderCheckItem(
    label: String,
    checked: Boolean,
    onClick: (Boolean) -> Unit
) {
    var localChecked by remember { mutableStateOf(checked) }
    LaunchedEffect(checked) {
        localChecked = checked
    }
    ListItem(
        modifier = Modifier.clickable { onClick.invoke(localChecked.not()) },
        leadingContent = {
            Checkbox(
                checked = localChecked,
                onCheckedChange = { onClick.invoke(it) }
            )
        },
        headlineContent = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    )
}
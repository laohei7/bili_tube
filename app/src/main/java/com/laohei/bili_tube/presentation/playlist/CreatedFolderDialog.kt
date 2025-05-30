package com.laohei.bili_tube.presentation.playlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun CreatedFolderDialog(
    isShowDialog: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isShowDialog) {
        Dialog(
            onDismissRequest = { onDismiss.invoke() }
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                ListItem(
                    headlineContent = {
                        Text(text = "创建收藏夹")
                    },
                    trailingContent = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = Icons.Outlined.Close.name,
                            )
                        }
                    }
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedSuffixColor = MaterialTheme.colorScheme.surfaceContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        focusedBorderColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceContainer,
                        disabledBorderColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                    shape = CircleShape,
                    placeholder = {
                        Text(text = "输入收藏夹名称")
                    }
                )

                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Switch(
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                    )
                    Text(
                        text = "是否为私有？",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = onSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 32.dp, bottom = 16.dp)
                ) {
                    Text(text = "提交")
                }
            }
        }
    }
}


@Preview
@Composable
private fun CreatedFolderDialogPreview() {
    var value by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(false) }
    CreatedFolderDialog(
        isShowDialog = true,
        value = value,
        onValueChange = { value = it },
        checked = checked,
        onCheckedChange = { checked = it },
        onSubmit = {},
        onDismiss = {}
    )
}
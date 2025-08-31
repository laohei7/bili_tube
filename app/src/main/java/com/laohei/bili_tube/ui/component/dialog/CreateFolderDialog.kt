package com.laohei.bili_tube.ui.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.theme.PaddingLg
import com.laohei.bili_tube.ui.theme.PaddingSm

@Composable
fun CreateFolderDialog(
    isVisible: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        focusedBorderColor = MaterialTheme.colorScheme.outline,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
    )

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
                    Text(text = stringResource(R.string.str_create_folder))
                },
                trailingContent = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = Icons.Rounded.Close.name,
                        )
                    }
                }
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PaddingLg),
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.bodyMedium,
                singleLine = true,
                colors = textFieldColors,
                shape = CircleShape,
                placeholder = {
                    Text(
                        text = stringResource(R.string.str_input_folder_name),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = PaddingLg)
                    .padding(top = PaddingSm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PaddingSm)
            ) {
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                )
                Text(
                    text = stringResource(R.string.str_is_pravite),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PaddingLg)
                    .padding(top = PaddingLg * 2, bottom = PaddingLg)
            ) {
                Text(text = stringResource(R.string.str_submit))
            }
        }
    }
}


@Preview
@Composable
private fun CreateFolderDialogPreview() {
    var value by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(false) }
    CreateFolderDialog(
        isVisible = true,
        value = value,
        onValueChange = { value = it },
        checked = checked,
        onCheckedChange = { checked = it },
        onSubmit = {},
        onDismiss = {}
    )
}
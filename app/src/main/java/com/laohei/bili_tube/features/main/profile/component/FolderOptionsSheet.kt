package com.laohei.bili_tube.features.main.profile.component

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.fastForEach
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.theme.CornerRadiusMd
import com.laohei.bili_tube.ui.theme.PaddingSm

private data class FolderOption(
    val icon: ImageVector,
    @StringRes val labelId: Int,
    val color: Color? = null
)

@Composable
private fun buildFolderOptions(): List<FolderOption> {
    val colorScheme = MaterialTheme.colorScheme
    return remember {
        listOf(
            FolderOption(
                icon = Icons.Rounded.Delete,
                labelId = R.string.str_delete,
                color = colorScheme.error
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FolderOptionsSheet(
    isSheetVisible: Boolean,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    onItemClick: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    if (isSheetVisible.not()) return
    val folderOptions = buildFolderOptions()
    val defaultColors = ListItemDefaults.colors()
    ModalBottomSheet(
        modifier = Modifier
            .padding(PaddingSm)
            .navigationBarsPadding(),
        shape = RoundedCornerShape(CornerRadiusMd),
        onDismissRequest = onDismissRequest
    ) {
        folderOptions.fastForEach { option ->
            ListItem(
                modifier = Modifier.clickable {
                    onItemClick(option.labelId)
                },
                colors = ListItemDefaults.colors(
                    containerColor = BottomSheetDefaults.ContainerColor,
                    leadingIconColor = option.color ?: defaultColors.leadingIconColor,
                    headlineColor = option.color ?: defaultColors.headlineColor
                ),
                leadingContent = {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = option.icon.name
                    )
                },
                headlineContent = {
                    Text(
                        text = stringResource(option.labelId),
                        style = style
                    )
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun FolderOptionsSheetPreview() {
    FolderOptionsSheet(
        isSheetVisible = true,
        onDismissRequest = {},
        onItemClick = {}
    )
}
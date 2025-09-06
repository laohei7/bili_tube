package com.laohei.bili_tube.features.main.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.laohei.bili_tube.R

@Composable
private fun listItemBackgroundColor(): ListItemColors {
    return ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.background
    )
}

private val videoMenus = listOf(
    Pair(R.string.str_save_watch_later, Icons.Rounded.History),
    Pair(R.string.str_save_playlist, Icons.Rounded.BookmarkBorder),
    Pair(R.string.str_shared, Icons.Rounded.IosShare),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VideoMenuSheet(
    isShowSheet: Boolean,
    onDismiss: () -> Unit,
    onClick: (Int) -> Unit
) {
    if (isShowSheet.not()) {
        return
    }
    ModalBottomSheet(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(8.dp)
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = { onDismiss.invoke() }
    ) {
        videoMenus.fastForEach {
            ListItem(
                modifier = Modifier.clickable {
                    onClick(it.first)
                },
                colors = listItemBackgroundColor(),
                headlineContent = {
                    Text(
                        text = stringResource(it.first)
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = it.second,
                        contentDescription = it.second.name
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VideoMenuSheetPreview() {
    VideoMenuSheet(
        isShowSheet = true,
        onDismiss = {}
    ) {

    }
}
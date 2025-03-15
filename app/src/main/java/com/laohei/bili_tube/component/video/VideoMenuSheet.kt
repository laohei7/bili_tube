package com.laohei.bili_tube.component.video

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.R
import com.laohei.bili_tube.component.color.sheetListItemColors

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun VideoMenuSheet(
    isShowSheet: Boolean = true,
    onDismiss: () -> Unit = {}
) {
    if (isShowSheet) {
        ModalBottomSheet(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(8.dp)
                .navigationBarsPadding(),
            containerColor = MaterialTheme.colorScheme.background,
            onDismissRequest = { onDismiss.invoke() }
        ) {
            ListItem(
                modifier = Modifier.clickable { },
                colors = sheetListItemColors(),
                headlineContent = {
                    Text(
                        text = stringResource(R.string.str_save_watch_later)
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = Icons.Outlined.History.name
                    )
                }
            )
            ListItem(
                modifier = Modifier.clickable { },
                colors = sheetListItemColors(),
                headlineContent = {
                    Text(
                        text = stringResource(R.string.str_save_playlist)
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = Icons.Outlined.BookmarkBorder.name
                    )
                }
            )
            ListItem(
                modifier = Modifier.clickable { },
                colors = sheetListItemColors(),
                headlineContent = {
                    Text(
                        text = stringResource(R.string.str_shared)
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = Icons.Outlined.Share.name
                    )
                }
            )
            ListItem(
                modifier = Modifier.clickable { },
                colors = sheetListItemColors(),
                headlineContent = {
                    Text(
                        text = stringResource(R.string.str_no_interesting)
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Block,
                        contentDescription = Icons.Outlined.Block.name
                    )
                }
            )
            ListItem(
                modifier = Modifier.clickable { },
                colors = sheetListItemColors(),
                headlineContent = {
                    Text(
                        text = stringResource(R.string.str_report)
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Flag,
                        contentDescription = Icons.Outlined.Flag.name
                    )
                }
            )
        }
    }
}
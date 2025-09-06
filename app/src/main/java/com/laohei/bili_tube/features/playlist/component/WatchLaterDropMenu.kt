package com.laohei.bili_tube.features.playlist.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.laohei.bili_tube.R
import com.laohei.bili_tube.features.playlist.PlaylistContentAction

@Composable
internal fun WatchLaterMenuButton(
    onPlaylistContentAction: (PlaylistContentAction) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = Icons.Rounded.MoreVert.name
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.str_delete_all_viewd))
                },
                onClick = {
                    onPlaylistContentAction(PlaylistContentAction.DelToView(viewed = true))
                }
            )
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.str_clear_to_view))
                },
                onClick = {
                    onPlaylistContentAction(PlaylistContentAction.ClearToView)
                }
            )
        }
    }
}
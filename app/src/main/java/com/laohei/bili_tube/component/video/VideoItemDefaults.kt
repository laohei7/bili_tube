package com.laohei.bili_tube.component.video

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DragHandle
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

object VideoItemDefaults {

    @Composable
    fun DefaultLeadingIcon() {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = Icons.Outlined.DragHandle,
            contentDescription = Icons.Outlined.DragHandle.name
        )
    }

    @Composable
    fun DefaultTrailingIcon(onClick: (() -> Unit)? = null) {
        IconButton(
            onClick = { onClick?.invoke() },
            modifier = Modifier
                .offset {
                    IntOffset(60, -30)
                }
        ) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = Icons.Outlined.MoreVert.name,
                modifier = Modifier
                    .size(16.dp)
            )
        }
    }

}
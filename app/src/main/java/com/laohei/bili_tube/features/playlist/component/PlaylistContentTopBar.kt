package com.laohei.bili_tube.features.playlist.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlaylistContentTopBar(
    upPress: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = upPress
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = Icons.AutoMirrored.Rounded.ArrowBack.name
                )
            }
        },
        title = {}
    )
}
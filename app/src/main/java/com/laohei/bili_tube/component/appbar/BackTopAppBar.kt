package com.laohei.bili_tube.component.appbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackTopAppBar(
    backup: () -> Unit,
    search: (() -> Unit)? = null
) {
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = backup
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = Icons.AutoMirrored.Outlined.ArrowBack.name,
                )
            }
        },
        title = {},
        actions = {
            search?.let {
                IconButton(
                    onClick = it,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = Icons.Outlined.Search.name
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun BackTopAppBarPreview() {
    BackTopAppBar(backup = {}, search = {})
}
package com.laohei.bili_tube.features.setting

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingTopBar(
    title: String,
    upPress: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = upPress) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = Icons.AutoMirrored.Outlined.ArrowBack.name,
                )
            }
        },
        title = {
            Text(text = title)
        }
    )
}
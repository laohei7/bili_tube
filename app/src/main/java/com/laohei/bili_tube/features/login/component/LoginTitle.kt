package com.laohei.bili_tube.features.login.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.layoutId


@Composable
internal fun LoginTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.layoutId("title"),
        style = MaterialTheme.typography.displayMedium,
        color = MaterialTheme.colorScheme.onPrimary
    )
}
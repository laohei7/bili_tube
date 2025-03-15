package com.laohei.bili_tube.component.color

import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
internal fun sheetListItemColors(): ListItemColors {
    return ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.background
    )
}

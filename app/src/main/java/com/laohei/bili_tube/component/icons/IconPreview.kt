package com.laohei.bili_tube.component.icons

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.fastForEach

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true)
@Composable
private fun IconPreview() {
    val icons = remember {
        listOf(Icons.Outlined.SleepTimer, Icons.Outlined.AutoSkip)
    }
    FlowRow {
        icons.fastForEach {
            Icon(
                imageVector = it,
                contentDescription = it.name
            )
        }
    }
}
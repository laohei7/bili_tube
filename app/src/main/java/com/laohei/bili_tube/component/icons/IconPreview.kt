package com.laohei.bili_tube.component.icons

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.fastForEach
import com.laohei.bili_tube.R

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true)
@Composable
private fun IconPreview() {
    val icons = remember {
        listOf(
            Icons.Outlined.SleepTimer, Icons.Outlined.AutoSkip,
        )
    }
    val levelIcons = remember {
        listOf(
            Pair(
                Icons.Outlined.Level0,
                R.color.level0
            ),
            Pair(
                Icons.Outlined.Level1,
                R.color.level1
            ),
            Pair(
                Icons.Outlined.Level2,
                R.color.level2
            ),
            Pair(
                Icons.Outlined.Level3,
                R.color.level3
            ),
            Pair(
                Icons.Outlined.Level4,
                R.color.level4
            ),
            Pair(
                Icons.Outlined.Level5,
                R.color.level5
            ),
            Pair(
                Icons.Outlined.Level6,
                R.color.level6
            )
        )
    }
    FlowRow {
        icons.fastForEach {
            Icon(
                imageVector = it,
                contentDescription = it.name
            )
        }
        levelIcons.fastForEach {
            Icon(
                imageVector = it.first,
                contentDescription = it.first.name,
                tint = colorResource(it.second)
            )
        }
    }
}
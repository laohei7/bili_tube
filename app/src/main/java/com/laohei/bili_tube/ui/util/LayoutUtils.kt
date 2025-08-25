package com.laohei.bili_tube.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class GridColumnRule(
    val maxWidth: Dp,
    val columnCount: Int
)

@Composable
fun rememberGridColumnCount(
    width: Dp,
    rules: List<GridColumnRule>
): Int = remember(width, rules) {
    rules.firstOrNull { width < it.maxWidth }?.columnCount ?: rules.last().columnCount
}

val HorizontalItemRules = listOf(
    GridColumnRule(500.dp, 1),
    GridColumnRule(1280.dp, 2),
    GridColumnRule(Dp.Infinity, 3)
)


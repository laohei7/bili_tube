package com.laohei.bili_tube.ui.component.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.laohei.bili_tube.ui.theme.PaddingLg
import com.laohei.bili_tube.ui.theme.PaddingSm

@Composable
fun ChipTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    selectedColors: ChipColors = AssistChipDefaults.assistChipColors(
        containerColor = MaterialTheme.colorScheme.primary,
        labelColor = MaterialTheme.colorScheme.onPrimary
    ),
    unselectedColors: ChipColors = AssistChipDefaults.assistChipColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        labelColor = MaterialTheme.colorScheme.onBackground
    )
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PaddingLg)
    ) {
        Spacer(Modifier)
        tabs.fastForEachIndexed { index, tab ->
            AssistChip(
                onClick = { onTabClick(index) },
                shape = RoundedCornerShape(PaddingSm),
                colors = if (selectedTabIndex == index) selectedColors else unselectedColors,
                border = BorderStroke(0.dp, Color.Transparent),
                label = { Text(text = tab) }
            )
        }
        Spacer(Modifier)
    }
}
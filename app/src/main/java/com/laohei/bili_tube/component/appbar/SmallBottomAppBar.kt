package com.laohei.bili_tube.component.appbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed

@Stable
data class BottomAppBarItem(
    val icon: ImageVector,
    val label: String? = null
)

@Composable
fun SmallBottomAppBar(
    modifier: Modifier = Modifier,
    items: List<BottomAppBarItem>,
    selectedIndex: Int = 0,
    onClick: (Int) -> Unit = { _ -> }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items.fastForEachIndexed { index, item ->
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                onClick = { onClick.invoke(index) },
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.98f),
                contentColor = when {
                    selectedIndex == index -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.secondary
                }
            ) {
                VerticalIconAndLabelItem(item)
            }
        }
    }
}

@Composable
private fun VerticalIconAndLabelItem(item: BottomAppBarItem) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterVertically)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label ?: item.icon.name
        )
        item.label?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall
            )
        }

    }
}

@Preview
@Composable
private fun SmallBottomAppBarPreview() {
    val items = remember {
        listOf(
            BottomAppBarItem(
                icon = Icons.Outlined.Home,
                label = "首页"
            ),
            BottomAppBarItem(
                icon = Icons.Outlined.Person,
                label = "我的"
            )
        )
    }
    SmallBottomAppBar(items = items)
}
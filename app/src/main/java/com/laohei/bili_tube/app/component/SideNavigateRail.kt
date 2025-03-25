package com.laohei.bili_tube.app.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.util.fastForEachIndexed
import com.laohei.bili_tube.component.appbar.BottomAppBarItem

@Composable
internal fun SideNavigateRail(
    items: List<BottomAppBarItem>,
    selectedIndex: Int,
    onClick: (Int) -> Unit = {}
) {
    NavigationRail {
        items.fastForEachIndexed { index, item ->
            NavigationRailItem(
                selected = selectedIndex == index,
                onClick = {onClick.invoke(index)},
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.icon.name
                    )
                },
                label = item.label?.run {
                    {
                        Text(text = item.label)
                    }
                }
            )
        }
    }
}
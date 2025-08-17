package com.laohei.bili_tube.features.main.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEachIndexed
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import com.laohei.bili_tube.features.main.navigation.MainRoute

@Composable
internal fun SideNavigateRail(
    items: List<BottomAppBarItem>,
    currentDestination: NavBackStackEntry?,
    onClick: (MainRoute) -> Unit = {}
) {
    NavigationRail {
        items.fastForEachIndexed { index, item ->
            val selected = currentDestination?.destination?.hasRoute(item.route::class) == true
            if (item.icon == Icons.Rounded.Settings) {
                Spacer(Modifier.weight(1f))
            }
            NavigationRailItem(
                selected = selected,
                onClick = { onClick.invoke(item.route) },
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
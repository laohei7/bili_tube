package com.laohei.bili_tube.features.main.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEachIndexed
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import com.laohei.bili_tube.features.main.navigation.MainRoute
import com.laohei.bili_tube.ui.theme.SmallPadding

@Composable
internal fun SideNavigateDrawer(
    items: List<BottomAppBarItem>,
    currentDestination:  NavBackStackEntry?,
    onClick: (MainRoute) -> Unit = {}
) {
    ModalDrawerSheet {
        items.fastForEachIndexed { index, item ->
            val selected = currentDestination?.destination?.hasRoute(item.route::class) == true
            if (item.icon == Icons.Rounded.Settings) {
                Spacer(Modifier.weight(1f))
            }
            NavigationDrawerItem(
                modifier = Modifier.padding(horizontal = SmallPadding),
                shape = RoundedCornerShape(SmallPadding),
                label = { Text(text = item.label ?: "") },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.icon.name
                    )
                },
                selected = selected,
                onClick = { onClick(item.route) }
            )
        }
    }
}


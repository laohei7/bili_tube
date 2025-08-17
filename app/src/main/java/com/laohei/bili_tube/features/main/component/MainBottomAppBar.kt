package com.laohei.bili_tube.features.main.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.laohei.bili_tube.R
import com.laohei.bili_tube.features.main.navigation.MainRoute

@Stable
internal data class BottomAppBarItem(
    val icon: ImageVector,
    val label: String? = null,
    val route: MainRoute
)

@Composable
internal fun MainBottomAppBar(
    modifier: Modifier = Modifier,
    items: List<BottomAppBarItem>,
    currentDestination:  NavBackStackEntry?,
    onClick: (MainRoute) -> Unit = { _ -> }
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items.fastForEachIndexed { index, item ->
            val selected = currentDestination?.destination?.hasRoute(item.route::class) == true
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                onClick = { onClick.invoke(item.route) },
                color = Color.Transparent,
                contentColor = when {
                    selected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.inversePrimary
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
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label ?: item.icon.name,
            modifier = Modifier.size(22.dp)
        )
        item.label?.let {
            Text(
                text = it,
                fontSize = 10.sp
            )
        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SmallBottomAppBarPreview() {
    val items = remember {
        listOf(
            BottomAppBarItem(
                icon = Icons.Outlined.Home,
                label = "首页",
                route = MainRoute.Home
            ),
            BottomAppBarItem(
                icon = Icons.Outlined.Person,
                label = "我的",
                route = MainRoute.Profile
            )
        )
    }
    Box(
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            painter = painterResource(R.drawable.bg),
            contentDescription = "bg",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        MainBottomAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
                .navigationBarsPadding(),
            items = items,
            currentDestination = null
        )
    }

}
package com.laohei.bili_tube.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.component.SideNavigateRail
import com.laohei.bili_tube.component.appbar.BottomAppBarItem
import com.laohei.bili_tube.component.appbar.SmallBottomAppBar
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.presentation.home.HomeScreen
import com.laohei.bili_tube.presentation.mine.MineScreen
import com.laohei.bili_tube.presentation.subscription.SubscriptionScreen
import com.laohei.common_ui.layout.AdaptiveLayout
import com.laohei.common_ui.layout.DeviceConfiguration
import kotlinx.coroutines.launch

@Composable
fun MainGraph(
    appNavigateToRoute: (Route) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var bottomAppBarSelectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val bottomAppBarItems = remember {
        listOf(
            BottomAppBarItem(
                icon = Icons.Outlined.Home,
                label = context.getString(R.string.str_home)
            ),
            BottomAppBarItem(
                icon = Icons.Outlined.Subscriptions,
                label = context.getString(R.string.str_subscription)
            ),
            BottomAppBarItem(
                icon = Icons.Outlined.Person,
                label = context.getString(R.string.str_mine)
            )
        )
    }
    val mainGraphController = rememberNavController()
    val currentDestination by mainGraphController.currentBackStackEntryAsState()

    fun handleMainNavigation(index: Int) {
        val route = when (index) {
            0 -> Route.HomeGraph.Home
            1 -> Route.HomeGraph.Subscription
            else -> Route.HomeGraph.Mine
        }

        val isHomeRoute =
            index == 0 && currentDestination?.destination?.hasRoute<Route.HomeGraph.Home>() == true
        val isDynamicRoute =
            index == 1 && currentDestination?.destination?.hasRoute<Route.HomeGraph.Subscription>() == true

        when {
            isHomeRoute || isDynamicRoute -> {
                scope.launch {
                    EventBus.send(Event.NotificationChildRefresh)
                }
            }

            else -> {
                mainGraphController.navigate(route) {
                    popUpTo(Route.HomeGraph.Home) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    fun onBottomAppBarIndexChanged(index: Int) {
        bottomAppBarSelectedIndex = index
        handleMainNavigation(index)
    }

    AdaptiveLayout(
        modifier = Modifier.fillMaxSize()
    ) { uiType ->
        val layoutType = when (uiType) {
            DeviceConfiguration.TABLE_PORTRAIT,
            DeviceConfiguration.MOBILE_PORTRAIT -> NavigationSuiteType.NavigationBar

            DeviceConfiguration.MOBILE_LANDSCAPE,
            DeviceConfiguration.TABLE_LANDSCAPE -> NavigationSuiteType.NavigationRail

            DeviceConfiguration.DESKTOP -> NavigationSuiteType.NavigationDrawer
        }
        NavigationSuiteScaffoldLayout(
            navigationSuite = {
                when (layoutType) {
                    NavigationSuiteType.NavigationBar -> {
                        SmallBottomAppBar(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background.copy(0.98f))
                                .navigationBarsPadding(),
                            items = bottomAppBarItems,
                            selectedIndex = bottomAppBarSelectedIndex
                        ) { index ->
                            onBottomAppBarIndexChanged(index)
                        }
                    }

                    NavigationSuiteType.NavigationRail,
                    NavigationSuiteType.NavigationDrawer -> {
                        SideNavigateRail(
                            items = bottomAppBarItems,
                            selectedIndex = bottomAppBarSelectedIndex
                        ) { index ->
                            onBottomAppBarIndexChanged(index)
                        }
                    }
                }
            },
            layoutType = layoutType
        ) {
            NavHost(
                navController = mainGraphController,
                startDestination = Route.HomeGraph.Home,
            ) {
                composable<Route.HomeGraph.Home> {
                    HomeScreen(
                        navigateToRoute = {
                            appNavigateToRoute.invoke(it)
                        }
                    )
                }
                composable<Route.HomeGraph.Mine> {
                    MineScreen(
                        navigateToRoute = {
                            appNavigateToRoute.invoke(it)
                        }
                    )
                }
                composable<Route.HomeGraph.Subscription> {
                    SubscriptionScreen(
                        navigateToRoute = {
                            appNavigateToRoute.invoke(it)
                        }
                    )
                }
            }
        }
    }
}
package com.laohei.bili_tube.features.main

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.features.main.component.BottomAppBarItem
import com.laohei.bili_tube.features.main.component.SideNavigateRail
import com.laohei.bili_tube.features.main.component.SmallBottomAppBar
import com.laohei.bili_tube.features.main.home.HomeScreen
import com.laohei.bili_tube.features.main.navigation.MainRoute
import com.laohei.bili_tube.features.main.profile.ProfileScreen
import com.laohei.bili_tube.features.main.subscription.SubscriptionScreen
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.component.layout.DeviceConfiguration
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainNav(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    appNavigateToRoute: (AppRoute) -> Unit
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
    val mainNavController = rememberNavController()
    val currentDestination by mainNavController.currentBackStackEntryAsState()

    fun handleMainNavigation(index: Int) {
        val route = when (index) {
            0 -> MainRoute.Home
            1 -> MainRoute.Subscription
            else -> MainRoute.Profile
        }

        val isHomeRoute =
            index == 0 && currentDestination?.destination?.hasRoute<MainRoute.Home>() == true
        val isDynamicRoute =
            index == 1 && currentDestination?.destination?.hasRoute<MainRoute.Subscription>() == true

        when {
            isHomeRoute || isDynamicRoute -> {
                scope.launch {
                    EventBus.send(Event.NotificationChildRefresh)
                }
            }

            else -> {
                mainNavController.navigate(route) {
                    popUpTo(MainRoute.Home) {
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
    ) { uiType, _, _ ->
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
                                .background(MaterialTheme.colorScheme.background)
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
                navController = mainNavController,
                startDestination = MainRoute.Home,
            ) {
                composable<MainRoute.Home> {
                    HomeScreen(
                        navigateToAppRoute = {
                            appNavigateToRoute.invoke(it)
                        }
                    )
                }
                composable<MainRoute.Profile> {
                    ProfileScreen(
                        navigateToAppRoute = {
                            appNavigateToRoute.invoke(it)
                        }
                    )
                }
                composable<MainRoute.Subscription> {
                    SubscriptionScreen(
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        navigateToAppRoute = {
                            appNavigateToRoute.invoke(it)
                        }
                    )
                }
            }
        }
    }
}
package com.laohei.bili_tube.features.main

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Subscriptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.features.main.component.BottomAppBarItem
import com.laohei.bili_tube.features.main.component.LogoTopAppBar
import com.laohei.bili_tube.features.main.component.MainBottomAppBar
import com.laohei.bili_tube.features.main.component.SideNavigateDrawer
import com.laohei.bili_tube.features.main.component.SideNavigateRail
import com.laohei.bili_tube.features.main.home.HomeScreen
import com.laohei.bili_tube.features.main.navigation.MainRoute
import com.laohei.bili_tube.features.main.profile.ProfileScreen
import com.laohei.bili_tube.features.main.subscription.SubscriptionScreen
import com.laohei.bili_tube.features.setting.SettingNav
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.component.layout.DeviceConfiguration
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainNav(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    appNavigateToRoute: (AppRoute) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var bottomAppBarSelectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val bottomAppBarItems = remember {
        listOf(
            BottomAppBarItem(
                icon = Icons.Rounded.Home,
                label = context.getString(R.string.str_home),
                route = MainRoute.Home
            ),
            BottomAppBarItem(
                icon = Icons.Rounded.Subscriptions,
                label = context.getString(R.string.str_subscription),
                route = MainRoute.Subscription
            ),
            BottomAppBarItem(
                icon = Icons.Rounded.Person,
                label = context.getString(R.string.str_mine),
                route = MainRoute.Profile
            )
        )
    }
    val railItems = remember {
        listOf(
            BottomAppBarItem(
                icon = Icons.Rounded.Settings,
                label = context.getString(R.string.str_settings),
                route = MainRoute.Settings
            ),
        )
    }
    val mainNavController = rememberNavController()
    val currentDestination by mainNavController.currentBackStackEntryAsState()

    fun handleMainNavigation(route: MainRoute) {
        val isHomeRoute =
            route is MainRoute.Home && currentDestination?.destination?.hasRoute<MainRoute.Home>() == true
        val isSubscriptionRoute =
            route is MainRoute.Subscription && currentDestination?.destination?.hasRoute<MainRoute.Subscription>() == true

        when {
            isHomeRoute || isSubscriptionRoute -> {
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


    AdaptiveLayout(
        modifier = Modifier.fillMaxSize()
    ) { uiType, _, _ ->
        when (uiType) {
            DeviceConfiguration.TABLE_PORTRAIT,
            DeviceConfiguration.MOBILE_PORTRAIT -> {
                PortraitContent(
                    mainNavController = mainNavController,
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope = sharedTransitionScope,
                    items = bottomAppBarItems,
                    currentDestination = currentDestination,
                    appNavigateToRoute = appNavigateToRoute,
                    onBottomBarNavigate = ::handleMainNavigation
                )
            }

            DeviceConfiguration.MOBILE_LANDSCAPE,
            DeviceConfiguration.TABLE_LANDSCAPE -> {
                LandscapeContent(
                    mainNavController = mainNavController,
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope = sharedTransitionScope,
                    items = bottomAppBarItems + railItems,
                    currentDestination = currentDestination,
                    appNavigateToRoute = appNavigateToRoute,
                    onBottomBarNavigate = ::handleMainNavigation
                )
            }

            DeviceConfiguration.DESKTOP -> {
                DesktopContent(
                    mainNavController = mainNavController,
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope = sharedTransitionScope,
                    items = bottomAppBarItems + railItems,
                    currentDestination = currentDestination,
                    appNavigateToRoute = appNavigateToRoute,
                    onBottomBarNavigate = ::handleMainNavigation
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PortraitContent(
    mainNavController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    items: List<BottomAppBarItem>,
    currentDestination: NavBackStackEntry?,
    appNavigateToRoute: (AppRoute) -> Unit,
    onBottomBarNavigate: (MainRoute) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        MainNavHost(
            mainNavController = mainNavController,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
            appNavigateToRoute = appNavigateToRoute
        )
        MainBottomAppBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
                .navigationBarsPadding(),
            items = items,
            currentDestination = currentDestination
        ) { route ->
            onBottomBarNavigate(route)
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun LandscapeContent(
    mainNavController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    items: List<BottomAppBarItem>,
    currentDestination: NavBackStackEntry?,
    appNavigateToRoute: (AppRoute) -> Unit,
    onBottomBarNavigate: (MainRoute) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LogoTopAppBar(
            onSearchClick = { appNavigateToRoute(AppRoute.Search) }
        )
        Row {
            SideNavigateRail(
                items = items,
                currentDestination = currentDestination,
                onClick = onBottomBarNavigate
            )
            MainNavHost(
                mainNavController = mainNavController,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                appNavigateToRoute = appNavigateToRoute
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DesktopContent(
    mainNavController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    items: List<BottomAppBarItem>,
    currentDestination: NavBackStackEntry?,
    appNavigateToRoute: (AppRoute) -> Unit,
    onBottomBarNavigate: (MainRoute) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(true) }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LogoTopAppBar(
            isShowMenu = true,
            onSearchClick = { appNavigateToRoute(AppRoute.Search) },
            onMenuClick = { expanded = expanded.not() }
        )
        Row {
            when {
                expanded -> {
                    SideNavigateDrawer(
                        items = items,
                        currentDestination = currentDestination,
                        onClick = onBottomBarNavigate
                    )
                }

                else -> {
                    SideNavigateRail(
                        items = items,
                        currentDestination = currentDestination,
                        onClick = onBottomBarNavigate
                    )
                }
            }

            MainNavHost(
                mainNavController = mainNavController,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                appNavigateToRoute = appNavigateToRoute
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun MainNavHost(
    mainNavController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    appNavigateToRoute: (AppRoute) -> Unit
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
        composable<MainRoute.Settings> {
            SettingNav(
                navigateToUp = {}
            )
        }
    }
}
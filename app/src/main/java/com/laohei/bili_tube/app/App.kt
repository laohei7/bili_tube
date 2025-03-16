package com.laohei.bili_tube.app

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.laohei.bili_sdk.user.UserInfo
import com.laohei.bili_tube.R
import com.laohei.bili_tube.component.appbar.BottomAppBarItem
import com.laohei.bili_tube.component.appbar.SmallBottomAppBar
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.core.FACE_URL_KEY
import com.laohei.bili_tube.core.USERNAME_KEY
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.dataStore
import com.laohei.bili_tube.presentation.dynamic.DynamicScreen
import com.laohei.bili_tube.presentation.home.HomeScreen
import com.laohei.bili_tube.presentation.login.QRCodeLoginScreen
import com.laohei.bili_tube.presentation.mine.MineScreen
import com.laohei.bili_tube.presentation.player.PlayerScreen
import com.laohei.bili_tube.presentation.splash.SplashScreen
import com.laohei.bili_tube.presentation.subscription.SubscriptionScreen
import com.laohei.bili_tube.utill.setValue
import com.laohei.bili_tube.utill.useLightSystemBarIcon
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private const val TAG = "App"

@Composable
fun App() {
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val navController = rememberNavController()
    val currentDestination by navController.currentBackStackEntryAsState()
    var isLogin by rememberSaveable(Unit) { mutableStateOf(false) }
    var bottomAppBarSelectedIndex by remember { mutableIntStateOf(0) }
    val bottomAppBarItems = remember {
        listOf(
            BottomAppBarItem(
                icon = Icons.Outlined.Home,
                label = context.getString(R.string.str_home)
            ),
            BottomAppBarItem(
                icon = Icons.Outlined.Subscriptions,
                label = context.getString(R.string.str_scrscription)
            ),
            BottomAppBarItem(
                icon = Icons.Outlined.Person,
                label = context.getString(R.string.str_mine)
            )
        )
    }

    activity?.useLightSystemBarIcon(isSystemInDarkTheme().not())

    LaunchedEffect(isLogin) {
        if (isLogin.not()) {
            return@LaunchedEffect
        }
        val isLoginRoute = currentDestination?.destination?.hasRoute<Route.Login>() == true
        if (isLoginRoute.not()) {
            return@LaunchedEffect
        }
        navController.navigate(Route.Home) {
            if (isLoginRoute) {
                popUpTo<Route.Login> { inclusive = true }
            } else {
                popUpTo<Route.Splash> { inclusive = true }
            }
            launchSingleTop = true
        }
    }

    LaunchedEffect(Unit) {
        context.dataStore.data.map { preferences ->
            !preferences[COOKIE_KEY].isNullOrBlank()
        }.collect {
            isLogin = it
        }
    }

    GetAndCacheUserProfile(isLogin)

    ExitAppHandle()

    fun handleBottomEvent(index: Int) {
        val route = when (index) {
            0 -> Route.Home
            1 -> Route.Dynamic
            else -> Route.Mine
        }

        val isHomeRoute =
            index == 0 && currentDestination?.destination?.hasRoute<Route.Home>() == true
        val isDynamicRoute =
            index == 1 && currentDestination?.destination?.hasRoute<Route.Dynamic>() == true

        when {
            isHomeRoute || isDynamicRoute -> {
                scope.launch {
                    EventBus.send(Event.NotificationChildRefresh)
                }
            }

            else -> {
                navController.navigate(route) {
                    popUpTo(Route.Home) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = Route.Splash,
            enterTransition = {
                slideInHorizontally { it }
            },
            popExitTransition = {
                slideOutHorizontally { it }
            },
            popEnterTransition = {
                slideInHorizontally { -it }
            },
            exitTransition = {
                slideOutHorizontally { -it }
            }
        ) {
            composable<Route.Splash>(
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                SplashScreen {
                    val startRoute = if (isLogin) Route.Home else Route.Login
                    navController.navigate(startRoute) {
                        popUpTo<Route.Splash> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            homeGraph(navController)
            composable<Route.Login> { QRCodeLoginScreen() }
            composable<Route.Play> { PlayerScreen(it.toRoute()) }

        }

        val isSplashScreen = currentDestination?.destination
            ?.hierarchy?.first()?.hasRoute(Route.Splash::class) == true
        if (isSplashScreen.not()) {
            val isHomeGraph = currentDestination?.destination
                ?.hierarchy?.any { it.hasRoute(Route.HomeGraph::class) } == true
            AnimatedVisibility(
                visible = isHomeGraph,
                modifier = Modifier.align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.background)
                    .navigationBarsPadding(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SmallBottomAppBar(
                    items = bottomAppBarItems,
                    selectedIndex = bottomAppBarSelectedIndex
                ) { index ->
                    bottomAppBarSelectedIndex = index
                    handleBottomEvent(index)
                }
            }
        }
    }

}

@Composable
private fun ExitAppHandle() {
    val activity = LocalActivity.current
    var backPressedTime by remember { mutableLongStateOf(0L) }
    BackHandler(enabled = true) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < 2000) {
            // 退出应用
            activity?.finish()
        } else {
            // 提示用户再按一次退出
            backPressedTime = currentTime
            Toast.makeText(activity, "再按一次退出应用", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
private fun GetAndCacheUserProfile(isLogin: Boolean) {
    val context = LocalContext.current
    val userInfo = koinInject<UserInfo>()

    LaunchedEffect(isLogin) {
        context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
            ?.apply {
                userInfo.getUserProfile(
                    cookie = this
                )?.data?.let {
                    Log.d(TAG, "App: $it")
                    context.setValue(FACE_URL_KEY.name, it.face)
                    context.setValue(USERNAME_KEY.name, it.uname)
                }
            }
    }
}


private fun NavGraphBuilder.homeGraph(navController: NavController) {
    navigation<Route.HomeGraph>(
        startDestination = Route.Home,
        enterTransition = { fadeIn() },
        popExitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        composable<Route.Home> {
            HomeScreen(
                navigateToRoute = {
                    navController.navigate(it)
                }
            )
        }
        composable<Route.Mine> { MineScreen() }
        composable<Route.Subscription> { SubscriptionScreen() }
        composable<Route.Dynamic> {
            DynamicScreen(
                navigateToRoute = {
                    navController.navigate(it)
                }
            )
        }
    }
}
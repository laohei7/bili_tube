package com.laohei.bili_tube.app

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.laohei.bili_sdk.user.GetUserInfo
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.component.SideNavigateRail
import com.laohei.bili_tube.component.appbar.BottomAppBarItem
import com.laohei.bili_tube.component.appbar.SmallBottomAppBar
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.core.FACE_URL_KEY
import com.laohei.bili_tube.core.UP_MID_KEY
import com.laohei.bili_tube.core.USERNAME_KEY
import com.laohei.bili_tube.core.VIP_STATUS_KEY
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.core.util.setValue
import com.laohei.bili_tube.core.util.useLightSystemBarIcon
import com.laohei.bili_tube.dataStore
import com.laohei.bili_tube.presentation.dynamic.DynamicScreen
import com.laohei.bili_tube.presentation.history.HistoryScreen
import com.laohei.bili_tube.presentation.home.HomeScreen
import com.laohei.bili_tube.presentation.login.QRCodeLoginScreen
import com.laohei.bili_tube.presentation.mine.MineScreen
import com.laohei.bili_tube.presentation.player.PlayerScreen
import com.laohei.bili_tube.presentation.playlist.PlaylistScreen
import com.laohei.bili_tube.presentation.splash.SplashScreen
import com.laohei.bili_tube.presentation.subscription.SubscriptionScreen
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.system.exitProcess

private const val TAG = "App"

@Composable
fun App() {
    val activity = LocalActivity.current
    val context = LocalContext.current
    val navController = rememberNavController()
    val currentDestination by navController.currentBackStackEntryAsState()
    var isLogin by rememberSaveable(Unit) { mutableStateOf(false) }

    val isPlayRoute = currentDestination?.destination?.hasRoute<Route.Play>() == true
    if (isPlayRoute.not()) {
        activity?.useLightSystemBarIcon(isSystemInDarkTheme().not())
    }

    AppEventListener()

    LaunchedEffect(isLogin) {
        if (isLogin.not()) {
            return@LaunchedEffect
        }
        val isLoginRoute = currentDestination?.destination?.hasRoute<Route.Login>() == true
        if (isLoginRoute.not()) {
            return@LaunchedEffect
        }
        navController.navigate(Route.HomeGraph) {
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
                val startRoute = if (isLogin) Route.HomeGraph else Route.Login
                navController.navigate(startRoute) {
                    popUpTo<Route.Splash> { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
        composable<Route.Login> { QRCodeLoginScreen() }
        composable<Route.Play> {
            PlayerScreen(
                params = it.toRoute(),
                upPress = { navController.navigateUp() }
            )
        }
        composable<Route.Playlist> { PlaylistScreen() }
        composable<Route.History> {
            HistoryScreen(
                navigateToRoute = { navController.navigate(it) }
            )
        }
        composable<Route.HomeGraph> { MainChildGraph(navController) }
    }
}

@Composable
private fun MainChildGraph(navController: NavController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var bottomAppBarSelectedIndex by rememberSaveable  { mutableIntStateOf(0) }
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
    val mainChildNavController = rememberNavController()
    val currentDestination by mainChildNavController.currentBackStackEntryAsState()
    val navSuiteType =
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())

    fun handleBottomEvent(index: Int) {
        val route = when (index) {
            0 -> Route.HomeGraph.Home
            1 -> Route.HomeGraph.Dynamic
            else -> Route.HomeGraph.Mine
        }

        val isHomeRoute =
            index == 0 && currentDestination?.destination?.hasRoute<Route.HomeGraph.Home>() == true
        val isDynamicRoute =
            index == 1 && currentDestination?.destination?.hasRoute<Route.HomeGraph.Dynamic>() == true

        when {
            isHomeRoute || isDynamicRoute -> {
                scope.launch {
                    EventBus.send(Event.NotificationChildRefresh)
                }
            }

            else -> {
                mainChildNavController.navigate(route) {
                    popUpTo(Route.HomeGraph.Home) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    NavigationSuiteScaffoldLayout(
        navigationSuite = {
            when (navSuiteType) {
                NavigationSuiteType.NavigationDrawer,
                NavigationSuiteType.NavigationRail -> {
                    SideNavigateRail(
                        items = bottomAppBarItems,
                        selectedIndex = bottomAppBarSelectedIndex
                    ) { index ->
                        bottomAppBarSelectedIndex = index
                        handleBottomEvent(index)
                    }
                }

                NavigationSuiteType.None,
                NavigationSuiteType.NavigationBar -> {
                    Surface(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .navigationBarsPadding()
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
    ) {
        NavHost(
            navController = mainChildNavController,
            startDestination = Route.HomeGraph.Home,
        ) {
            homeGraph(navController)
        }
    }
}

@Composable
private fun ExitAppHandle() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = LocalActivity.current
    var backPressedTime by remember { mutableLongStateOf(0L) }
    BackHandler(enabled = true) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < 2000) {
            // 退出应用
            activity?.finish()
            exitProcess(0)
        } else {
            // 提示用户再按一次退出
            backPressedTime = currentTime
            scope.launch {
                EventBus.send(Event.AppEvent.ToastEvent(context.getString(R.string.str_exit_app_hint)))
            }
        }
    }
}

@Composable
private fun AppEventListener() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        EventBus.events.collect { event ->
            if ((event is Event.AppEvent).not()) {
                return@collect
            }
            when (val appEvent = event as Event.AppEvent) {
                is Event.AppEvent.ToastEvent -> {
                    Toast.makeText(context, appEvent.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Composable
private fun GetAndCacheUserProfile(isLogin: Boolean) {
    val context = LocalContext.current
    val userInfo = koinInject<GetUserInfo>()

    LaunchedEffect(isLogin) {
        context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
            ?.apply {
                userInfo.getUserProfile(
                    cookie = this
                )?.data?.let {
                    Log.d(TAG, "App: $it")
                    context.setValue(UP_MID_KEY.name, it.mid)
                    context.setValue(FACE_URL_KEY.name, it.face)
                    context.setValue(USERNAME_KEY.name, it.uname)
                    context.setValue(VIP_STATUS_KEY.name, it.vipStatus)
                }
            }
    }
}


private fun NavGraphBuilder.homeGraph(navController: NavController) {
    composable<Route.HomeGraph.Home> {
        HomeScreen(
            navigateToRoute = {
                navController.navigate(it)
            }
        )
    }
    composable<Route.HomeGraph.Mine> { MineScreen(navigateToRoute = { navController.navigate(it) }) }
    composable<Route.HomeGraph.Subscription> { SubscriptionScreen() }
    composable<Route.HomeGraph.Dynamic> {
        DynamicScreen(
            navigateToRoute = {
                navController.navigate(it)
            }
        )
    }
}
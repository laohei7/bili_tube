package com.laohei.bili_tube.app

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.util.fastJoinToString
import androidx.core.net.toUri
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.laohei.bili_sdk.apis.UserApi
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.core.FACE_URL_KEY
import com.laohei.bili_tube.core.IS_LOGIN_KEY
import com.laohei.bili_tube.core.UP_MID_KEY
import com.laohei.bili_tube.core.USERNAME_KEY
import com.laohei.bili_tube.core.VIP_STATUS_KEY
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.core.util.setValue
import com.laohei.bili_tube.core.util.useLightSystemBarIcon
import com.laohei.bili_tube.dataStore
import com.laohei.bili_tube.presentation.download.DownloadScreen
import com.laohei.bili_tube.presentation.history.HistoryScreen
import com.laohei.bili_tube.presentation.login.LoginScreen
import com.laohei.bili_tube.presentation.player.PlayerScreen
import com.laohei.bili_tube.presentation.playlist.PlaylistDetailScreen
import com.laohei.bili_tube.presentation.playlist.PlaylistScreen
import com.laohei.bili_tube.presentation.search.SearchScreen
import com.laohei.bili_tube.presentation.settings.SettingsScreen
import com.laohei.bili_tube.presentation.splash.SplashScreen
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.system.exitProcess

private const val TAG = "App"
private const val DBG = true

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
            preferences[IS_LOGIN_KEY] == true
        }.collect {
            isLogin = it
        }
    }

    InitCookieAndProfile(isLogin)

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
        composable<Route.Login> { LoginScreen() }
        composable<Route.Play> {
            PlayerScreen(
                playParam = koinInject<SharedViewModel>().mPlayParam,
                upPress = { navController.navigateUp() }
            )
        }
        composable<Route.Playlist> {
            PlaylistScreen(
                navigateToRoute = { navController.navigate(it) }
            )
        }
        composable<Route.PlaylistDetail> {
            PlaylistDetailScreen(
                param = it.toRoute(),
                upPress = { navController.navigateUp() },
                navigateToRoute = { navController.navigate(it) }
            )
        }
        composable<Route.History> {
            HistoryScreen(
                navigateToRoute = { navController.navigate(it) }
            )
        }
        composable<Route.DownloadManagement> {
            DownloadScreen(
                navigateToRoute = { navController.navigate(it) },
                upPress = { navController.navigateUp() }
            )
        }
        composable<Route.Search> {
            SearchScreen(
                navigateToRoute = { navController.navigate(it) },
                upPress = { navController.navigateUp() }
            )
        }
        composable<Route.HomeGraph> { MainGraph(navController) }

        composable<Route.Settings> { SettingsScreen(upPress = { navController.navigateUp() }) }
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
    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val message = results.any { it.value.not() }.run {
            if (this) context.getString(R.string.str_permission_not_granted)
            else context.getString(R.string.str_permission_granted)
        }
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
    val manageStorageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Environment.isExternalStorageManager()
            } else {
                false
            }
            val message = if (hasPermission) context.getString(R.string.str_permission_granted)
            else context.getString(R.string.str_permission_not_granted)
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    )
    LaunchedEffect(Unit) {
        EventBus.events.collect { event ->
            if ((event is Event.AppEvent).not()) {
                return@collect
            }
            when (event) {
                is Event.AppEvent.ToastEvent -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                is Event.AppEvent.PermissionRequestEvent -> {
                    if (event.permissions.contains(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val intent =
                                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                    data = "package:${context.packageName}".toUri()
                                }
                            manageStorageLauncher.launch(intent)
                        }
                    } else {
                        permissionsLauncher.launch(event.permissions.toTypedArray())
                    }
                }
            }
        }
    }
}

@Composable
private fun InitCookieAndProfile(isLogin: Boolean) {
    val context = LocalContext.current
    val userApi = koinInject<UserApi>()
    val client = koinInject<HttpClient>()

    LaunchedEffect(isLogin) {
        var cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        Log.d(TAG, "InitCookieAndProfile: $cookie")
        val hasBuvid4 = cookie?.contains("buvid4") == true
        val hasBuvid3 = cookie?.contains("buvid3") == true
        val newCookie = cookie?.split("; ")?.toMutableList() ?: mutableListOf()
        async(Dispatchers.IO) {
            userApi.getSpiInfo(cookie).data.let {
                if (hasBuvid4.not()) {
                    newCookie.add("buvid4=${it.b4}")
                }
            }
        }.await()

        async(Dispatchers.IO) {
            val response = client.get("https://www.bilibili.com/") {
                header(HttpHeaders.UserAgent, "awa")
            }
            val tempCookies = response.headers.getAll(HttpHeaders.SetCookie) ?: emptyList()
            if (hasBuvid3.not()) {
                newCookie.addAll(tempCookies)
            }
        }.await()

        context.dataStore.edit { settings ->
            val cookieStr = newCookie.fastJoinToString("; ")
            settings[COOKIE_KEY] = cookieStr
        }
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        Log.d(TAG, "InitCookieAndProfile: $cookie")
        if (isLogin.not()) {
            return@LaunchedEffect
        }
        cookie?.run {
            userApi.getUserProfile(cookie = this)?.let {
                if (it.code == -101) {
                    return@let
                }
                val profile = it.data
                context.setValue(UP_MID_KEY.name, profile.mid)
                context.setValue(FACE_URL_KEY.name, profile.face)
                context.setValue(USERNAME_KEY.name, profile.uname)
                context.setValue(VIP_STATUS_KEY.name, profile.vipStatus)
            }
        }
    }
}
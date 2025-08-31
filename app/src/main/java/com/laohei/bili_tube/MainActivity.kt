package com.laohei.bili_tube

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastJoinToString
import androidx.core.net.toUri
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.laohei.bili_sdk.apis.AuthApi
import com.laohei.bili_sdk.apis.UserApi
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.core.FACE_URL_KEY
import com.laohei.bili_tube.core.IS_LOGIN_KEY
import com.laohei.bili_tube.core.UP_MID_KEY
import com.laohei.bili_tube.core.USERNAME_KEY
import com.laohei.bili_tube.core.VIP_STATUS_KEY
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.core.runtime.LifecycleEffect
import com.laohei.bili_tube.data.local.datastore.dataStore
import com.laohei.bili_tube.nav.AppNav
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.theme.Bili_tubeTheme
import com.laohei.bili_tube.network.HttpClientFactory
import com.laohei.bili_tube.core.extension.setValue
import com.laohei.bili_tube.core.extension.useLightSystemBarIcon
import com.laohei.bili_tube.ui.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {

    companion object {
        private val TAG = MainActivity::class.simpleName
        private const val DBG = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedViewModel = koinInject<SharedViewModel>()
            val appNavController = rememberNavController()
            val appState by sharedViewModel.appState.collectAsStateWithLifecycle()

            AppEventListener()
            OnExitAppRequested()
            ApplySystemUi(appNavController)
            ObserveLoginState(
                appNavController = appNavController,
                appState = appState,
                updateAppState = sharedViewModel::updateAppState
            )

            Bili_tubeTheme {
                AppNav(
                    sharedViewModel = sharedViewModel,
                    appNavController = appNavController,
                    appState = appState
                )
            }
        }
    }

    @OptIn(UnstableApi::class)
    override fun onDestroy() {
        super.onDestroy()
        HttpClientFactory.client.close()
    }


    @Composable
    private fun AppEventListener() {
        val scope = rememberCoroutineScope()
        val permissionsLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            scope.launch {
                EventBus.send(
                    Event.AppEvent.ToastEvent(
                        when {
                            results.any { it.value.not() } -> R.string.str_permission_not_granted
                            else -> R.string.str_permission_granted
                        }
                    )
                )
            }
        }
        val manageStorageLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Environment.isExternalStorageManager()
                } else {
                    false
                }
                scope.launch {
                    EventBus.send(
                        Event.AppEvent.ToastEvent(
                            when {
                                !hasPermission -> R.string.str_permission_not_granted
                                else -> R.string.str_permission_granted
                            }
                        )
                    )
                }
            }
        )
        LaunchedEffect(Unit) {
            EventBus.events.collect { event ->
                if ((event is Event.AppEvent).not()) {
                    return@collect
                }
                when (event) {
                    is Event.AppEvent.ToastEvent -> {
                        Toast.makeText(
                            this@MainActivity,
                            getString(event.messageId),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Event.AppEvent.ToastTextEvent -> {
                        Toast.makeText(this@MainActivity, event.message, Toast.LENGTH_SHORT).show()
                    }

                    is Event.AppEvent.PermissionRequestEvent -> {
                        if (event.permissions.contains(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                val intent =
                                    Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                        data = "package:${packageName}".toUri()
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
    private fun OnExitAppRequested() {
        val scope = rememberCoroutineScope()
        var backPressedTime by remember { mutableLongStateOf(0L) }
        BackHandler(enabled = true) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - backPressedTime < 2000) {
                finish()
                exitProcess(0)
            } else {
                backPressedTime = currentTime
                scope.launch {
                    EventBus.send(Event.AppEvent.ToastEvent(R.string.str_exit_app_hint))
                }
            }
        }
    }

    @Composable
    private fun ApplySystemUi(appNavController: NavHostController) {
        val currentDestination by appNavController.currentBackStackEntryAsState()
        val isPlayRoute = currentDestination?.destination?.hasRoute<AppRoute.Play>() == true
        when {
            isPlayRoute -> {
                useLightSystemBarIcon(false)
            }

            else -> {
                useLightSystemBarIcon(isSystemInDarkTheme().not())
            }
        }
    }

    @Composable
    private fun ObserveLoginState(
        scope: CoroutineScope = rememberCoroutineScope(),
        appNavController: NavHostController,
        appState: AppState,
        authApi: AuthApi = koinInject(),
        userApi: UserApi = koinInject(),
        updateAppState: (AppState) -> Unit,
    ) {
        val currentDestination by appNavController.currentBackStackEntryAsState()

        LaunchedEffect(Unit) {
            val cookie = dataStore.data.firstOrNull()?.get(COOKIE_KEY)
            val newCookie = cookie?.split("; ")?.toMutableList() ?: mutableListOf()
            val buvid3Cookie = authApi.getBuvid3()
            buvid3Cookie.fastForEach { item ->
                if (!newCookie.contains(item.split("=").first())) {
                    newCookie.add(item)
                }
            }
//            newCookie.addAll(buvid3Cookie)
            if (DBG) {
                Log.d(TAG, "ObserveLoginState: init buvid3 success")
            }

            // initialize buvid4 / buvid3 for cookie
            newCookie.removeIf { it.contains("buvid4") }
            newCookie.removeIf { it.contains("buvid3") }
            userApi.getSpiInfo(cookie).data.let {
                newCookie.add("buvid4=${it.b4}")
                newCookie.add("buvid3=${it.b3}")
                Log.d(TAG, "ObserveLoginState: b3 ${it.b3}")
                Log.d(TAG, "ObserveLoginState: b4 ${it.b4}")
                if (DBG) {
                    Log.d(TAG, "ObserveLoginState: init buvid4 / buvid3 success")
                }
            }

            dataStore.edit { settings ->
                val cookieStr = newCookie.distinct().fastJoinToString("; ")
                settings[COOKIE_KEY] = cookieStr
            }

            if (DBG) {
                Log.d(TAG, "ObserveLoginState: ${dataStore.data.firstOrNull()?.get(COOKIE_KEY)}")
            }
        }

        LifecycleEffect(
            onCreate = {
                scope.launch {

                }
            },
            onStart = {
                // listen login state
                scope.launch {
                    dataStore.data.map { preferences ->
                        preferences[IS_LOGIN_KEY] == true
                    }.distinctUntilChanged().collect {
                        if (DBG) {
                            Log.d(
                                TAG,
                                "ObserveLoginState: The login status changes to the value of $it"
                            )
                        }
                        updateAppState(appState.copy(isLogin = it))
                    }
                }

                scope.launch {
                    dataStore.data.map { preferences ->
                        Pair(
                            preferences[IS_LOGIN_KEY],
                            preferences[COOKIE_KEY]
                        )
                    }.distinctUntilChanged().collect { pair ->
                        if (DBG) {
                            Log.d(TAG, "ObserveLoginState: $pair")
                        }
                        if (pair.first == true && pair.second != null) {
                            initUserProfile(userApi, pair.second!!)
                        }
                    }
                }
            }
        )

        // Log in successfully and redirect to the main page
        LaunchedEffect(appState.isLogin) {
            val isLogin = appState.isLogin
            if (isLogin.not()) {
                return@LaunchedEffect
            }

            val isLoginRoute =
                currentDestination?.destination?.hasRoute<AppRoute.LoginNav>() == true

            when {
                isLoginRoute -> {
                    appNavController.navigate(AppRoute.MainNav) {
                        popUpTo<AppRoute.LoginNav> { inclusive = true }
                        launchSingleTop = true
                    }
                    if (DBG) {
                        Log.d(TAG, "ObserveLoginState: navigate to MainNav")
                    }
                }
            }
        }
    }

    private suspend fun initUserProfile(userApi: UserApi, cookie: String) {
        // initialize user profile (eg: name, mid, face, vip and so on)
        userApi.getUserProfile(cookie = cookie)?.let {
            if (it.code == -101) {
                return@let
            }
            val profile = it.data
            setValue(UP_MID_KEY.name, profile.mid)
            setValue(FACE_URL_KEY.name, profile.face)
            setValue(USERNAME_KEY.name, profile.uname)
            setValue(VIP_STATUS_KEY.name, profile.vipStatus)

            if (DBG) {
                Log.d(TAG, "ObserveLoginState: init user profile success")
            }
        }
    }
}
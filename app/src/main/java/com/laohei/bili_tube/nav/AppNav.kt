package com.laohei.bili_tube.nav

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.laohei.bili_tube.AppState
import com.laohei.bili_tube.SharedViewModel
import com.laohei.bili_tube.features.download.DownloadScreen
import com.laohei.bili_tube.features.history.HistoryScreen
import com.laohei.bili_tube.features.login.LoginNav
import com.laohei.bili_tube.features.main.MainNav
import com.laohei.bili_tube.features.player.VideoScreen
import com.laohei.bili_tube.features.playlist.PlaylistDetailScreen
import com.laohei.bili_tube.features.playlist.PlaylistScreen
import com.laohei.bili_tube.features.search.SearchScreen
import com.laohei.bili_tube.features.setting.SettingNav
import com.laohei.bili_tube.features.splash.SplashScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNav(
    sharedViewModel: SharedViewModel,
    appNavController: NavHostController,
    appState: AppState
) {
    var localIsLogin by remember { mutableStateOf(false) }

    LaunchedEffect(appState.isLogin) {
        localIsLogin = appState.isLogin
    }

    SharedTransitionScope {
        NavHost(
            navController = appNavController,
            startDestination = AppRoute.Splash,
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
            composable<AppRoute.Splash>(
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                SplashScreen {
                    val nextRoute = when {
                        localIsLogin -> AppRoute.MainNav
                        else -> AppRoute.LoginNav
                    }
                    appNavController.navigate(nextRoute) {
                        popUpTo<AppRoute.Splash> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            composable<AppRoute.LoginNav> { LoginNav() }

            composable<AppRoute.MainNav> {
                MainNav(
                    sharedTransitionScope = this@SharedTransitionScope,
                    animatedVisibilityScope = this
                ) {
                    appNavController.navigate(it)
                }
            }

            composable<AppRoute.Play> {
                VideoScreen(
                    playParam = sharedViewModel.mPlayParam
                ) {
                    appNavController.navigateUp()
                }
            }

            composable<AppRoute.Download> {
                DownloadScreen(
                    navigateToAppRoute = { appNavController.navigate(it) },
                    upPress = { appNavController.navigateUp() }
                )
            }

            composable<AppRoute.History> {
                HistoryScreen(
                    navigateToAppRoute = { appNavController.navigate(it) },
                    upPress = { appNavController.navigateUp() }
                )
            }

            composable<AppRoute.SettingNav> {
                SettingNav {
                    appNavController.navigateUp()
                }
            }

            composable<AppRoute.Search> {
                SearchScreen(
                    navigateToAppRoute = { appNavController.navigate(it) },
                    upPress = { appNavController.navigateUp() }
                )
            }

            composable<AppRoute.Playlist> {
                PlaylistScreen(
                    navigateToAppRoute = { appNavController.navigate(it) },
                    upPress = { appNavController.navigateUp() }
                )
            }
            composable<AppRoute.PlaylistDetail> {
                PlaylistDetailScreen(
                    param = it.toRoute(),
                    upPress = { appNavController.navigateUp() },
                    navigateToAppRoute = { route -> appNavController.navigate(route) }
                )
            }
        }
    }
}
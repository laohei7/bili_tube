package com.laohei.bili_tube.nav

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.laohei.bili_tube.AppState
import com.laohei.bili_tube.ui.viewmodel.SharedViewModel
import com.laohei.bili_tube.features.download.DownloadScreen
import com.laohei.bili_tube.features.history.HistoryScreen
import com.laohei.bili_tube.features.image.ZoomableImagePagerScreen
import com.laohei.bili_tube.features.login.LoginNav
import com.laohei.bili_tube.features.main.MainNav
import com.laohei.bili_tube.features.player.VideoScreen
import com.laohei.bili_tube.features.playlist.PlaylistContentScreen
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

    SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = appNavController,
            startDestination = AppRoute.Splash,
            enterTransition = {
                when {
                    isMainNavToGallery(initialState, targetState) ||
                            isSplashOrLoginToMainNav(initialState, targetState) -> fadeIn()

                    else -> slideInHorizontally { it }
                }
            },
            popExitTransition = {
                when {
                    isMainNavToGallery(initialState, targetState) ||
                            isSplashOrLoginToMainNav(initialState, targetState) -> fadeOut()

                    else -> slideOutHorizontally { it }
                }
            },
            popEnterTransition = {
                when {
                    isMainNavToGallery(initialState, targetState) ||
                            isSplashOrLoginToMainNav(initialState, targetState) -> fadeIn()

                    else -> slideInHorizontally { -it }
                }
            },
            exitTransition = {
                when {
                    isMainNavToGallery(initialState, targetState) ||
                            isSplashOrLoginToMainNav(initialState, targetState) -> fadeOut()

                    else -> slideOutHorizontally { -it }
                }

            }
        ) {
            composable<AppRoute.Splash> {
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
                    sharedTransitionScope = this@SharedTransitionLayout,
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
            composable<AppRoute.PlaylistContent> {
                PlaylistContentScreen(
                    param = it.toRoute(),
                    upPress = { appNavController.navigateUp() },
                    navigateToAppRoute = { route -> appNavController.navigate(route) }
                )
            }
            composable<AppRoute.Gallery> {
                ZoomableImagePagerScreen(
                    galleryParam = it.toRoute(),
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this
                )
            }
        }
    }
}

private fun isSplashOrLoginToMainNav(
    initialState: NavBackStackEntry,
    targetState: NavBackStackEntry,
): Boolean {
    val isSplashToMainNav =
        initialState.destination.hasRoute(AppRoute.Splash::class) &&
                targetState.destination.hasRoute(AppRoute.MainNav::class)
    val isLoginNavToMainNav =
        initialState.destination.hasRoute(AppRoute.LoginNav::class) &&
                targetState.destination.hasRoute(AppRoute.MainNav::class)
    return isLoginNavToMainNav || isSplashToMainNav
}

private fun isMainNavToGallery(
    initialState: NavBackStackEntry,
    targetState: NavBackStackEntry,
): Boolean {
    val isMainNavToGallery =
        initialState.destination.hasRoute(AppRoute.MainNav::class) &&
                targetState.destination.hasRoute(AppRoute.Gallery::class)
    val isGalleryToMainNav =
        initialState.destination.hasRoute(AppRoute.Gallery::class) &&
                targetState.destination.hasRoute(AppRoute.MainNav::class)
    return isMainNavToGallery || isGalleryToMainNav
}
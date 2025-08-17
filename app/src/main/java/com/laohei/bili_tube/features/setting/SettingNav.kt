package com.laohei.bili_tube.features.setting

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.component.layout.DeviceConfiguration
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingNav(
    viewModel: SettingViewModel = koinViewModel(),
    navigateToUp: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val settingNavController = rememberNavController()
    val currentDestination by settingNavController.currentBackStackEntryAsState()
    val titleId by remember {
        derivedStateOf {
            val destination = currentDestination?.destination
            when {
                destination?.hasRoute<Route.Settings.VideoSetting>() == true -> R.string.str_video_quality
                destination?.hasRoute<Route.Settings.AudioSetting>() == true -> R.string.str_audio_quality
                destination?.hasRoute<Route.Settings.PlaySetting>() == true -> R.string.str_play
                else -> R.string.str_settings
            }
        }
    }
    AdaptiveLayout { uiType, _, _ ->
        Scaffold(
            topBar = {
                when (uiType) {
                    DeviceConfiguration.TABLE_PORTRAIT,
                    DeviceConfiguration.MOBILE_PORTRAIT -> {
                        SettingTopBar(
                            title = stringResource(titleId)
                        ) {
                            val isMainSetting =
                                currentDestination?.destination?.hasRoute<Route.Settings.MainSetting>() == true
                            if (isMainSetting) {
                                navigateToUp()
                            } else {
                                settingNavController.navigateUp()
                            }
                        }
                    }

                    else -> {}
                }

            }
        ) { innerPadding ->
            NavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        when (uiType) {
                            DeviceConfiguration.TABLE_PORTRAIT,
                            DeviceConfiguration.MOBILE_PORTRAIT -> {
                                Modifier.padding(innerPadding)
                            }

                            else -> Modifier
                        }
                    ),
                navController = settingNavController,
                startDestination = Route.Settings.MainSetting,
                enterTransition = {
                    when (uiType) {
                        DeviceConfiguration.TABLE_PORTRAIT,
                        DeviceConfiguration.MOBILE_PORTRAIT -> slideInHorizontally { it }

                        else -> fadeIn()
                    }

                },
                popExitTransition = {
                    when (uiType) {
                        DeviceConfiguration.TABLE_PORTRAIT,
                        DeviceConfiguration.MOBILE_PORTRAIT -> slideOutHorizontally { it }

                        else -> fadeOut()
                    }

                },
                popEnterTransition = {
                    when (uiType) {
                        DeviceConfiguration.TABLE_PORTRAIT,
                        DeviceConfiguration.MOBILE_PORTRAIT -> slideInHorizontally { -it }

                        else -> fadeIn()
                    }
                },
                exitTransition = {
                    when (uiType) {
                        DeviceConfiguration.TABLE_PORTRAIT,
                        DeviceConfiguration.MOBILE_PORTRAIT -> slideOutHorizontally { -it }

                        else -> fadeOut()
                    }

                }
            ) {
                composable<Route.Settings.MainSetting> {
                    MainSettingScreen(
                        state = state,
                        navigateToSettingRoute = {
                            settingNavController.navigate(it)
                        },
                        onSettingsActionClick = viewModel::onSettingsAction
                    )
                }
                composable<Route.Settings.VideoSetting> {
                    VideoSettingScreen(
                        mobileQuality = state.mobileNetVideoQuality,
                        wlanQuality = state.wlanVideoQuality,
                        onSettingsActionClick = viewModel::onSettingsAction
                    )
                }
                composable<Route.Settings.AudioSetting> {
                    AudioSettingScreen(
                        mobileQuality = state.mobileNetAudioQuality,
                        wlanQuality = state.wlanAudioQuality,
                        onSettingsActionClick = viewModel::onSettingsAction
                    )
                }
                composable<Route.Settings.PlaySetting> {
                    PlaySettingScreen(
                        autoSkipOpEnd = state.autoSkipOpEnd,
                        onSettingsActionClick = viewModel::onSettingsAction
                    )
                }
            }
        }
    }
}
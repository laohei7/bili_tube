package com.laohei.bili_tube.di

import com.laohei.bili_tube.features.download.DownloadViewModel
import com.laohei.bili_tube.features.history.HistoryViewModel
import com.laohei.bili_tube.features.login.LoginViewModel
import com.laohei.bili_tube.features.main.home.HomeViewModel
import com.laohei.bili_tube.features.main.home.hot.HotViewModel
import com.laohei.bili_tube.features.main.home.recommend.RecommendViewModel
import com.laohei.bili_tube.features.main.profile.ProfileViewModel
import com.laohei.bili_tube.features.main.subscription.SubscriptionViewModel
import com.laohei.bili_tube.features.player.MediaViewModel
import com.laohei.bili_tube.features.playlist.PlaylistContentViewModel
import com.laohei.bili_tube.features.playlist.PlaylistViewModel
import com.laohei.bili_tube.features.search.SearchViewModel
import com.laohei.bili_tube.features.setting.SettingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::RecommendViewModel)
    viewModelOf(::HotViewModel)
    viewModelOf(::SubscriptionViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::PlaylistViewModel)
    viewModelOf(::DownloadViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::SettingViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::PlaylistContentViewModel)
    viewModelOf(::MediaViewModel)
}
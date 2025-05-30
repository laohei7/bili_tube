package com.laohei.bili_tube.di

import android.annotation.SuppressLint
import android.content.Context
import com.laohei.bili_sdk.apis.AuthApi
import com.laohei.bili_sdk.apis.BangumiApi
import com.laohei.bili_sdk.apis.FolderApi
import com.laohei.bili_sdk.apis.HistoryApi
import com.laohei.bili_sdk.apis.InternationalizationApi
import com.laohei.bili_sdk.apis.PlayApi
import com.laohei.bili_sdk.apis.UserApi
import com.laohei.bili_sdk.apis.VideoApi
import com.laohei.bili_sdk.apis.impl.AuthApiImpl
import com.laohei.bili_sdk.apis.impl.BangumiApiImpl
import com.laohei.bili_sdk.apis.impl.FolderApiImpl
import com.laohei.bili_sdk.apis.impl.HistoryApiImpl
import com.laohei.bili_sdk.apis.impl.InternationalizationApiImpl
import com.laohei.bili_sdk.apis.impl.PlayApiImpl
import com.laohei.bili_sdk.apis.impl.UserApiImpl
import com.laohei.bili_sdk.apis.impl.VideoApiImpl
import com.laohei.bili_sdk.search.SearchRequest
import com.laohei.bili_tube.app.SharedViewModel
import com.laohei.bili_tube.core.util.NetworkUtil
import com.laohei.bili_tube.core.util.PreferencesUtil
import com.laohei.bili_tube.db.BiliTubeDB
import com.laohei.bili_tube.presentation.download.DownloadViewModel
import com.laohei.bili_tube.presentation.history.HistoryViewModel
import com.laohei.bili_tube.presentation.home.HomeViewModel
import com.laohei.bili_tube.presentation.home.hot.HotViewModel
import com.laohei.bili_tube.presentation.home.recommend.RecommendViewModel
import com.laohei.bili_tube.presentation.login.LoginViewModel
import com.laohei.bili_tube.presentation.mine.MineViewModel
import com.laohei.bili_tube.presentation.player.PlayerViewModel
import com.laohei.bili_tube.presentation.playlist.PlaylistDetailViewModel
import com.laohei.bili_tube.presentation.playlist.PlaylistViewModel
import com.laohei.bili_tube.presentation.search.SearchViewModel
import com.laohei.bili_tube.presentation.settings.SettingsViewModel
import com.laohei.bili_tube.presentation.subscription.SubscriptionViewModel
import com.laohei.bili_tube.repository.BiliHistoryRepository
import com.laohei.bili_tube.repository.BiliHomeRepository
import com.laohei.bili_tube.repository.BiliLoginRepository
import com.laohei.bili_tube.repository.BiliMineRepository
import com.laohei.bili_tube.repository.BiliPlayRepository
import com.laohei.bili_tube.repository.BiliPlaylistRepository
import com.laohei.bili_tube.repository.BiliSearchRepository
import com.laohei.bili_tube.repository.BiliSubscriptionRepository
import com.laohei.bili_tube.utill.HttpClientFactory
import com.laohei.bili_tube.utill.download.DownloadManager
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

@SuppressLint("UnsafeOptInUsageError")
val appModule = module {
    singleOf(::PreferencesUtil)
    singleOf(::NetworkUtil)
    singleOf(::SharedViewModel)
    single { BiliTubeDB.getInstance(get(Context::class) as Context) }
    single { HttpClientFactory.client }
    single { HttpClientFactory.getCronetEngine(get()) }
    single { HttpClientFactory.getSimpleCache(get()) }

    singleOf(::DownloadManager)
    singleOf(::SearchRequest)
    singleOf(::UserApiImpl).bind(UserApi::class)
    singleOf(::HistoryApiImpl).bind(HistoryApi::class)
    singleOf(::AuthApiImpl).bind(AuthApi::class)
    singleOf(::InternationalizationApiImpl).bind(InternationalizationApi::class)
    singleOf(::FolderApiImpl).bind(FolderApi::class)
    singleOf(::VideoApiImpl).bind(VideoApi::class)
    singleOf(::BangumiApiImpl).bind(BangumiApi::class)
    singleOf(::PlayApiImpl).bind(PlayApi::class)

    singleOf(::BiliHomeRepository)
    singleOf(::BiliPlayRepository)
    singleOf(::BiliSubscriptionRepository)
    singleOf(::BiliHistoryRepository)
    singleOf(::BiliMineRepository)
    singleOf(::BiliHistoryRepository)
    singleOf(::BiliPlaylistRepository)
    singleOf(::BiliSearchRepository)
    singleOf(::BiliLoginRepository)

//    factoryOf(::DefaultMediaManager)

    viewModelOf(::HomeViewModel)
    viewModelOf(::RecommendViewModel)
    viewModelOf(::HotViewModel)
    viewModelOf(::PlayerViewModel)
    viewModelOf(::SubscriptionViewModel)
    viewModelOf(::MineViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::PlaylistViewModel)
    viewModelOf(::DownloadViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::PlaylistDetailViewModel)

}
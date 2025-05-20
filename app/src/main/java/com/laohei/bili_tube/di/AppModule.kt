package com.laohei.bili_tube.di

import android.annotation.SuppressLint
import android.content.Context
import com.laohei.bili_sdk.anime.GetBangumi
import com.laohei.bili_sdk.anime.GetTimeline
import com.laohei.bili_sdk.dynamic.GetWebDynamic
import com.laohei.bili_sdk.folder.GetFolder
import com.laohei.bili_sdk.folder.PostFolder
import com.laohei.bili_sdk.history.GetHistory
import com.laohei.bili_sdk.history.GetWatchLater
import com.laohei.bili_sdk.history.PostToView
import com.laohei.bili_sdk.hot.GetHots
import com.laohei.bili_sdk.location.GetCountryList
import com.laohei.bili_sdk.login.Login
import com.laohei.bili_sdk.recommend.GetRecommend
import com.laohei.bili_sdk.search.SearchRequest
import com.laohei.bili_sdk.user.GetUploadedVideo
import com.laohei.bili_sdk.user.GetUserInfo
import com.laohei.bili_sdk.video.GetArchive
import com.laohei.bili_sdk.video.GetInfo
import com.laohei.bili_sdk.video.GetReply
import com.laohei.bili_sdk.video.GetURL
import com.laohei.bili_sdk.video.PostHeartBeat
import com.laohei.bili_sdk.video.PostInfo
import com.laohei.bili_tube.core.util.NetworkUtil
import com.laohei.bili_tube.core.util.PreferencesUtil
import com.laohei.bili_tube.db.BiliTubeDB
import com.laohei.bili_tube.presentation.download.DownloadViewModel
import com.laohei.bili_tube.presentation.subscription.SubscriptionViewModel
import com.laohei.bili_tube.presentation.history.HistoryViewModel
import com.laohei.bili_tube.presentation.home.HomeViewModel
import com.laohei.bili_tube.presentation.home.hot.HotViewModel
import com.laohei.bili_tube.presentation.home.recommend.RecommendViewModel
import com.laohei.bili_tube.presentation.login.LoginViewModel
import com.laohei.bili_tube.presentation.mine.MineViewModel
import com.laohei.bili_tube.presentation.player.PlayerViewModel
import com.laohei.bili_tube.presentation.playlist.PlaylistViewModel
import com.laohei.bili_tube.presentation.search.SearchViewModel
import com.laohei.bili_tube.presentation.settings.SettingsViewModel
import com.laohei.bili_tube.repository.BiliSubscriptionRepository
import com.laohei.bili_tube.repository.BiliHistoryRepository
import com.laohei.bili_tube.repository.BiliHomeRepository
import com.laohei.bili_tube.repository.BiliLoginRepository
import com.laohei.bili_tube.repository.BiliMineRepository
import com.laohei.bili_tube.repository.BiliPlayRepository
import com.laohei.bili_tube.repository.BiliPlaylistRepository
import com.laohei.bili_tube.repository.BiliSearchRepository
import com.laohei.bili_tube.utill.HttpClientFactory
import com.laohei.bili_tube.utill.download.DownloadManager
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@SuppressLint("UnsafeOptInUsageError")
val appModule = module {
    singleOf(::PreferencesUtil)
    singleOf(::NetworkUtil)
    single { BiliTubeDB.getInstance(get(Context::class) as Context) }
    single { HttpClientFactory.client }
    single { HttpClientFactory.getCronetEngine(get()) }
    single { HttpClientFactory.getSimpleCache(get()) }

    singleOf(::DownloadManager)
    singleOf(::Login)
    singleOf(::GetRecommend)
    singleOf(::GetHots)
    singleOf(::GetURL)
    singleOf(::GetTimeline)
    singleOf(::GetInfo)
    singleOf(::PostInfo)
    singleOf(::GetUserInfo)
    singleOf(::GetReply)
    singleOf(::PostHeartBeat)
    singleOf(::GetWebDynamic)
    singleOf(::GetWatchLater)
    singleOf(::GetHistory)
    singleOf(::GetFolder)
    singleOf(::PostFolder)
    singleOf(::GetArchive)
    singleOf(::PostToView)
    singleOf(::SearchRequest)
    singleOf(::GetBangumi)
    singleOf(::GetCountryList)
    singleOf(::GetUploadedVideo)

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

}
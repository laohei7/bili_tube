package com.laohei.bili_tube.di

import android.annotation.SuppressLint
import com.laohei.bili_sdk.anime.Timeline
import com.laohei.bili_sdk.dynamic.WebDynamic
import com.laohei.bili_sdk.folder.Folder
import com.laohei.bili_sdk.history.History
import com.laohei.bili_sdk.history.WatchLater
import com.laohei.bili_sdk.hot.Hots
import com.laohei.bili_sdk.login.QRLogin
import com.laohei.bili_sdk.recommend.Recommend
import com.laohei.bili_sdk.user.UserInfo
import com.laohei.bili_sdk.video.PlayURL
import com.laohei.bili_sdk.video.VideoHeartBeat
import com.laohei.bili_sdk.video.VideoInfo
import com.laohei.bili_sdk.video.VideoReply
import com.laohei.bili_tube.presentation.dynamic.DynamicViewModel
import com.laohei.bili_tube.presentation.home.HomeViewModel
import com.laohei.bili_tube.presentation.home.hot.HotViewModel
import com.laohei.bili_tube.presentation.home.recommend.RecommendViewModel
import com.laohei.bili_tube.presentation.mine.MineViewModel
import com.laohei.bili_tube.presentation.player.PlayerViewModel
import com.laohei.bili_tube.presentation.player.state.media.DefaultMediaManager
import com.laohei.bili_tube.repository.BiliDynamicRepository
import com.laohei.bili_tube.repository.BiliHistoryRepository
import com.laohei.bili_tube.repository.BiliHomeRepository
import com.laohei.bili_tube.repository.BiliMineRepository
import com.laohei.bili_tube.repository.BiliPlayRepository
import com.laohei.bili_tube.utill.HttpClientFactory
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@SuppressLint("UnsafeOptInUsageError")
val appModule = module {
    single { HttpClientFactory.client }

    singleOf(::QRLogin)
    singleOf(::Recommend)
    singleOf(::Hots)
    singleOf(::PlayURL)
    singleOf(::Timeline)
    singleOf(::VideoInfo)
    singleOf(::UserInfo)
    singleOf(::VideoReply)
    singleOf(::VideoHeartBeat)
    singleOf(::WebDynamic)
    singleOf(::WatchLater)
    singleOf(::History)
    singleOf(::Folder)

    singleOf(::BiliHomeRepository)
    singleOf(::BiliPlayRepository)
    singleOf(::BiliDynamicRepository)
    singleOf(::BiliHistoryRepository)
    singleOf(::BiliMineRepository)

    factoryOf(::DefaultMediaManager)

    viewModelOf(::HomeViewModel)
    viewModelOf(::RecommendViewModel)
    viewModelOf(::HotViewModel)
    viewModelOf(::PlayerViewModel)
    viewModelOf(::DynamicViewModel)
    viewModelOf(::MineViewModel)

}
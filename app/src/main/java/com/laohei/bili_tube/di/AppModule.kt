package com.laohei.bili_tube.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import com.laohei.bili_sdk.anime.GetTimeline
import com.laohei.bili_sdk.dynamic.GetWebDynamic
import com.laohei.bili_sdk.folder.GetFolder
import com.laohei.bili_sdk.folder.PostFolder
import com.laohei.bili_sdk.history.GetHistory
import com.laohei.bili_sdk.history.GetWatchLater
import com.laohei.bili_sdk.history.PostToView
import com.laohei.bili_sdk.hot.GetHots
import com.laohei.bili_sdk.login.QRLogin
import com.laohei.bili_sdk.recommend.GetRecommend
import com.laohei.bili_sdk.user.GetUserInfo
import com.laohei.bili_sdk.video.GetURL
import com.laohei.bili_sdk.video.GetArchive
import com.laohei.bili_sdk.video.PostHeartBeat
import com.laohei.bili_sdk.video.GetInfo
import com.laohei.bili_sdk.video.GetReply
import com.laohei.bili_sdk.video.PostInfo
import com.laohei.bili_tube.presentation.dynamic.DynamicViewModel
import com.laohei.bili_tube.presentation.history.HistoryViewModel
import com.laohei.bili_tube.presentation.home.HomeViewModel
import com.laohei.bili_tube.presentation.home.hot.HotViewModel
import com.laohei.bili_tube.presentation.home.recommend.RecommendViewModel
import com.laohei.bili_tube.presentation.mine.MineViewModel
import com.laohei.bili_tube.presentation.player.PlayerViewModel
import com.laohei.bili_tube.presentation.player.state.media.DefaultMediaManager
import com.laohei.bili_tube.presentation.playlist.PlaylistViewModel
import com.laohei.bili_tube.repository.BiliDynamicRepository
import com.laohei.bili_tube.repository.BiliHistoryRepository
import com.laohei.bili_tube.repository.BiliHomeRepository
import com.laohei.bili_tube.repository.BiliMineRepository
import com.laohei.bili_tube.repository.BiliPlayRepository
import com.laohei.bili_tube.repository.BiliPlaylistRepository
import com.laohei.bili_tube.utill.HttpClientFactory
import org.chromium.net.CronetEngine
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import java.io.File

@SuppressLint("UnsafeOptInUsageError")
val appModule = module {
    single { HttpClientFactory.client }
    single {
        CronetEngine.Builder(get(Context::class) as Context)
            .enableHttp2(true)
            .enableQuic(true)
            .build()
    }
    single {
        val context = get(Context::class) as Context
        SimpleCache(
            File(context.cacheDir, "media_cache"),
            LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024),
            StandaloneDatabaseProvider(context)
        )
    }

    singleOf(::QRLogin)
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

    singleOf(::BiliHomeRepository)
    singleOf(::BiliPlayRepository)
    singleOf(::BiliDynamicRepository)
    singleOf(::BiliHistoryRepository)
    singleOf(::BiliMineRepository)
    singleOf(::BiliHistoryRepository)
    singleOf(::BiliPlaylistRepository)

//    factoryOf(::DefaultMediaManager)

    viewModelOf(::HomeViewModel)
    viewModelOf(::RecommendViewModel)
    viewModelOf(::HotViewModel)
    viewModelOf(::PlayerViewModel)
    viewModelOf(::DynamicViewModel)
    viewModelOf(::MineViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::PlaylistViewModel)

}
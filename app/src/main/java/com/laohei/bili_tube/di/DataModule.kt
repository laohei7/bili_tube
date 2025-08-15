package com.laohei.bili_tube.di

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
import com.laohei.bili_tube.features.history.data.repository.BiliHistoryRepository
import com.laohei.bili_tube.features.login.data.repository.BiliLoginRepository
import com.laohei.bili_tube.features.main.home.data.repository.BiliHomeRepository
import com.laohei.bili_tube.features.main.profile.data.repository.BiliProfileRepository
import com.laohei.bili_tube.features.main.subscription.data.repository.BiliSubscriptionRepository
import com.laohei.bili_tube.features.player.data.repository.BiliPlayRepository
import com.laohei.bili_tube.features.playlist.data.repository.BiliPlaylistRepository
import com.laohei.bili_tube.features.search.data.repository.BiliSearchRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
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
    singleOf(::BiliProfileRepository)
    singleOf(::BiliHistoryRepository)
    singleOf(::BiliPlaylistRepository)
    singleOf(::BiliSearchRepository)
    singleOf(::BiliLoginRepository)
}
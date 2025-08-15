package com.laohei.bili_tube.di

import android.annotation.SuppressLint
import android.content.Context
import com.laohei.bili_tube.SharedViewModel
import com.laohei.bili_tube.room.BiliTubeDB
import com.laohei.bili_tube.utill.HttpClientFactory
import com.laohei.bili_tube.utill.NetworkUtil
import com.laohei.bili_tube.utill.PreferencesUtil
import com.laohei.bili_tube.utill.download.DownloadManager
import org.koin.core.module.dsl.singleOf
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
}
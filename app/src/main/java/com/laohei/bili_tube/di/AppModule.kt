package com.laohei.bili_tube.di

import android.annotation.SuppressLint
import com.laohei.bili_tube.ui.viewmodel.SharedViewModel
import com.laohei.bili_tube.data.local.prefs.PreferencesUtil
import com.laohei.bili_tube.network.HttpClientFactory
import com.laohei.bili_tube.network.NetworkUtil
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@SuppressLint("UnsafeOptInUsageError")
val appModule = module {
    singleOf(::PreferencesUtil)
    singleOf(::NetworkUtil)
    singleOf(::SharedViewModel)
    single { HttpClientFactory.client }
}
package com.laohei.bili_tube.di

import android.content.Context
import com.laohei.bili_tube.data.local.room.BiliTubeDB
import org.koin.dsl.module

val roomModule = module {
    single { BiliTubeDB.getInstance(get(Context::class) as Context) }
    single { get<BiliTubeDB>().downloadTaskDao() }
    single { get<BiliTubeDB>().biliSharedSourceDao() }
    single { get<BiliTubeDB>().searchHistoryDao() }
}
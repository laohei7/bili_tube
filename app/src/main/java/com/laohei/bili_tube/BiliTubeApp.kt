package com.laohei.bili_tube

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.CachePolicy
import com.laohei.bili_sdk.wbi.GetWbi
import com.laohei.bili_sdk.wbi.WbiParams
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.core.IMG_URL_KEY
import com.laohei.bili_tube.core.SUB_URL_KEY
import com.laohei.bili_tube.core.util.SystemUtil
import com.laohei.bili_tube.di.appModule
import com.laohei.bili_tube.utill.HttpClientFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bili_tube")

class BiliTubeApp : Application(), SingletonImageLoader.Factory {
    companion object {
        private val TAG = BiliTubeApp::class.simpleName
    }

    override fun onCreate() {
        super.onCreate()
        SystemUtil.init(this)
        startKoin {
            androidContext(this@BiliTubeApp)
            modules(appModule)
        }
        CoroutineScope(Dispatchers.IO).launch {
            if (WbiParams.wbi == null) {
                val imgKey = dataStore.data.firstOrNull()?.get(IMG_URL_KEY)
                val subKey = dataStore.data.firstOrNull()?.get(SUB_URL_KEY)
                if (imgKey != null && subKey != null) {
                    WbiParams.initWbi(imgKey, subKey)
                    Log.d(TAG, "onCreate: $imgKey $subKey")
                } else {
                    val cookie = dataStore.data.firstOrNull()?.get(COOKIE_KEY)
                    GetWbi.getWbiRequest(HttpClientFactory.client)
                        .wbi(cookie) { biliWbi ->
                            dataStore.edit { settings ->
                                settings[IMG_URL_KEY] = biliWbi.wbiImg.imgUrl
                                settings[SUB_URL_KEY] = biliWbi.wbiImg.subUrl
                            }
                        }
                }
            }
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .components {
                add(KtorNetworkFetcherFactory(httpClient = { HttpClientFactory.client }))
            }
            .build()
    }
}
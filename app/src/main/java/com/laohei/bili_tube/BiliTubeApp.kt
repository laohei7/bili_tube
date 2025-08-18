package com.laohei.bili_tube

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.media3.common.util.UnstableApi
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.laohei.bili_sdk.wbi.GetWbi
import com.laohei.bili_sdk.wbi.WbiParams
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.core.IMG_URL_KEY
import com.laohei.bili_tube.core.SUB_URL_KEY
import com.laohei.bili_tube.di.appModule
import com.laohei.bili_tube.di.dataModule
import com.laohei.bili_tube.di.viewModelModule
import com.laohei.bili_tube.utill.HttpClientFactory
import com.laohei.bili_tube.utill.SystemUtil
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

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        SystemUtil.init(this)
        startKoin {
            androidContext(this@BiliTubeApp)
            modules(appModule, dataModule, viewModelModule)
        }
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            if (WbiParams.wbi != null) return@launch

            val settings = dataStore.data.firstOrNull()
            val imgKey = settings?.get(IMG_URL_KEY)
            val subKey = settings?.get(SUB_URL_KEY)

            if (imgKey != null && subKey != null) {
                WbiParams.initWbi(imgKey, subKey)
                Log.d(TAG, "onCreate: Wbi from cache $imgKey $subKey")
            } else {
                val cookie = settings?.get(COOKIE_KEY)
                GetWbi.getWbiRequest(HttpClientFactory.client)
                    .wbi(cookie) { biliWbi ->
                        dataStore.edit { settings ->
                            settings[IMG_URL_KEY] = biliWbi.wbiImg.imgUrl
                            settings[SUB_URL_KEY] = biliWbi.wbiImg.subUrl
                        }
                        WbiParams.initWbi(
                            biliWbi.wbiImg.imgUrl,
                            biliWbi.wbiImg.subUrl
                        )
                        Log.d(TAG, "onCreate: Wbi from network ${biliWbi.wbiImg}")
                    }
            }
        }
    }

    @OptIn(UnstableApi::class)
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(this, 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.15)
                    .build()
            }
            .components {
                add(KtorNetworkFetcherFactory(httpClient = { HttpClientFactory.coilClient }))
            }
            .build()
    }
}
package com.laohei.bili_tube.data

import android.content.Context
import android.os.Build
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.CacheDataSink
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cronet.CronetDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import com.laohei.bili_tube.network.CacheManager
import com.laohei.bili_tube.network.HttpClientFactory
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import org.chromium.net.CronetEngine
import java.util.concurrent.Executors

@UnstableApi
object PlayerDataSourceFactory {

    @Volatile
    private var mCronetEngineInstance: CronetEngine? = null

    @Volatile
    private var mOkHttpClient: OkHttpClient? = null

    private val _requestProperties = mapOf(
        "referer" to HttpClientFactory.REFERER,
        "User-Agent" to HttpClientFactory.USER_AGENT
    )

    fun build(context: Context): DataSource.Factory {
        val upstreamDataSourceFactory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val cronetEngine = mCronetEngineInstance ?: synchronized(this) {
                mCronetEngineInstance ?: buildCronetEngine(context)
                    .also { mCronetEngineInstance = it }
            }
            CronetDataSource.Factory(cronetEngine, Executors.newFixedThreadPool(5)).apply {
                setDefaultRequestProperties(_requestProperties)
            }
        } else {
            val okHttpClient = mOkHttpClient ?: synchronized(this) {
                mOkHttpClient ?: buildOkHttpClient().also { mOkHttpClient = it }
            }
            OkHttpDataSource.Factory(okHttpClient).apply {
                setDefaultRequestProperties(_requestProperties)
            }
        }
        val simpleCache = CacheManager.getSimpleCache(context)
        return CacheDataSource.Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(upstreamDataSourceFactory)
            .setCacheWriteDataSinkFactory(
                CacheDataSink.Factory()
                    .setCache(simpleCache)
                    .setFragmentSize(50 * 1024 * 1024)
            )
    }

    private fun buildOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectionSpecs(listOf(
                ConnectionSpec.MODERN_TLS,
                ConnectionSpec.CLEARTEXT
            ))
            .build()
    }

    private fun buildCronetEngine(context: Context): CronetEngine {
        return CronetEngine.Builder(context)
            .enableHttp2(true)
            .enableQuic(true)
            .enableBrotli(true)
            .enableHttpCache(CronetEngine.Builder.HTTP_CACHE_IN_MEMORY, 100 * 1024 * 1024)
            .build()
    }
}
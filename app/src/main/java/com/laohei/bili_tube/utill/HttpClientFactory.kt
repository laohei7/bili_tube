package com.laohei.bili_tube.utill

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import org.chromium.net.CronetEngine
import java.io.File

object HttpClientFactory {
    val client: HttpClient by lazy {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json()
            }
            defaultRequest {
                header(
                    HttpHeaders.UserAgent,
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36"
                )
                header(
                    HttpHeaders.Referrer,
                    "https://www.bilibili.com"
                )
            }
        }
    }

    val coilClient by lazy { HttpClient(OkHttp) }

    @Volatile
    private var mCronetEngineInstance: CronetEngine? = null

    @Volatile
    private var mSimpleCacheInstance: SimpleCache? = null

    fun getCronetEngine(context: Context): CronetEngine {
        return mCronetEngineInstance ?: synchronized(this) {
            mCronetEngineInstance ?: buildCronetEngine(context).also { mCronetEngineInstance = it }
        }
    }

    private fun buildCronetEngine(context: Context): CronetEngine {
        return CronetEngine.Builder(context)
            .enableHttp2(true)
            .enableQuic(true)
            .enableHttpCache(CronetEngine.Builder.HTTP_CACHE_IN_MEMORY, 50 * 1024 * 1024)
            .build()
    }

    @OptIn(UnstableApi::class)
    fun getSimpleCache(context: Context): SimpleCache {
        return mSimpleCacheInstance ?: synchronized(this) {
            mSimpleCacheInstance ?: buildSimpleCache(context).also { mSimpleCacheInstance = it }
        }
    }

    @OptIn(UnstableApi::class)
    private fun buildSimpleCache(context: Context): SimpleCache {
        return SimpleCache(
            File(context.cacheDir, "media_cache"),
            LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024),
            StandaloneDatabaseProvider(context)
        )
    }

}
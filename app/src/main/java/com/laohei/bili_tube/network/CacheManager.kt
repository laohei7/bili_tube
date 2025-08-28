package com.laohei.bili_tube.network

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@UnstableApi
object CacheManager {
    @Volatile
    private var mSimpleCacheInstance: SimpleCache? = null

    @OptIn(UnstableApi::class)
    fun getSimpleCache(context: Context): SimpleCache {
        return mSimpleCacheInstance ?: synchronized(this) {
            mSimpleCacheInstance
                ?: buildSimpleCache(context).also { mSimpleCacheInstance = it }
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


    fun release() {
        synchronized(this) {
            mSimpleCacheInstance?.release()
            mSimpleCacheInstance = null
        }
    }
}
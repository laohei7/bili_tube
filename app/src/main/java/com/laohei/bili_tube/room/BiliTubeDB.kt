package com.laohei.bili_tube.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.laohei.bili_tube.room.converter.RoomTypeConverters
import com.laohei.bili_tube.room.dao.BiliSharedSourceDao
import com.laohei.bili_tube.room.dao.DownloadTaskDao
import com.laohei.bili_tube.room.dao.SearchHistoryDao
import com.laohei.bili_tube.room.entity.BiliAudioUrl
import com.laohei.bili_tube.room.entity.BiliVideoUrl
import com.laohei.bili_tube.room.entity.DownloadTask
import com.laohei.bili_tube.room.entity.SearchHistory

@Database(
    entities = [
        SearchHistory::class,
        DownloadTask::class,
        BiliVideoUrl::class,
        BiliAudioUrl::class
    ],
    version = 1
)
@TypeConverters(RoomTypeConverters::class)
abstract class BiliTubeDB : RoomDatabase() {
    abstract fun downloadTaskDao(): DownloadTaskDao
    abstract fun biliSharedSourceDao(): BiliSharedSourceDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: BiliTubeDB? = null

        fun getInstance(context: Context): BiliTubeDB {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): BiliTubeDB {
            return Room.databaseBuilder(
                context.applicationContext,
                BiliTubeDB::class.java,
                "bili_tube.db"
            ).build()
        }
    }
}
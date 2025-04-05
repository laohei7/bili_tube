package com.laohei.bili_tube.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.laohei.bili_tube.core.room.RoomTypeConverters
import com.laohei.bili_tube.db.dao.DownloadTaskDao
import com.laohei.bili_tube.model.DownloadTask

@Database(entities = [DownloadTask::class], version = 1)
@TypeConverters(RoomTypeConverters::class)
abstract class BiliTubeDB : RoomDatabase() {
    abstract fun downloadTaskDao(): DownloadTaskDao

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
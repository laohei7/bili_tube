package com.laohei.bili_tube.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.laohei.bili_tube.data.local.room.entity.BiliAudioUrl
import com.laohei.bili_tube.data.local.room.entity.BiliSharedSource
import com.laohei.bili_tube.data.local.room.entity.BiliVideoUrl

@Dao
interface BiliSharedSourceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addVideo(item: BiliVideoUrl)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addVideos(items: List<BiliVideoUrl>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addAudio(item: BiliAudioUrl)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addAudios(items: List<BiliAudioUrl>)

    @Query("SELECT * FROM tb_video_urls WHERE bvid=:bvid")
    fun getVideoUrlByBvid(bvid: String): List<BiliVideoUrl>

    @Query("SELECT * FROM tb_audio_urls WHERE bvid=:bvid")
    fun getAudioUrlByBvid(bvid: String): List<BiliAudioUrl>

    @Transaction
    @Query("SELECT DISTINCT * FROM tb_video_urls")
    fun exportBiliSharedSource(): List<BiliSharedSource>
}
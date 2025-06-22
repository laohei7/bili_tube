package com.laohei.bili_tube.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.laohei.bili_tube.model.BiliAudioUrl
import com.laohei.bili_tube.model.BiliSharedSource
import com.laohei.bili_tube.model.BiliVideoUrl

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
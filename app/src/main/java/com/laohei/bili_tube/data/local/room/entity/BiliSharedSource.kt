package com.laohei.bili_tube.data.local.room.entity

import androidx.room.Entity
import androidx.room.Relation
import kotlinx.serialization.Serializable

@Serializable
data class BiliSharedSource(
    val bvid: String,
    val aid: Long?,
    val cid: Long?,
    @Relation(
        parentColumn = "bvid",
        entityColumn = "bvid",
        entity = BiliVideoUrl::class,
        projection = ["url"]
    )
    val videoUrls: List<String>,
    @Relation(
        parentColumn = "bvid",
        entityColumn = "bvid",
        entity = BiliAudioUrl::class,
        projection = ["url"]
    )
    val audioUrls: List<String>
)

@Entity(
    tableName = "tb_video_urls",
    primaryKeys = ["bvid", "quality"]
)
data class BiliVideoUrl(
    val bvid: String,
    val aid: Long?,
    val cid: Long?,
    val quality: Int,
    val url: String
)

@Entity(
    tableName = "tb_audio_urls",
    primaryKeys = ["bvid", "quality"]
)
data class BiliAudioUrl(
    val bvid: String,
    val aid: Long?,
    val cid: Long?,
    val quality: Int,
    val url: String
)
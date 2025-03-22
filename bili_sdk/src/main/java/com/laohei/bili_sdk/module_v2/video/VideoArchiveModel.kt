package com.laohei.bili_sdk.module_v2.video

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class VideoArchiveModel(
    val aids: List<Long>,
    val archives: List<ArchiveItem>,
    val meta: ArchiveMeta,
    val page: ArchivePage
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ArchiveItem(
    val aid: Long,
    val bvid: String,
    val ctime: Long,
    val duration: Long,
    val pic: String,
    @SerialName("playback_position") val playbackPosition: Long,
    val pubdate: Long,
    val stat: VideoStat,
    val title: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ArchiveMeta(
    val category: Long,
    val cover: String,
    val description: String,
    val mid: Long,
    val name: String,
    val ptime: Long,
    @SerialName("season_id") val seasonId: Long,
    val total: Long
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ArchivePage(
    @SerialName("page_num") val pageNum: Int,
    @SerialName("page_size") val pageSize: Int,
    val total: Int
)
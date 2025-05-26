package com.laohei.bili_sdk.module_v2.video

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class VideoURLModel(
    val quality: Int,
    @SerialName("timelength") val timeLength: Long,
    @SerialName("accept_description") val acceptDescription: List<String>,
    @SerialName("accept_quality") val acceptQuality: List<Int>,
    @SerialName("video_codecid") val videoCodecId: Int,
    val durl: List<DurlModel>? = null,
    val dash: DashModel? = null,
    @SerialName("support_formats") val supportFormats: List<SupportFormatItem>,
    @SerialName("last_play_time") val lastPlayTime: Long = 0,
    @SerialName("last_play_cid") val lastPlayCid: Long? = null
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class DashModel(
    val duration: Long,
    val video: List<DashItem>,
    val audio: List<DashItem>,
    val dolby: DolbyModel? = null,
    val flac: FlacModel? = null
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class DolbyModel(
    val type: Int,
    val audio: List<DashItem>? = null,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class FlacModel(
    val display: Boolean,
    val audio: DashItem? = null
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class DashItem(
    val id: Int,
    @SerialName("base_url") val baseUrl: String,
    @SerialName("backup_url") val backupUrl: List<String>,
    @SerialName("mime_type") val mimeType: String,
    val codecs: String,
    val width: Int,
    val height: Int,
    @SerialName("frame_rate") val frameRate: String,
    @SerialName("segment_base") val segmentBase: SegmentBase,
    @SerialName("codecid") val codecId: Int
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class SegmentBase(
    val initialization: String,
    @SerialName("index_range") val indexRange: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class SupportFormatItem(
    val quality: Int,
    val format: String,
    @SerialName("new_description") val newDescription: String,
    @SerialName("display_desc") val displayDesc: String,
    val superscript: String,
    val codecs: List<String>?
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class DurlModel(
    val order: Int,
    val length: Int,
    val size: Int,
    val ahead: String,
    val vhead: String,
    val url: String,
    @SerialName("backup_url") val backupUrl: List<String>
)
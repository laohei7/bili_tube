package com.laohei.bili_sdk.model


import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class BiliVideoURL(
    val from: String = "",
    val result: String = "",
    val message: String? = null,
    val quality: Int = -1,
    val format: String = "",
    val timelength: Int = 0,
    @SerialName("accept_format") val acceptFormat: String = "",
    @SerialName("accept_description") val acceptDescription: List<String> = emptyList(),
    @SerialName("accept_quality") val acceptQuality: List<Int> = emptyList(),
    @SerialName("video_codecid") val videoCodecId: Int = -1,
    @SerialName("seek_param") val seekParam: String = "",
    @SerialName("seek_type") val seekType: String = "",
    val durl: List<Durl> = emptyList(),
    val dash: Dash? = null,
    @SerialName("support_formats") val supportFormats: List<SupportFormat> = emptyList(),
    @SerialName("high_format") val highFormat: String? = null,
    @SerialName("last_play_time") val lastPlayTime: Int = 0,
    @SerialName("last_play_cid") val lastPlayCid: Long = -1,
    @SerialName("view_info") val viewInfo: String? = null,
    val volume: Volume? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Durl(
    val order: Int,
    val length: Int,
    val size: Int,
    val ahead: String,
    val vhead: String,
    val url: String,
    @SerialName("backup_url") val backupUrl: List<String>
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class SupportFormat(
    val quality: Int,
    val format: String,
    @SerialName("new_description") val newDescription: String,
    @SerialName("display_desc") val displayDesc: String,
    val superscript: String,
    val codecs: List<String>? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Volume(
    @SerialName("measured_i") val measuredI: Double,
    @SerialName("measured_lra") val measuredLra: Double,
    @SerialName("measured_tp") val measuredTp: Double,
    @SerialName("measured_threshold") val measuredThreshold: Double,
    @SerialName("target_offset") val targetOffset: Double,
    @SerialName("target_i") val targetI: Int,
    @SerialName("target_tp") val targetTp: Int,
    @SerialName("multi_scene_args") val multiSceneArgs: MultiSceneArgs
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class MultiSceneArgs(
    @SerialName("high_dynamic_target_i") val highDynamicTargetI: String,
    @SerialName("normal_target_i") val normalTargetI: String,
    @SerialName("undersized_target_i") val undersizedTargetI: String
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Dash(
    val duration: Int = 0,
    @SerialName("min_buffer_time") val minBufferTime: Double = 0.0,
    val video: List<Video> = emptyList(),
    val audio: List<Audio> = emptyList(),
    val dolby: Dolby? = null,
    val flac: Flac? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Flac(
    val display: Boolean,
    val audio: String? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Video(
    val id: Int,
    @SerialName("base_url") val baseUrl: String? = null,
    @SerialName("backup_url") val backupUrl: List<String> = emptyList(),
    val bandwidth: Int,
    @SerialName("mime_type") val mimeType: String,
    val codecs: String,
    val width: Int,
    val height: Int,
    @SerialName("frame_rate") val frameRate: String,
    val sar: String,
    @SerialName("start_with_sap") val startWithSap: Int,
    @SerialName("segment_base") val segmentBase: SegmentBase,
    val codecid: Int
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Audio(
    val id: Int,
    @SerialName("base_url") val baseUrl: String? = null,
    @SerialName("backup_url") val backupUrl: List<String> = emptyList(),
    val bandwidth: Int,
    @SerialName("mime_type") val mimeType: String,
    val codecs: String,
    val width: Int,
    val height: Int,
    @SerialName("frame_rate") val frameRate: String? = null,
    val sar: String? = null,
    @SerialName("start_with_sap") val startWithSap: Int,
    @SerialName("segment_base") val segmentBase: SegmentBase? = null,
    val codecid: Int
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class SegmentBase(
    val initialization: String,
    @SerialName("index_range") val indexRange: String
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Dolby(
    val type: Int,
    val audio: String? = null
)
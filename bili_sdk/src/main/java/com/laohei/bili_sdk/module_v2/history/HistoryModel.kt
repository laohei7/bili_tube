package com.laohei.bili_sdk.module_v2.history

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class HistoryModel(
    val cursor: HistoryCursor,
    val list: List<HistoryItem>
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class HistoryCursor(
    val max: Long,
    @SerialName("view_at") val viewAt: Long,
    val business: String,
    val ps: Int
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class HistoryItem(
    val title: String,
    val cover: String,
    val uri: String,
    @SerialName("author_name") val authorName: String,
    @SerialName("author_face") val authorFace: String,
    @SerialName("author_mid") val authorMid: Long,
    @SerialName("view_at") val viewAt: Long,
    val progress: Long,
    val badge: String,
    val duration: Long,
    val kid: Long,
    @SerialName("tag_name") val tagName: String,
    val videos: Int,
    val history: HistoryVideoInfo
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class HistoryVideoInfo(
    val oid: Long,
    val epid: Long,
    val bvid: String,
    val page: Int,
    val cid: Long,
    val part: String,
    val business: String,
    val dt: Int
)
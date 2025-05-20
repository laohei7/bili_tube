package com.laohei.bili_sdk.module_v2.user

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class UploadedVideoModel(
    val count: Int,
    @SerialName("has_next") val hasNext: Boolean,
    @SerialName("has_prev") val hasPrev: Boolean,
    @SerialName("item") val items: List<UploadedVideoItem>
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class UploadedVideoItem(
    val title: String,
    val subtitle: String,
    val tname: String,
    val cover: String,
    val uri: String,
    @SerialName("param") val aid: String,
    val goto: String,
    val duration: Long,
    val play: Long,
    val danmaku: Long,
    val ctime: Long,
    val author: String,
    val bvid: String,
    val videos: Int,
    @SerialName("first_cid") val cid: Long,
    @SerialName("view_content") val viewContent: String,
    @SerialName("publish_time_text") val publishTimeText: String
)

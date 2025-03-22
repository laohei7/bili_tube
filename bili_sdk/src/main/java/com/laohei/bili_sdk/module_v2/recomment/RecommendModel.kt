package com.laohei.bili_sdk.module_v2.recomment

import com.laohei.bili_sdk.module_v2.video.VideoOwner
import com.laohei.bili_sdk.module_v2.video.VideoStat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class RecommendModel(
    val item: List<RecommendItem>,
    val mid: Long
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class RecommendItem(
    val id: Long,
    val bvid: String,
    val cid: Long,
    val goto: String,
    val uri: String,
    val pic: String,
    @SerialName("pic_4_3") val pic43: String,
    val title: String,
    val duration: Long,
    @SerialName("pubdate") val pubDate: Long,
    val owner: VideoOwner? = null,
    val stat: VideoStat? = null,
    @SerialName("is_followed") val isFollowed: Int
)
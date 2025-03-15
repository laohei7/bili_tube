package com.laohei.bili_sdk.module_v2.video

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class VideoDetailModel(
    @SerialName("View") val view: VideoView,
    @SerialName("Card") val card: VideoCard,
    @SerialName("Tags") val tags: List<VideoTagItem>,
    @SerialName("Related") val related: List<VideoView>
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class VideoView(
    val bvid: String,
    val aid: Long,
    val cid: Long,
    val pic: String,
    val title: String,
    val pubdate: Long,
    val ctime: Long,
    val desc: String,
    val duration: Long,
    val owner: VideoOwner,
    val stat: VideoStat,
    val dimension: VideoDimension
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class VideoOwner(
    val mid: Long,
    val name: String,
    val face: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class VideoStat(
    val aid: Long,
    val view: Long,
    val danmaku: Long,
    val reply: Long,
    val favorite: Long,
    val coin: Long,
    val share: Long,
    val like: Long,
    val dislike: Long
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class VideoDimension(
    val width: Int,
    val height: Int,
    val rotate: Int
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class VideoCard(
    val card: VideoCardContent,
    @SerialName("like_num") val likeNum: Long
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class VideoCardContent(
    val mid: String,
    val name: String,
    val sex: String,
    val rank: String,
    val face: String,
    val fans: Long,
    val friend: Long,
    val attention: Long
)


@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class VideoTagItem(
    @SerialName("tag_id") val tagId: Long,
    @SerialName("tag_name") val tagName: String,
    @SerialName("music_id") val musicId: String,
    @SerialName("tag_type") val tagType: String,
    @SerialName("jump_url") val jumpUrl: String
)
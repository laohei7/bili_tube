package com.laohei.bili_sdk.model.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)  // 启用实验性API
@Serializable
@JsonIgnoreUnknownKeys  // 忽略JSON中未知字段
data class Basic(
    @SerialName("comment_id_str") val commentIdStr: String,
    @SerialName("comment_type") val commentType: Int,
    @SerialName("like_icon") val likeIcon: LikeIcon,
    @SerialName("rid_str") val ridStr: String
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class LikeIcon(
    @SerialName("action_url") val actionUrl: String,
    @SerialName("end_url") val endUrl: String,
    @SerialName("id") val id: Int,
    @SerialName("start_url") val startUrl: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class DynamicItem(
    @SerialName("basic") val basic: Basic,
    @SerialName("id_str") val idStr: String,
    @SerialName("modules") val modules: BiliModule,
    @SerialName("type") val type: String,
    @SerialName("visible") val visible: Boolean
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class DynamicData(
    @SerialName("has_more") val hasMore: Boolean = false,
    val items: List<DynamicItem> = emptyList(),
    val offset: Long = 0L,
    @SerialName("update_baseline") val updateBaseline: Long = 0L,
    @SerialName("update_num") val updateNum: Long = 0L
)


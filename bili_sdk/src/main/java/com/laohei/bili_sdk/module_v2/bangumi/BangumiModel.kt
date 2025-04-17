package com.laohei.bili_sdk.module_v2.bangumi

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class BangumiModel(
    @SerialName("has_next") val hasNext: Int,
    val num: Int,
    val size: Int,
    val total: Int,
    val list: List<BangumiItem>
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class BangumiItem(
    val cover: String,
    @SerialName("first_ep") val firstEp: FirstEpisode,
    @SerialName("index_show") val indexShow: String,
    @SerialName("is_finish") val isFinish: Int,
    val link: String,
    @SerialName("media_id") val mediaId: Long,
    val order: String,
    @SerialName("order_type") val orderType: String,
    val score: String,
    @SerialName("season_id") val seasonId: Long,
    @SerialName("season_status") val seasonStatus: Int,
    @SerialName("season_type") val seasonType: Int,
    val subTitle: String,
    val title: String,
    @SerialName("title_icon") val titleIcon: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class FirstEpisode(
    val cover: String,
    @SerialName("ep_id") val epId: Long,
)

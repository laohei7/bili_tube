package com.laohei.bili_sdk.module_v2.bangumi

import com.laohei.bili_sdk.module_v2.video.NewEpModel
import com.laohei.bili_sdk.module_v2.video.RatingModel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class RelatedBangumiModel(
    val season: List<RelatedBangumiItem>
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class RelatedBangumiItem(
    val actor: String,
    val cover: String,
    @SerialName("new_ep") val newEp: NewEpModel,
    val rating: RatingModel? = null,
    @SerialName("rcmd_reason") val rcmdReason: String,
    @SerialName("season_id") val seasonId: Long,
    @SerialName("season_type") val seasonType: Int,
    val stat: BangumiStat2,
    val styles: List<StyleItem>,
    val subtitle: String,
    val title: String,
    val url: String,
    @SerialName("user_status") val userStatus: UserStatusModel
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class BangumiStat2(
    val view: Long = 0L,
    val follow: Long = 0L,
    val danmaku: Long = 0L,
    val vtForUnity: Long = 0L,
    val vt: Long = 0L,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class StyleItem(
    val id: Int,
    val name: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class UserStatusModel(
    val follow: Int = 0
)
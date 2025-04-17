package com.laohei.bili_sdk.module_v2.video

import com.laohei.bili_sdk.module_v2.common.Dimension
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class BangumiDetailModel(
    val actors: String,
    val cover: String,
    val evaluate: String,
    @SerialName("media_id") val mediaId: Long,
    val mode: Int,
    val record: String,
    @SerialName("season_id") val seasonId: Long,
    @SerialName("season_title") val seasonTitle: String,
    val staff: String,
    val stat: BangumiStat,
    val styles: List<String>,
    val subtitle: String,
    val title: String,
    val total: Int,
    val areas: List<AreaModel>,
    val episodes: List<EpisodeModel>,
    val publish: PublishModel,
    val rating: RatingModel,
    val seasons: List<SeasonModel>
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class BangumiStat(
    val views: Long = 0L,
    val vt: Long = 0L,
    val danmakus: Long = 0L,
    val reply: Long = 0L,
    val favorite: Long = 0L,
    val favorites: Long = 0L,
    val coins: Long = 0L,
    val share: Long = 0L,
    val likes: Long = 0L,
    @SerialName("follow_text") val followText: String = "0L",
)


@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class AreaModel(
    val id: Int,
    val name: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class EpisodeModel(
    val aid: Long,
    val bvid: String,
    val cid: Long,
    val cover: String,
    val dimension: Dimension,
    val duration: Long,
    @SerialName("ep_id") val epId: Long,
    val from: String,
    val id: Long,
    @SerialName("long_title") val longTitle: String,
    @SerialName("pub_time") val pubTime: Long,
    @SerialName("share_url") val shareUrl: String,
    @SerialName("short_link") val shortLink: String,
    @SerialName("show_title") val showTitle: String,
    val skip: Map<String, SkipModel>? = null,
    val subtitle: String,
    val title: String,
    val vid: String,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class SkipModel(
    val end: Long,
    val start: Long
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class PublishModel(
    @SerialName("is_finish") val isFinish: Int,
    @SerialName("is_started") val isStarted: Int,
    @SerialName("pub_time") val pubTime: String,
    @SerialName("pub_time_show") val pubTimeShow: String,
    val weekday: Int
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class RatingModel(
    val count: Long,
    val score: Float
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class SeasonModel(
    val cover: String,
    @SerialName("media_id") val mediaId: Long,
    @SerialName("new_ep") val newEp: NewEpModel,
    @SerialName("season_id") val seasonId: Long,
    @SerialName("season_title") val seasonTitle: String,
    val stat: SeasonStat
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class NewEpModel(
    val cover: String,
    val id: Long = -1,
    @SerialName("index_show") val indexShow: String
)


@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class SeasonStat(
    val views: Long = 0L,
    val vt: Long = 0L,
    val favorites: Long = 0L,
    @SerialName("series_follow") val seriesFollow: Long = 0L
)
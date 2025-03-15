package com.laohei.bili_sdk.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys


@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class BiliAnimeSchedule(
    val date: String,
    @SerialName("date_ts") val dateTs: Long,
    @SerialName("day_of_week") val dayOfWeek: Int,
    val episodes: List<Episode>,
    @SerialName("is_today") val isToday: Int
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Episode(
    val cover: String,
    val delay: Int,
    @SerialName("delay_id") val delayId: Int,
    @SerialName("delay_index") val delayIndex: String,
    @SerialName("delay_reason") val delayReason: String,
    @SerialName("enable_vt") val enableVt: Boolean,
    @SerialName("ep_cover") val epCover: String,
    @SerialName("episode_id") val episodeId: Int,
    val follows: String,
    @SerialName("icon_font") val iconFont: IconFont,
    val plays: String,
    @SerialName("pub_index") val pubIndex: String,
    @SerialName("pub_time") val pubTime: String,
    @SerialName("pub_ts") val pubTs: Long,
    val published: Int,
    @SerialName("season_id") val seasonId: Int,
    @SerialName("square_cover") val squareCover: String,
    val title: String
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class IconFont(
    val name: String,
    val text: String
)

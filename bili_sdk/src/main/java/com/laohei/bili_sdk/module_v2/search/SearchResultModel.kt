package com.laohei.bili_sdk.module_v2.search

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class SearchResultModel(
    val seid: String,
    val page: Int,
    @SerialName("pagesize") val pageSize: Int,
    val next: Int,
    val numResults: Int,
    val numPages: Int,
    @SerialName("rqt_type") val rqtType: String,
    @SerialName("pageinfo") val pageInfo: Map<String, PageInfo>,
    val result: List<SearchResultItem>
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class SearchResultModel2(
    val seid: String,
    val page: Int,
    @SerialName("pagesize") val pageSize: Int,
    val next: Int? = null,
    val numResults: Int,
    val numPages: Int,
    @SerialName("rqt_type") val rqtType: String,
//    @SerialName("pageinfo") val pageInfo: Map<String, PageInfo>,
    val result: List<SearchResultItemType>
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class PageInfo(
    val total: Long,
    val numResults: Long,
    val pages: Int
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class SearchResultItem(
    @SerialName("result_type") val resultType: String,
    val data: List<SearchResultItemType>
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
@JsonClassDiscriminator("type")
sealed class SearchResultItemType {

    companion object {
        const val TYPE_VIDEO = "video"
        const val TYPE_MEDIA_BANGUMI = "media_bangumi"
        const val TYPE_MEDIA_FT = "media_ft"
        const val TYPE_UNKNOWN = "unknown"
    }

    fun getType(): String {
        return when (this) {
            is MediaBangumiItem -> TYPE_MEDIA_BANGUMI
            is MediaFTItem -> TYPE_MEDIA_FT
            UnknownItem -> TYPE_UNKNOWN
            is VideoItem -> TYPE_VIDEO
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonIgnoreUnknownKeys
    @SerialName("video")
    data class VideoItem(
        val id: Long,
        val author: String,
        val mid: Long,
        @SerialName("typeid") val typeId: String,
        val typename: String,
        @SerialName("arcurl") val arcUrl: String,
        val aid: Long,
        val bvid: String,
        val title: String,
        val description: String,
        val pic: String,
        val play: Long,
        @SerialName("video_review") val videoReview: Long,
        val favorites: Long,
        val tag: String,
        val duration: String,
        val review: Long,
        @SerialName("pubdate") val pubDate: Long,
        @SerialName("senddate") val sendDate: Long,
        @SerialName("rank_score") val rankScore: Long,
        val like: Long,
        val upic: String,
        val danmaku: Long
    ) : SearchResultItemType()

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonIgnoreUnknownKeys
    @SerialName("media_bangumi")
    data class MediaBangumiItem(
        @SerialName("media_id") val mediaId: Long,
        val title: String,
        @SerialName("org_title") val orgTitle: String,
        @SerialName("media_type") val mediaType: Int,
        val cv: String,
        val staff: String,
        @SerialName("season_id") val seasonId: Long,
        @SerialName("is_avid") val isAvid: Boolean,
        @SerialName("season_type") val seasonType: Int,
        @SerialName("season_type_name") val seasonTypeName: String,
        @SerialName("ep_size") val epSize: Int,
        val url: String,
        val eps: List<EpisodeItem>? = null,
        val cover: String,
        val areas: String,
        val styles: String,
        @SerialName("goto_url") val gotoUrl: String,
        val desc: String,
        @SerialName("pubtime") val pubTime: Long,
        @SerialName("media_score") val mediaScore: MediaScore,
        @SerialName("index_show") val indexShow: String
    ) : SearchResultItemType()

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonIgnoreUnknownKeys
    @SerialName("media_ft")
    data class MediaFTItem(
        @SerialName("media_id") val mediaId: Long,
        val title: String,
        @SerialName("org_title") val orgTitle: String,
        @SerialName("media_type") val mediaType: Int,
        val cv: String,
        val staff: String,
        @SerialName("season_id") val seasonId: Long,
        @SerialName("is_avid") val isAvid: Boolean,
        @SerialName("season_type") val seasonType: Int,
        @SerialName("season_type_name") val seasonTypeName: String,
        @SerialName("ep_size") val epSize: Int,
        val url: String,
        val eps: List<EpisodeItem>? = null,
        val cover: String,
        val areas: String,
        val styles: String,
        @SerialName("goto_url") val gotoUrl: String,
        val desc: String,
        @SerialName("pubtime") val pubTime: Long,
        @SerialName("media_score") val mediaScore: MediaScore,
        @SerialName("index_show") val indexShow: String
    ) : SearchResultItemType()

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonIgnoreUnknownKeys
    data object UnknownItem : SearchResultItemType()
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class EpisodeItem(
    val id: Long,
    val cover: String,
    val title: String,
    val url: String,
    @SerialName("release_date") val releaseDate: String,
    @SerialName("index_title") val indexTitle: String,
    @SerialName("long_title") val longTitle: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class MediaScore(
    val score: Float,
    @SerialName("user_count") val userCount: Long
)
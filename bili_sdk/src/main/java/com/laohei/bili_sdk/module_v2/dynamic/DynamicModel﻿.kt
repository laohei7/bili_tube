package com.laohei.bili_sdk.module_v2.dynamic

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class DynamicModel(
    @SerialName("has_more") val hasMore: Boolean,
    val items: List<DynamicItem>? = null,
    val offset: Long,
    @SerialName("update_baseline") val updateBaseline: String,
    @SerialName("update_num") val updateNum: Long
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class DynamicItem(
    val visible: Boolean,
    val type: String,
    val modules: DynamicModules,
    @SerialName("id_str") val idStr: String
) {
    companion object {
        const val DYNAMIC_TYPE_AV = "DYNAMIC_TYPE_AV"
        const val DYNAMIC_TYPE_DRAW = "DYNAMIC_TYPE_DRAW"
        const val DYNAMIC_TYPE_FORWARD = "DYNAMIC_TYPE_FORWARD"
        const val DYNAMIC_TYPE_WORD = "DYNAMIC_TYPE_WORD"
        const val DYNAMIC_TYPE_PGC_UNION = "DYNAMIC_TYPE_PGC_UNION"
        const val DYNAMIC_TYPE_ARTICLE = "DYNAMIC_TYPE_ARTICLE"
        const val DYNAMIC_TYPE_COMMON_SQUARE = "DYNAMIC_TYPE_COMMON_SQUARE"
        const val DYNAMIC_TYPE_LIVE_RCMD = "DYNAMIC_TYPE_LIVE_RCMD"
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class DynamicModules(
    @SerialName("module_author") val moduleAuthor: ModuleAuthor,
    @SerialName("module_dynamic") val moduleDynamic: ModuleDynamic,
    @SerialName("module_stat") val moduleStat: ModuleStat
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ModuleAuthor(
    val face: String,
    val label: String,
    val mid: Long,
    val name: String,
    @SerialName("pub_time") val pubTime: String,
    @SerialName("pub_ts") val pubTs: Long
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ModuleDynamic(
    val desc: DynamicDesc? = null,
    val major: DynamicMajor? = null,
    val topic: DynamicTopic? = null
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class DynamicTopic(
    val id: Long? = null,
    @SerialName("jump_url") val jumpUrl: String? = null,
    val name: String? = null
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ModuleStat(
    val comment: ModuleStatItem,
    val forward: ModuleStatItem,
    val like: ModuleStatItem
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ModuleStatItem(
    val count: Long,
    val forbidden: Boolean
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class DynamicDesc(
    val text: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class DynamicMajor(
    val draw: MajorDraw? = null,
    val type: String,
    val archive: MajorArchive? = null,
    val common: MajorCommon? = null,
    val article: MajorArticle? = null,
    @SerialName("live_rcmd") val liveRcmd: MajorLiveRcmd? = null,
) {
    companion object {
        const val TYPE_MAJOR_TYPE_DRAW = "MAJOR_TYPE_DRAW"
        const val TYPE_MAJOR_TYPE_ARTICLE = "MAJOR_TYPE_ARTICLE"
        const val TYPE_MAJOR_TYPE_COMMON = "MAJOR_TYPE_COMMON"
        const val TYPE_MAJOR_TYPE_LIVE_RCMD = "MAJOR_TYPE_LIVE_RCMD"
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class MajorDraw(
    val id: Long,
    val items: List<MajorDrawItem>
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class MajorDrawItem(
    val height: Int,
    val size: Float,
    val src: String,
    val width: Int,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class MajorArchive(
    val aid: String,
    val bvid: String,
    val cover: String,
    val desc: String,
    @SerialName("duration_text") val durationText: String,
    val title: String,
    val stat: MajorArchiveStat
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class MajorArchiveStat(
    val danmaku: String,
    val play: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class MajorCommon(
    val cover: String,
    val desc: String,
    val id: String,
    @SerialName("jump_url") val jumpUrl: String,
    val label: String,
    val title: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class MajorArticle(
    val covers: List<String>,
    val desc: String,
    val id: Long,
    @SerialName("jump_url") val jumpUrl: String,
    val label: String,
    val title: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class MajorLiveRcmd(
    @SerialName("reserve_type") val reserveType: Int,
    val content: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class MajorLiveRcmdContent(
    val type: Int,
    @SerialName("live_play_info") val livePlayInfo: LivePlayInfo
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class LivePlayInfo(
    @SerialName("parent_area_id") val parentAreaId: Int,
    val link: String,
    @SerialName("parent_area_name") val parentAreaName: String,
    @SerialName("room_id") val roomId: Long,
    val uid: Long,
    @SerialName("live_status") val liveStatus: Int,
    val title: String,
    val cover: String,
    @SerialName("area_id") val areaId: Long,
    @SerialName("live_id") val liveId: String,
    val online: Long,
    @SerialName("area_name") val areaName: String,
    @SerialName("live_start_time") val liveStartTime: Long,
    @SerialName("watched_show") val watchedShow: WatchedShow
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class WatchedShow(
    @SerialName("text_small") val textSmall: String,
    @SerialName("text_large") val textLarge: String,
    val icon: String,
    @SerialName("icon_web") val iconWeb: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class MajorTopic(
    val id: Long,
    @SerialName("jump_url") val jumpUrl: String,
    val name: String
)
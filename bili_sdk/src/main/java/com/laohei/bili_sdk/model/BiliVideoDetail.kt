package com.laohei.bili_sdk.model

import com.laohei.bili_sdk.model.common.Dimension
import com.laohei.bili_sdk.model.common.Owner
import com.laohei.bili_sdk.model.common.Rights
import com.laohei.bili_sdk.model.common.Stat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class BiliVideoDetail(
    @SerialName("View") val view: BiliVideoInfo,
    @SerialName("Card") val card: BiliUserCard,
    @SerialName("Tags") val tags: List<Tag>,
    @SerialName("Reply") val reply: ReplyDetails,
    @SerialName("Related") val related: List<RelatedItem>,
    val spec: String? = null,
    @SerialName("hot_share") val hotShare: HotShare,
    val elec: String? = null,
    val emergency: Emergency,
    @SerialName("view_addit") val viewAddit: Map<String, Boolean>,
    val guide: String? = null,
    @SerialName("query_tags") val queryTags: String? = null,
    @SerialName("is_old_user") val isOldUser: Boolean,
    val participle: List<String>? = null

)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class HotShare(
    val show: Boolean,
    val list: List<String> = emptyList()
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Emergency(
    @SerialName("no_like") val noLike: Boolean,
    @SerialName("no_coin") val noCoin: Boolean,
    @SerialName("no_fav") val noFav: Boolean,
    @SerialName("no_share") val noShare: Boolean
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Tag(
    @SerialName("tag_id") val tagId: Long,
    @SerialName("tag_name") val tagName: String,
    @SerialName("music_id") val musicId: String? = null,
    @SerialName("tag_type") val tagType: String,
    @SerialName("jump_url") val jumpUrl: String? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class ReplyDetails(
    val page: Int? = null,
    val replies: List<Reply>? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Reply(
    @SerialName("rpid") val rpid: Long,
    @SerialName("oid") val oid: Long,
    @SerialName("type") val type: Int,
    @SerialName("mid") val mid: Long,
    @SerialName("root") val root: Long,
    @SerialName("parent") val parent: Long,
    @SerialName("dialog") val dialog: Long,
    @SerialName("count") val count: Int,
    @SerialName("rcount") val rcount: Int,
    @SerialName("state") val state: Int,
    @SerialName("fansgrade") val fansgrade: Int,
    @SerialName("attr") val attr: Int,
    @SerialName("ctime") val ctime: Long,
    @SerialName("like") val like: Int,
    @SerialName("action") val action: Int,
    @SerialName("content") val content: String? = null,
    @SerialName("replies") val replies: List<Reply>? = null,
    @SerialName("assist") val assist: Int,
    @SerialName("show_follow") val showFollow: Boolean
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class RelatedItem(
    @SerialName("aid") val aid: Long,
    @SerialName("videos") val videos: Int,
    @SerialName("tid") val tid: Int,
    @SerialName("tname") val tname: String,
    @SerialName("copyright") val copyright: Int,
    @SerialName("pic") val pic: String,
    @SerialName("title") val title: String,
    @SerialName("pubdate") val pubdate: Long,
    @SerialName("ctime") val ctime: Long,
    @SerialName("desc") val desc: String,
    @SerialName("state") val state: Int,
    @SerialName("duration") val duration: Int,
    @SerialName("rights") val rights: Rights,
    @SerialName("owner") val owner: Owner,
    @SerialName("stat") val stat: Stat,
    @SerialName("dynamic") val dynamic: String,
    @SerialName("cid") val cid: Long,
    @SerialName("dimension") val dimension: Dimension,
    @SerialName("short_link_v2") val shortLinkV2: String,
    @SerialName("up_from_v2") val upFromV2: Int = 0,
    @SerialName("first_frame") val firstFrame: String? = null,
    @SerialName("pub_location") val pubLocation: String? = null,
    @SerialName("cover43") val cover43: String,
    @SerialName("bvid") val bvid: String,
    @SerialName("season_type") val seasonType: Int,
    @SerialName("is_ogv") val isOgv: Boolean,
    @SerialName("ogv_info") val ogvInfo: String?,
    @SerialName("rcmd_reason") val rcmdReason: String,
    @SerialName("enable_vt") val enableVt: Int,
    @SerialName("ai_rcmd") val aiRcmd: AiRcmd
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class AiRcmd(
    @SerialName("id") val id: Long,
    @SerialName("goto") val goto: String,
    @SerialName("trackid") val trackid: String,
    @SerialName("uniq_id") val uniqId: String
)
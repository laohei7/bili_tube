package com.laohei.bili_sdk.model

import com.laohei.bili_sdk.model.common.Dimension
import com.laohei.bili_sdk.model.common.Label
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
data class BiliVideoInfo(
    val bvid: String,
    val aid: Long,
    val videos: Int,
    val tid: Int,
    @SerialName("tid_v2") val tidV2: Int,
    val tname: String,
    @SerialName("tname_v2") val tnameV2: String,
    val copyright: Int,
    val pic: String,
    val title: String,
    val pubdate: Long,
    val ctime: Long,
    val desc: String,
    @SerialName("desc_v2") val descV2: List<DescriptionV2>? = null,
    val state: Int,
    val duration: Int,
    val rights: Rights,
    val owner: Owner,
    val stat: Stat,
    @SerialName("argue_info") val argueInfo: ArgueInfo,
    val dynamic: String,
    val cid: Long,
    val dimension: Dimension,
    val premiere: String? = null,
    @SerialName("teenage_mode") val teenageMode: Int,
    @SerialName("is_chargeable_season") val isChargeableSeason: Boolean,
    @SerialName("is_story") val isStory: Boolean,
    @SerialName("is_upower_exclusive") val isUpowerExclusive: Boolean,
    @SerialName("is_upower_play") val isUpowerPlay: Boolean,
    @SerialName("is_upower_preview") val isUpowerPreview: Boolean,
    @SerialName("enable_vt") val enableVt: Int,
    @SerialName("vt_display") val vtDisplay: String,
    @SerialName("no_cache") val noCache: Boolean,
    val pages: List<Page>,
    val subtitle: Subtitle,
    val label: Label? = null,
    @SerialName("is_season_display") val isSeasonDisplay: Boolean,
    @SerialName("user_garb") val userGarb: UserGarb,
    @SerialName("honor_reply") val honorReply: HonorReply? = null,
    @SerialName("like_icon") val likeIcon: String,
    @SerialName("need_jump_bv") val needJumpBv: Boolean,
    @SerialName("disable_show_up_info") val disableShowUpInfo: Boolean,
    @SerialName("is_story_play") val isStoryPlay: Int,
    @SerialName("is_view_self") val isViewSelf: Boolean
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class DescriptionV2(
    @SerialName("raw_text") val rawText: String,
    val type: Int,
    @SerialName("biz_id") val bizId: Int
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class ArgueInfo(
    @SerialName("argue_msg") val argueMsg: String,
    @SerialName("argue_type") val argueType: Int,
    @SerialName("argue_link") val argueLink: String
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Page(
    val cid: Long,
    val page: Int,
    val from: String,
    val part: String,
    val duration: Int,
    val vid: String,
    val weblink: String,
    val dimension: Dimension,
    @SerialName("first_frame") val firstFrame: String?=null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Subtitle(
    @SerialName("allow_submit") val allowSubmit: Boolean,
    val list: List<SubtitleItem>
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class SubtitleItem(
    val id: String,
    val lan: String,
    @SerialName("lan_doc") val lanDoc: String,
    @SerialName("is_lock") val isLock: Boolean,
    @SerialName("subtitle_url") val subtitleUrl: String,
    val type: Int,
    @SerialName("id_str") val idStr: String,
    @SerialName("ai_type") val aiType: Int,
    @SerialName("ai_status") val aiStatus: Int,
    val author: SubtitleAuthor
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class SubtitleAuthor(
    val mid: Long,
    val name: String,
    val sex: String,
    val face: String,
    val sign: String,
    val rank: Int,
    val birthday: Long,
    @SerialName("is_fake_account") val isFakeAccount: Int,
    @SerialName("is_deleted") val isDeleted: Int,
    @SerialName("in_reg_audit") val inRegAudit: Int,
    @SerialName("is_senior_member") val isSeniorMember: Int
)



@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class UserGarb(
    @SerialName("url_image_ani_cut") val urlImageAniCut: String
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class HonorReply(
    val honor: List<Honor>? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Honor(
    val aid: Long,
    val type: Int,
    val desc: String,
    @SerialName("weekly_recommend_num") val weeklyRecommendNum: Int
)

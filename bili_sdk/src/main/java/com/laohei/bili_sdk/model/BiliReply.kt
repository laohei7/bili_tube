package com.laohei.bili_sdk.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class BiliReply(
    val replies: List<VideoReplyItem>,
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class VideoReplyItem(
    val rpid: Long? = null,
    val oid: Long? = null,
    val type: Long? = null,
    val mid: Long? = null,
    val root: Long? = null,
    val parent: Long? = null,
    val dialog: Long? = null,
    val count: Long? = null,
    val rcount: Long? = null,
    val state: Long? = null,
    @SerialName("fansgrade") val fansGrade: Long? = null,
    val attr: Long? = null,
    val ctime: Long? = null,
    @SerialName("mid_str") val midStr: String? = null,
    @SerialName("oid_str") val oidStr: String? = null,
    @SerialName("rpid_str") val rpidStr: String? = null,
    @SerialName("root_str") val rootStr: String? = null,
    @SerialName("parent_str") val parentStr: String? = null,
    @SerialName("dialog_str") val dialogStr: String? = null,
    val like: Long? = null,
    val action: Long? = null,
    val member: Member? = null,
    val content: Content? = null,
    val replies: List<VideoReplyItem>? = null,
    val assist: Long? = null,
    val upAction: UpAction? = null,
    val invisible: Boolean? = null,
    val replyControl: ReplyControl? = null,
    val folder: Folder? = null,
    @SerialName("dynamic_id_str") val dynamicIdStr: String? = null,
    @SerialName("note_cvid_str") val noteCvidStr: String? = null,
    val trackInfo: String? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Member(
    val mid: String? = null,
    val uname: String? = null,
    val sex: String? = null,
    val sign: String? = null,
    val avatar: String? = null,
    val rank: String? = null,
    @SerialName("face_nft_new") val faceNftNew: Long? = null,
    @SerialName("is_senior_member") val isSeniorMember: Long? = null,
//    val senior: Senior? = null,
    @SerialName("level_info") val levelInfo: LevelInfo? = null,
    val pendant: Pendant? = null,
    val nameplate: Nameplate? = null,
    @SerialName("official_verify") val officialVerify: OfficialVerify? = null,
    val vip: Vip? = null,
    @SerialName("avatar_subscript") val avatarSubscript: Long? = null,
    @SerialName("nickname_color") val nicknameColor: String? = null
)

//@Serializable
//data class Senior(
//    // Assuming it's an empty object
//)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class LevelInfo(
    @SerialName("current_level") val currentLevel: Long? = null,
    @SerialName("current_min") val currentMin: Long? = null,
    @SerialName("current_exp") val currentExp: Long? = null,
    @SerialName("next_exp") val nextExp: Long? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Pendant(
    val pid: Long? = null,
    val name: String? = null,
    val image: String? = null,
    val expire: Long? = null,
    @SerialName("image_enhance") val imageEnhance: String? = null,
    @SerialName("image_enhance_frame") val imageEnhanceFrame: String? = null,
    @SerialName("n_pid") val nPid: Long? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Nameplate(
    val nid: Long? = null,
    val name: String? = null,
    val image: String? = null,
    @SerialName("image_small") val imageSmall: String? = null,
    val level: String? = null,
    val condition: String? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class OfficialVerify(
    val type: Long? = null,
    val desc: String? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Vip(
    @SerialName("vipType") val vipType: Long? = null,
    @SerialName("vipDueDate") val vipDueDate: Long? = null,
    @SerialName("dueRemark") val dueRemark: String? = null,
    @SerialName("accessStatus") val accessStatus: Long? = null,
    @SerialName("vipStatus") val vipStatus: Long? = null,
    @SerialName("vipStatusWarn") val vipStatusWarn: String? = null,
    @SerialName("themeType") val themeType: Long? = null,
    val label: Label? = null,
    @SerialName("avatar_subscript") val avatarSubscript: Long? = null,
    @SerialName("nickname_color") val nicknameColor: String? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Label(
    val path: String? = null,
    val text: String? = null,
    @SerialName("label_theme") val labelTheme: String? = null,
    @SerialName("text_color") val textColor: String? = null,
    @SerialName("bg_style") val bgStyle: Long? = null,
    @SerialName("bg_color") val bgColor: String? = null,
    @SerialName("border_color") val borderColor: String? = null,
    @SerialName("use_img_label") val useImgLabel: Boolean? = null,
    @SerialName("img_label_uri_hans") val imgLabelUriHans: String? = null,
    @SerialName("img_label_uri_hant") val imgLabelUriHant: String? = null,
    @SerialName("img_label_uri_hans_static") val imgLabelUriHansStatic: String? = null,
    @SerialName("img_label_uri_hant_static") val imgLabelUriHantStatic: String? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Content(
    val message: String? = null,
//    val members: List<Any>? = null,
//    val jumpUrl: Any? = null,
    @SerialName("max_line") val maxLine: Long? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class UpAction(
    val like: Boolean? = null,
    val reply: Boolean? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class ReplyControl(
    @SerialName("max_line") val maxLine: Long? = null,
    @SerialName("time_desc") val timeDesc: String? = null,
    val location: String? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Folder(
    @SerialName("has_folded") val hasFolded: Boolean? = null,
    @SerialName("is_folded") val isFolded: Boolean? = null,
    val rule: String? = null
)
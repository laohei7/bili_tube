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
data class BiliHotVideoItem(
    val aid: Long,
    val videos: Int,
    val tid: Int,
    val tname: String,
    val tidv2: Int,
    val tnamev2: String,
    val copyright: Int,
    val pic: String,
    val title: String,
    val pubdate: Long,
    val ctime: Long,
    val desc: String,
    val state: Int,
    val duration: Int,
    @SerialName("mission_id") val missionId: Int = -1,
    val rights: Rights,
    @SerialName("redirect_url") val redirectURL: String? = null,
    val owner: Owner,
    val stat: Stat,
    val dynamic: String,
    val cid: Long,
    val dimension: Dimension,
    @SerialName("season_id") val seasonId: Int = -1,
    @SerialName("short_link_v2") val shortLinkV2: String,
    @SerialName("up_from_v2") val upFromV2: Int = -1,
    @SerialName("first_frame") val firstFrame: String = "",
    @SerialName("pub_location") val pubLocation: String = "",
    val cover43: String,
    val bvid: String,
    @SerialName("season_type") val seasonType: Int,
    @SerialName("is_ogv") val isOgv: Boolean,
    @SerialName("ogv_info") val ogvInfo: OgvInfo? = null,
    @SerialName("enable_vt") val enableVt: Int,
    @SerialName("ai_rcmd") val aiRcmd: String? = null,
    @SerialName("rcmd_reason") val rcmdReason: RcmdReason
)

@Serializable
data class BiliRandomVideoItem(
    val id: Long,
    val bvid: String,
    val cid: Long,
    val goto: String,
    val uri: String,
    val pic: String,
    @SerialName("pic_4_3") val picFourThree: String,
    val title: String,
    val duration: Int,
    val pubdate: Long,
    val owner: Owner,
    val stat: Stat,
    @SerialName("av_feature") val avFeature: String? = null,
    @SerialName("is_followed") val isFollowed: Int,
    @SerialName("rcmd_reason") val rcmdReason: RcmdReason?,
    @SerialName("show_info") val showInfo: Int,
    @SerialName("track_id") val trackId: String,
    val pos: Int,
    @SerialName("room_info") val roomInfo: String? = null,
    @SerialName("ogv_info") val ogvInfo: String? = null,
    @SerialName("business_info") val businessInfo: String? = null,
    @SerialName("is_stock") val isStock: Int,
    @SerialName("enable_vt") val enableVt: Int,
    @SerialName("vt_display") val vtDisplay: String,
    @SerialName("dislike_switch") val dislikeSwitch: Int,
    @SerialName("dislike_switch_pc") val dislikeSwitchPc: Int
)

@Serializable
data class OgvInfo(
    val info: String? // 根据实际结构补充
)

@Serializable
data class RcmdReason(
    val content: String? = null,
    @SerialName("corner_mark") val cornerMark: Int = -1,
    @SerialName("reason_type") val reasonType: Int = -1
)
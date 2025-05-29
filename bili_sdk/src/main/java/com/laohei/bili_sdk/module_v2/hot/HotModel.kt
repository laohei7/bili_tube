package com.laohei.bili_sdk.module_v2.hot

import com.laohei.bili_sdk.module_v2.common.Dimension
import com.laohei.bili_sdk.module_v2.common.OwnerModel
import com.laohei.bili_sdk.module_v2.common.RightsModel
import com.laohei.bili_sdk.module_v2.common.StatModel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class HotModel(
    val list: List<HotItem>,
    @SerialName("no_more") val noMore: Boolean = false,
)


@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class HotItem(
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
    val rights: RightsModel,
    @SerialName("redirect_url") val redirectURL: String? = null,
    val owner: OwnerModel,
    val stat: StatModel,
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
    @SerialName("enable_vt") val enableVt: Int,
    @SerialName("ai_rcmd") val aiRcmd: String? = null,
    @SerialName("rcmd_reason") val rcmdReason: RcmdReasonModel
)

@Serializable
data class RcmdReasonModel(
    val content: String? = null,
    @SerialName("corner_mark") val cornerMark: Int = -1,
    @SerialName("reason_type") val reasonType: Int = -1
)
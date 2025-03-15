package com.laohei.bili_sdk.model

import com.laohei.bili_sdk.model.common.LevelInfo
import com.laohei.bili_sdk.model.common.Official
import com.laohei.bili_sdk.model.common.OfficialVerify
import com.laohei.bili_sdk.model.common.Pendant
import com.laohei.bili_sdk.model.common.Vip
import com.laohei.bili_sdk.model.common.VipLabel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class BiliUserProfile(
    @SerialName("isLogin") val isLogin: Boolean,
    @SerialName("email_verified") val emailVerified: Int,
    @SerialName("face") val face: String,
    @SerialName("face_nft") val faceNft: Int,
    @SerialName("face_nft_type") val faceNftType: Int,
    @SerialName("level_info") val levelInfo: LevelInfo,
    @SerialName("mid") val mid: Long,
    @SerialName("mobile_verified") val mobileVerified: Int,
    @SerialName("money") val money: Double,
    @SerialName("moral") val moral: Int,
    @SerialName("official") val official: Official,
    @SerialName("officialVerify") val officialVerify: OfficialVerify,
    @SerialName("pendant") val pendant: Pendant,
    @SerialName("scores") val scores: Int,
    @SerialName("uname") val uname: String,
    @SerialName("vipDueDate") val vipDueDate: Long,
    @SerialName("vipStatus") val vipStatus: Int,
    @SerialName("vipType") val vipType: Int,
    @SerialName("vip_pay_type") val vipPayType: Int,
    @SerialName("vip_theme_type") val vipThemeType: Int,
    @SerialName("vip_label") val vipLabel: VipLabel,
    @SerialName("vip_avatar_subscript") val vipAvatarSubscript: Int,
    @SerialName("vip_nickname_color") val vipNicknameColor: String,
    @SerialName("vip") val vip: Vip?=null,
    @SerialName("wallet") val wallet: Wallet,
    @SerialName("has_shop") val hasShop: Boolean,
    @SerialName("shop_url") val shopUrl: String,
    @SerialName("answer_status") val answerStatus: Int,
    @SerialName("is_senior_member") val isSeniorMember: Int,
    @SerialName("wbi_img") val wbiImg: WbiImage,
    @SerialName("is_jury") val isJury: Boolean,
    @SerialName("name_render") val nameRender: String?
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Wallet(
    @SerialName("mid") val mid: Long,
    @SerialName("bcoin_balance") val bcoinBalance: Double,
    @SerialName("coupon_balance") val couponBalance: Double,
    @SerialName("coupon_due_time") val couponDueTime: Long
)


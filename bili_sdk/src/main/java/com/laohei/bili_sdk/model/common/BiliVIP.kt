package com.laohei.bili_sdk.model.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Vip(
    val type: Int?=null,
    val status: Int?=null,
    @SerialName("due_date") val dueDate: Long?=null,
    @SerialName("vip_pay_type") val vipPayType: Int?=null,
    @SerialName("theme_type") val themeType: Int?=null,
    val label: VipLabel,
    @SerialName("avatar_subscript") val avatarSubscript: Int,
    @SerialName("nickname_color") val nicknameColor: String,
    val role: Int?=null,
    @SerialName("avatar_subscript_url") val avatarSubscriptUrl: String?=null,
    @SerialName("tv_vip_status") val tvVipStatus: Int?=null,
    @SerialName("tv_vip_pay_type") val tvVipPayType: Int?=null,
    @SerialName("tv_due_date") val tvDueDate: Long?=null,
    @SerialName("avatar_icon") val avatarIcon: AvatarIcon?=null,
    @SerialName("vipType") val vipType: Int?=null,
    @SerialName("vipStatus") val vipStatus: Int?=null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class VipLabel(
    val path: String,
    val text: String,
    @SerialName("label_theme") val labelTheme: String,
    @SerialName("text_color") val textColor: String,
    @SerialName("bg_style") val bgStyle: Int,
    @SerialName("bg_color") val bgColor: String,
    @SerialName("border_color") val borderColor: String,
    @SerialName("use_img_label") val useImgLabel: Boolean,
    @SerialName("img_label_uri_hans") val imgLabelUriHans: String,
    @SerialName("img_label_uri_hant") val imgLabelUriHant: String,
    @SerialName("img_label_uri_hans_static") val imgLabelUriHansStatic: String,
    @SerialName("img_label_uri_hant_static") val imgLabelUriHantStatic: String
)
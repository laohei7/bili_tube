package com.laohei.bili_sdk.model.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Label(
    val type: Int
)

@Serializable
data class BiliReplyLabel(
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
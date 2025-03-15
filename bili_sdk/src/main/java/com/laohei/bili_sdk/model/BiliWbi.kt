package com.laohei.bili_sdk.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class BiliWbi(
    @SerialName("isLogin") val isLogin: Boolean,
    @SerialName("wbi_img") val wbiImg: WbiImage
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class WbiImage(
    @SerialName("img_url") val imgUrl: String,
    @SerialName("sub_url") val subUrl: String
)
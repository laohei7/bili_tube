package com.laohei.bili_sdk.module_v2.captcha

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class GeetestModel(
    val gt: String,
    val challenge: String
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class CaptchaModel(
    val type: String,
    val token: String,
    val geetest: GeetestModel
)

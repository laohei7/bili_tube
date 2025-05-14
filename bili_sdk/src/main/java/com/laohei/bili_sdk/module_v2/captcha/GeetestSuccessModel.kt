package com.laohei.bili_sdk.module_v2.captcha

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys


@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class GeetestSuccessModel(
    @SerialName("geetest_challenge") val geetestChallenge: String,
    @SerialName("geetest_seccode") val geetestSeccode: String,
    @SerialName("geetest_validate") val geetestValidate: String
)
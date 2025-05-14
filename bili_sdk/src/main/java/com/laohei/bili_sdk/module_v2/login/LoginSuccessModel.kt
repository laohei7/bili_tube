package com.laohei.bili_sdk.module_v2.login

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class LoginSuccessModel(
    @SerialName("is_new") val isNew: Boolean,
    val status: Int,
    val message: String,
    val url: String,
    @SerialName("refresh_token") val refreshToken: String,
    val timestamp: Long,
    val hint: String
)

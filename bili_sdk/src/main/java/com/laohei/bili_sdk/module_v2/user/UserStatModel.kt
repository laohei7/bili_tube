package com.laohei.bili_sdk.module_v2.user

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class UserStatModel(
    val following: Int,
    val follower: Int,
    @SerialName("dynamic_count") val dynamicCount: Int
)
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

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class SpiModel(
    @SerialName("b_3") val b3: String,
    @SerialName("b_4") val b4: String
)
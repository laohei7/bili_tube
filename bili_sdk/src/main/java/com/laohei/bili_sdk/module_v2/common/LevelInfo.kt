package com.laohei.bili_sdk.module_v2.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class LevelInfo(
    @SerialName("current_level") val currentLevel: Int,
    @SerialName("current_min") val currentMin: Int,
    @SerialName("current_exp") val currentExp: Int,
    @SerialName("next_exp") val nextExp: Int
)
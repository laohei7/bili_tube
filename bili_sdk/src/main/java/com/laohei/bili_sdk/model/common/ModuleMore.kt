package com.laohei.bili_sdk.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class) // 启用实验性API
@Serializable
@JsonIgnoreUnknownKeys // 忽略未知字段
data class ModuleMore(
    @SerialName("three_point_items") val threePointItems: List<ThreePointItem>
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class ThreePointItem(
    @SerialName("label") val label: String,
    @SerialName("type") val type: String
)

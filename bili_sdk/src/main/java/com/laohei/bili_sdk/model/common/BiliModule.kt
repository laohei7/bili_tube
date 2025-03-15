package com.laohei.bili_sdk.model.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)  // 启用实验性API
@Serializable
@JsonIgnoreUnknownKeys  // 忽略JSON中未知字段
data class BiliModule(
    @SerialName("module_author") val moduleAuthor: ModuleAuthor,
    @SerialName("module_dynamic") val moduleDynamic: ModuleDynamic,
    @SerialName("module_more") val moduleMore: ModuleMore,
    @SerialName("module_stat") val moduleStat: ModuleStat
)
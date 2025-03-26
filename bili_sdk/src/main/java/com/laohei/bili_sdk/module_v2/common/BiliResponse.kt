package com.laohei.bili_sdk.module_v2.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class BiliResponse<T>(
    val code: Int,
    val message: String,
    val ttl: Int = 0,
    val data: T
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class BiliResponseNoData(
    val code: Int,
    val message: String,
    val ttl: Int = 0,
)
package com.laohei.bili_sdk.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class BiliResponse<T>(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: T
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class BiliTimelineResponse<T>(
    val code: Int,
    val message: String,
    val result: T
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class BiliResponseNoData(
    val code: Int,
    val message: String,
    val ttl: Int,
)
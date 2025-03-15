package com.laohei.bili_sdk.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)  // 启用实验性API
@Serializable
@JsonIgnoreUnknownKeys  // 忽略JSON中未知字段
data class ModuleStat(
    @SerialName("comment") val comment: Comment,
    @SerialName("forward") val forward: Forward,
    @SerialName("like") val like: Like
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Comment(
    @SerialName("count") val count: Int,
    @SerialName("forbidden") val forbidden: Boolean
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Forward(
    @SerialName("count") val count: Int,
    @SerialName("forbidden") val forbidden: Boolean
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Like(
    @SerialName("count") val count: Int,
    @SerialName("forbidden") val forbidden: Boolean,
    @SerialName("status") val status: Boolean
)

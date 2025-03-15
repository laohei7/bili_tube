package com.laohei.bili_sdk.model.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Official(
    val role: Int,
    val title: String,
    val desc: String,
    val type: Int
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class OfficialVerify(
    val type: Int,
    val desc: String
)
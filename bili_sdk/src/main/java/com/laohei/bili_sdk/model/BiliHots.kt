package com.laohei.bili_sdk.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class BiliHots(
    val list: List<BiliHotVideoItem>,
    @SerialName("no_more") val noMore: Boolean = false,
)
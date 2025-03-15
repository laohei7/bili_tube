package com.laohei.bili_sdk.model.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Nameplate(
    val nid: Int,
    val name: String,
    val image: String,
    @SerialName("image_small") val imageSmall: String,
    val level: String,
    val condition: String
)

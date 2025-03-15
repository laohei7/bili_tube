package com.laohei.bili_sdk.model.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys


@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Pendant(
    val pid: Int,
    val name: String,
    val image: String?=null,
    val expire: Long,
    @SerialName("image_enhance") val imageEnhance: String,
    @SerialName("image_enhance_frame") val imageEnhanceFrame: String,
    @SerialName("n_pid") val nPid: Long
)

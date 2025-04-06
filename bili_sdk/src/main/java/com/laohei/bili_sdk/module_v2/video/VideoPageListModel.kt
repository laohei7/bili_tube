package com.laohei.bili_sdk.module_v2.video

import com.laohei.bili_sdk.module_v2.common.Dimension
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class VideoPageListModel(
    val cid: Long,
    val page: Int,
    val from: String,
    val part: String,
    val duration: Long,
    val vid: String,
    val weblink: String,
    val dimension: Dimension,
    @SerialName(value = "first_frame") val firstFrame: String,
    val ctime: Long
)
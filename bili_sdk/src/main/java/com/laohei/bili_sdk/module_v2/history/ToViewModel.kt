package com.laohei.bili_sdk.module_v2.history

import com.laohei.bili_sdk.module_v2.video.VideoView
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ToViewModel(
    val count: Int,
    val list: List<VideoView>
) {
    companion object {
        val EMPTY = ToViewModel(0, emptyList())
    }
}
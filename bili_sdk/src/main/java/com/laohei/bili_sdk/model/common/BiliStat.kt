package com.laohei.bili_sdk.model.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys


@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Stat(
    val aid: Long = -0,
    val view: Int = 0,
    val danmaku: Int = 0,
    val reply: Int = 0,
    val favorite: Int = 0,
    val coin: Int = 0,
    val share: Int = 0,
    @SerialName("now_rank") val nowRank: Int = 0,
    @SerialName("his_rank") val hisRank: Int = 0,
    val like: Int = 0,
    val dislike: Int = 0,
    val evaluation: String = "",
    val vt: Int = 0,
    val vv: Int = 0
)
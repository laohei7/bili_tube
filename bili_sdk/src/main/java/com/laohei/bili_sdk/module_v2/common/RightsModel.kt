package com.laohei.bili_sdk.module_v2.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys


@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class RightsModel(
    val bp: Int = 0,
    val elec: Int = 0,
    val download: Int = 0,
    val movie: Int = 0,
    val pay: Int = 0,
    val hd5: Int = 0,
    @SerialName("no_reprint") val noReprint: Int = 0,
    val autoplay: Int = 0,
    @SerialName("ugc_pay") val ugcPay: Int = 0,
    @SerialName("is_cooperation") val isCooperation: Int = 0,
    @SerialName("ugc_pay_preview") val ugcPayPreview: Int = 0,
    @SerialName("no_background") val noBackground: Int = 0,
    @SerialName("arc_pay") val arcPay: Int = 0,
    @SerialName("pay_free_watch") val payFreeWatch: Int = 0,
    @SerialName("clean_mode") val cleanMode: Int = 0,
    @SerialName("is_stein_gate") val isSteinGate: Int = 0,
    @SerialName("is_360") val is360: Int = 0,
    @SerialName("no_share") val noShare: Int = 0,
    @SerialName("free_watch") val freeWatch: Int = 0
)
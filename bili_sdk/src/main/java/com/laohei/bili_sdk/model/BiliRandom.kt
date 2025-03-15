package com.laohei.bili_sdk.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliRandom(
    val item: List<BiliRandomVideoItem>,
    @SerialName("business_card") val businessCard: String? = null,
    @SerialName("floor_info") val floorInfo: String? = null,
    @SerialName("user_feature") val userFeature: String? = null,
    @SerialName("preload_expose_pct") val preloadExposePct: Float,
    @SerialName("preload_floor_expose_pct") val preloadFloorExposePct: Float,
    val mid: Long
)
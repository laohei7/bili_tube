package com.laohei.bili_sdk.model

import com.laohei.bili_sdk.model.common.LevelInfo
import com.laohei.bili_sdk.model.common.Nameplate
import com.laohei.bili_sdk.model.common.Official
import com.laohei.bili_sdk.model.common.OfficialVerify
import com.laohei.bili_sdk.model.common.Pendant
import com.laohei.bili_sdk.model.common.Vip
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class BiliUserCard(
    val card: Card,
    val following: Boolean,
    val archiveCount: Int = 0,
    val articleCount: Int = 0,
    val follower: Int = 0,
    val likeNum: Int = 0
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Card(
    val mid: String,
    val name: String,
    val approve: Boolean,
    val sex: String,
    val rank: String,
    val face: String,
    @SerialName("face_nft") val faceNft: Int,
    @SerialName("face_nft_type") val faceNftType: Int,
    @SerialName("DisplayRank") val displayRank: String,
    val regtime: Long,
    val spacesta: Int,
    val birthday: String,
    val place: String,
    val description: String,
    val article: Int,
    val attentions: List<String> = emptyList(),
    val fans: Int,
    val friend: Int,
    val attention: Int,
    val sign: String,
    @SerialName("level_info") val levelInfo: LevelInfo,
    val pendant: Pendant,
    val nameplate: Nameplate,
    val official: Official? = null,
    @SerialName("official_verify") val officialVerify: OfficialVerify,
    val vip: Vip,
    @SerialName("is_senior_member") val isSeniorMember: Int,
    @SerialName("name_render") val nameRender: String? = null
)






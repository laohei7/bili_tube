package com.laohei.bili_sdk.module_v2.user

import com.laohei.bili_sdk.module_v2.common.LevelInfo
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class InfoCardModel(
    val card: InfoCard,
    val following: Boolean,
    @SerialName("archive_count") val archiveCount: Int,
    val follower: Long,
    @SerialName("like_num") val likeNum: Long
) {
    companion object {
        val ERROR = InfoCardModel(
            card = InfoCard(
                "", "", "", "", "", "", 0, 0, 0, "",
                OfficialModel(1, "", "", 0),
                OfficialModel(1, "", "", 0),
                LevelInfo(0, 0, 0, 0)
            ),
            following = false,
            archiveCount = 0,
            follower = 0,
            likeNum = 0
        )
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class InfoCard(
    val mid: String,
    val name: String,
    val sex: String,
    val rank: String,
    val face: String,
    val birthday: String,
    val fans: Long,
    val friend: Long,
    val attention: Long,
    val sign: String,
    @SerialName("Official") val official: OfficialModel,
    @SerialName("official_verify") val officialVerify: OfficialModel,
    @SerialName("level_info") val levelInfo: LevelInfo
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class OfficialModel(
    val role: Int = 1,
    val title: String = "",
    val desc: String,
    val type: Int
)
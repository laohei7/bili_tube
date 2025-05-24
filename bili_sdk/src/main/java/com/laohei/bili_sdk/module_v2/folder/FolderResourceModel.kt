package com.laohei.bili_sdk.module_v2.folder

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class FolderResourceModel(
    val info: FolderInfoModel,
    val medias: List<FolderMediaItem>,
    @SerialName("has_more") val hasMore: Boolean
) {
    companion object {
        val ERROR = FolderResourceModel(
            info = FolderInfoModel(0, 0, 0, "", "", UpperModel(0, "", ""), 0, 0, 0),
            medias = emptyList(),
            hasMore = false
        )
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class FolderInfoModel(
    val id: Long,
    val fid: Long,
    val mid: Long,
    val title: String,
    val cover: String,
    val upper: UpperModel,
    @SerialName("media_count") val mediaCount: Int,
    val ctime: Long,
    val mtime: Long
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class FolderMediaItem(
    val id: Long,
    val type: Int,
    val title: String,
    val cover: String,
    val intro: String,
    val page: Int,
    val duration: Long,
    val upper: UpperModel,
    @SerialName("cnt_info") val cntInfo: CNTInfoModel,
    val link: String,
    val ctime: Long,
    val pubtime: Long,
    @SerialName("fav_time") val favTime: Long,
    val bvid: String,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class UpperModel(
    val mid: Long,
    val name: String,
    val face: String,
    val followed: Boolean = false,
    @SerialName("vip_type") val vipType: Int = Int.MIN_VALUE,
    @SerialName("vip_statue") val vipStatue: Int = Int.MIN_VALUE
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class CNTInfoModel(
    val collect: Long,
    val play: Long,
    @SerialName("thumb_up") val thumbUp: Long = 0,
    val share: Long = 0
)
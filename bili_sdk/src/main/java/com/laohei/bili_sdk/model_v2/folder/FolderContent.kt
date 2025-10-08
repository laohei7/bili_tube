package com.laohei.bili_sdk.model_v2.folder

import com.feature.annotations.FeatureTag
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class FolderContent(
    val info: FolderInfo,
    val medias: List<MediaItem>,
    @SerialName("has_more") val hasMore: Boolean
) {
    companion object {
        val ERROR by lazy {
            FolderContent(
                info = FolderInfo.ERROR,
                medias = emptyList(),
                hasMore = false
            )
        }
    }
}

@FeatureTag(
    module = "bili_sdk",
    layer = "data",
    feature = "Folder Info",
    desc = """
        Represents basic folder information returned by the Bili SDK.
        Used in folder resource list and folder editor.
    """
)
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class FolderInfo(
    val id: Long,
    val fid: Long,
    val mid: Long,
    val attr: Int,
    val title: String,
    val cover: String,
    val upper: UpperInfo,
    val intro: String = "",
    @SerialName("media_count") val mediaCount: Int = 0,
    @SerialName("ctime") val cTime: Long,
    @SerialName("mtime") val mTime: Long
) {
    companion object {
        val ERROR by lazy {
            FolderInfo(0, 0, 0, 0, "", "", UpperInfo(0, "", ""), "", 0, 0, 0)
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class MediaItem(
    val id: Long,
    val type: Int,
    val title: String,
    val attr: Int,
    val cover: String,
    val intro: String,
    val page: Int,
    val duration: Long,
    val upper: UpperInfo,
    @SerialName("cnt_info") val cntInfo: CNTInfo,
    val link: String,
    @SerialName("ctime") val cTime: Long,
    @SerialName("pubtime") val pubTime: Long,
    @SerialName("fav_time") val favTime: Long,
    val bvid: String,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class UpperInfo(
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
data class CNTInfo(
    val collect: Long,
    val play: Long,
    @SerialName("thumb_up") val thumbUp: Long = 0,
    val share: Long = 0
)
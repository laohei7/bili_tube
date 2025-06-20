package com.laohei.bili_sdk.module_v2.folder

import com.laohei.bili_sdk.module_v2.video.VideoOwner
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class FolderModel(
    val id: Int,
    val name: String,
    val mediaListResponse: MediaListModel,
    val uri: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class SimpleFolderModel(
    val count: Int,
    val list: List<SimpleFolderItem>
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class MediaListModel(
    val count: Int,
    val list: List<FolderItem>? = null,
    @SerialName("has_more") val hasMore: Boolean
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class FolderItem(
    val id: Long,
    val fid: Long,
    val mid: Long,
    val title: String,
    val cover: String,
    val upper: VideoOwner,
    val attr: Int = 7,
    val ctime: Long,
    val mtime: Long,
    @SerialName("media_count") val mediaCount: Int
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class SimpleFolderItem(
    val id: Long,
    val fid: Long,
    val mid: Long,
    val attr: Int,
    val title: String,
    @SerialName("fav_state") val favState: Int,
    @SerialName("media_count") val mediaCount: Int
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class FolderDealModel(
    val prompt: Boolean
)
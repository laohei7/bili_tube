package com.laohei.bili_sdk.module_v2.reply

import com.laohei.bili_sdk.module_v2.common.LevelInfo
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ReplyModel(
    val page: ReplyPage,
    val config: ReplyConfig,
    val replies: List<ReplyItem>
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ReplyPage(
    val num: Int,
    val size: Int,
    val count: Int
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ReplyConfig(
    @SerialName("showtopic") val showTopic: Int,
    @SerialName("show_up_flag") val showUpFlag: Boolean,
    @SerialName("read_only") val readOnly: Boolean
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ReplyItem(
    val rpid: Long,
    val oid: Long,
    val type: Int,
    val mid: Long,
    val root: Long,
    val parent: Long,
    val ctime: Long,
    val like: Int,
    val member: ReplyMember,
    val content: ReplyContent,
    val replies: List<ReplyItem>? = null
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ReplyMember(
    val mid: String,
    val uname: String,
    val avatar: String,
    val rank: String,
    @SerialName("level_info") val levelInfo: LevelInfo
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ReplyContent(
    val message: String,
    val emote: Map<String, EmoteModel>? = null
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class EmoteModel(
    val id: Long,
    @SerialName("package_id") val packageId: Int,
    val text: String,
    val url: String
)
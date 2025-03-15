package com.laohei.bili_sdk.model.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class ModuleDynamic(
    @SerialName("additional") val additional: Additional? = null,
    @SerialName("desc") val desc: Desc? = null,
    @SerialName("major") val major: Major,
    @SerialName("topic") val topic: Topic? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Additional(
    val reserve: Reserve? = null,
    val type: String? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Reserve(
    val button: Button? = null,
    val desc1: Desc2? = null,
    val desc2: Desc2? = null,
    @SerialName("jump_url") val jumpUrl: String? = null,
    @SerialName("reserve_total") val reserveTotal: Int? = null,
    val rid: Int? = null,
    val state: Int? = null,
    val stype: Int? = null,
    val title: String? = null,
    @SerialName("up_mid") val upMid: Int? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Button(
    val check: ButtonState? = null,
    val status: Int? = null,
    val type: Int? = null,
    val uncheck: ButtonState? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class ButtonState(
    @SerialName("icon_url") val iconUrl: String? = null,
    val text: String? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Desc2(
    val style: Int? = null,
    val text: String = "",
    val visible: Boolean = true // 默认 `true`，避免 `desc1` 中缺失 `visible` 时报错
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Topic(
    val id: Int,
    @SerialName("jump_url") val jumpUrl: String,
    val name: String
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Desc(
    @SerialName("rich_text_nodes") val richTextNodes: List<RichTextNode>? = null,
    @SerialName("text") val text: String? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class RichTextNode(
    @SerialName("orig_text") val origText: String,
    @SerialName("text") val text: String,
    @SerialName("type") val type: String
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Major(
    @SerialName("draw") val draw: Draw? = null,
    @SerialName("type") val type: String? = null,
    val archive: Archive
)

@Serializable
data class Archive(
    val aid: String,
    val badge: Badge? = null,
    val bvid: String,
    val cover: String,
    val desc: String,
    @SerialName("disable_preview") val disablePreview: Int,
    @SerialName("duration_text") val durationText: String,
    @SerialName("jump_url") val jumpUrl: String,
    val stat: Stat,
    val title: String,
    val type: Int
)

@Serializable
data class Badge(
    @SerialName("bg_color") val bgColor: String? = null,
    val color: String? = null,
    @SerialName("icon_url") val iconUrl: String? = null,
    val text: String? = null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Draw(
    @SerialName("id") val id: Long,
    @SerialName("items") val items: List<DrawItem>
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class DrawItem(
    @SerialName("height") val height: Double,
    @SerialName("size") val size: Double,
    @SerialName("src") val src: String,
    @SerialName("tags") val tags: List<String>,
    @SerialName("width") val width: Int
)

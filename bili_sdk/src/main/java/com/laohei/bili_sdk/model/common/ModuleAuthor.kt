package com.laohei.bili_sdk.model.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class ModuleAuthor(
    @SerialName("avatar") val avatar: Avatar?=null,
    @SerialName("decorate") val decorate: Decorate?=null,
    @SerialName("face") val face: String,
    @SerialName("face_nft") val faceNft: Boolean,
    @SerialName("following") val following: Boolean?=null,
    @SerialName("jump_url") val jumpUrl: String,
    @SerialName("label") val label: String,
    @SerialName("mid") val mid: Long,
    @SerialName("name") val name: String,
    @SerialName("official_verify") val officialVerify: OfficialVerify?=null,
    @SerialName("pendant") val pendant: Pendant?=null,
    @SerialName("pub_action") val pubAction: String,
    @SerialName("pub_location_text") val pubLocationText: String?=null,
    @SerialName("pub_time") val pubTime: String,
    @SerialName("pub_ts") val pubTs: Long,
    @SerialName("type") val type: String,
    @SerialName("vip") val vip: Vip?=null
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Avatar(
    @SerialName("container_size") val containerSize: ContainerSize,
    @SerialName("fallback_layers") val fallbackLayers: FallbackLayers,
    @SerialName("mid") val mid: String
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class ContainerSize(
    @SerialName("height") val height: Double,
    @SerialName("width") val width: Double
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class FallbackLayers(
    @SerialName("is_critical_group") val isCriticalGroup: Boolean,
    @SerialName("layers") val layers: List<Layer>
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Layer(
    @SerialName("general_spec") val generalSpec: GeneralSpec,
    @SerialName("layer_config") val layerConfig: LayerConfig,
    @SerialName("resource") val resource: Resource,
    @SerialName("visible") val visible: Boolean
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class GeneralSpec(
    @SerialName("pos_spec") val posSpec: PosSpec,
    @SerialName("render_spec") val renderSpec: RenderSpec,
    @SerialName("size_spec") val sizeSpec: SizeSpec
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class PosSpec(
    @SerialName("axis_x") val axisX: Double,
    @SerialName("axis_y") val axisY: Double,
    @SerialName("coordinate_pos") val coordinatePos: Int
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class RenderSpec(
    @SerialName("opacity") val opacity: Double
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class SizeSpec(
    @SerialName("height") val height: Double,
    @SerialName("width") val width: Double
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class LayerConfig(
    @SerialName("is_critical") val isCritical: Boolean=false,
    @SerialName("tags") val tags: Tags
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Tags(
    @SerialName("AVATAR_LAYER") val avatarLayer: AvatarLayer?=null,
    @SerialName("GENERAL_CFG") val generalCfg: GeneralCfg?=null
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class AvatarLayer(
    val id: String? = null
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class GeneralCfg(
    @SerialName("config_type") val configType: Int,
    @SerialName("general_config") val generalConfig: GeneralConfigDetails
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class GeneralConfigDetails(
    @SerialName("web_css_style") val webCssStyle: WebCssStyle
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class WebCssStyle(
    @SerialName("borderRadius") val borderRadius: String
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Resource(
    @SerialName("res_image") val resImage: ResImage,
    @SerialName("res_type") val resType: Int
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class ResImage(
    @SerialName("image_src") val imageSrc: ImageSrc?=null
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class ImageSrc(
    @SerialName("placeholder") val placeholder: Int?=null,
    @SerialName("remote") val remote: Remote?=null,
    @SerialName("src_type") val srcType: Int?=null
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Remote(
    @SerialName("bfs_style") val bfsStyle: String,
    @SerialName("url") val url: String
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Decorate(
    @SerialName("card_url") val cardUrl: String,
    @SerialName("fan") val fan: Fan,
    @SerialName("id") val id: Long,
    @SerialName("jump_url") val jumpUrl: String,
    @SerialName("name") val name: String,
    @SerialName("type") val type: Int
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Fan(
    @SerialName("color") val color: String,
    @SerialName("color_format") val colorFormat: ColorFormat,
    @SerialName("is_fan") val isFan: Boolean,
    @SerialName("num_prefix") val numPrefix: String,
    @SerialName("num_str") val numStr: String,
    @SerialName("number") val number: Int
)
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class ColorFormat(
    @SerialName("colors") val colors: List<String>,
    @SerialName("end_point") val endPoint: String,
    @SerialName("gradients") val gradients: List<Int>,
    @SerialName("start_point") val startPoint: String
)







package com.laohei.bili_tube.core

import android.Manifest
import android.os.Build
import androidx.datastore.preferences.core.stringPreferencesKey

val COOKIE_KEY = stringPreferencesKey("cookie")
val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
val IMG_URL_KEY = stringPreferencesKey("img_url")
val SUB_URL_KEY = stringPreferencesKey("sub_url")
val FACE_URL_KEY = stringPreferencesKey("face_url")
val USERNAME_KEY = stringPreferencesKey("username")
val UP_MID_KEY = stringPreferencesKey("up_mid")
val VIP_STATUS_KEY = stringPreferencesKey("vip_status")
val LAST_SHOW_LIST_KEY = stringPreferencesKey("last_show_list")
const val AUTO_SKIP_KEY = "auto_skip"
const val MERGE_SOURCE_KEY = "merge_source"
internal const val MOBILE_NET_VIDEO_QUALITY = "mobile_net_video_quality"
internal const val WLAN_VIDEO_QUALITY = "wlan_video_quality"
internal const val MOBILE_NET_AUDIO_QUALITY = "mobile_net_audio_quality"
internal const val WLAN_AUDIO_QUALITY = "wlan_audio_quality"

const val SHARED_FILE = "bili_tube_shared"

internal val READ_STORAGE_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    listOf(
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_AUDIO
    )
} else {
    listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
}

internal val WRITE_STORAGE_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    listOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
} else {
    listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
}

internal val NOTIFICATION_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    listOf(Manifest.permission.POST_NOTIFICATIONS)
} else {
    emptyList()
}

internal val VideoSuperQualities = listOf(
    Pair(127, "8K 超高清"),
    Pair(126, "杜比视界"),
    Pair(125, "HDR 真彩色"),
    Pair(120, "4K 超清"),
    Pair(116, "1080P60 高帧率"),
    Pair(112, "1080P+ 高码率"),
)

internal val VideoQualities = listOf(
    Pair(Int.MAX_VALUE, "超高画质"),
    Pair(80, "1080P 高清"),
    Pair(74, "720P60 高帧率"),
    Pair(64, "720P 高清"),
    Pair(32, "480P 清晰"),
    Pair(16, "360P 流畅"),
)

internal val AudioQualities = listOf(
    Pair(30251, "Hi-Res无损"),
    Pair(30250, "杜比全景声"),
    Pair(30280, "高质量"),
    Pair(30232, "中质量"),
    Pair(30216, "低质量"),
)
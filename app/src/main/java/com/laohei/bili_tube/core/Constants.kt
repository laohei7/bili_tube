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
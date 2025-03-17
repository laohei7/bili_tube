package com.laohei.bili_tube.core

import androidx.datastore.preferences.core.stringPreferencesKey

val COOKIE_KEY = stringPreferencesKey("cookie")
val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
val IMG_URL_KEY = stringPreferencesKey("img_url")
val SUB_URL_KEY = stringPreferencesKey("sub_url")
val FACE_URL_KEY = stringPreferencesKey("face_url")
val USERNAME_KEY = stringPreferencesKey("username")
val VIP_STATUS_KEY = stringPreferencesKey("vip_status")

const val SHARED_FILE = "bili_tube_shared"
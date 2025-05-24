package com.laohei.bili_tube.core.util

import android.content.Context

class PreferencesUtil(
    val context: Context
) {
    fun setValue(key: String, value: Any) = context.setValue(key, value)
    inline fun <reified T : Any> getValue(key: String, defaultValue: T): T =
        context.getValue(key, defaultValue)
}
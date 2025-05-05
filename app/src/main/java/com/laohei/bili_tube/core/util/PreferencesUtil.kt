package com.laohei.bili_tube.core.util

import android.content.Context

class PreferencesUtil(
    private val context: Context
) {
    fun setValue(key: String, value: Any) = context.setValue(key, value)
    fun <T> getValue(key: String, defaultValue: T): T = context.getValue(key, defaultValue)
}
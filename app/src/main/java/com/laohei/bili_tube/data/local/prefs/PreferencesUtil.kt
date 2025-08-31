package com.laohei.bili_tube.data.local.prefs

import android.content.Context
import com.laohei.bili_tube.core.extension.getValue
import com.laohei.bili_tube.core.extension.setValue

class PreferencesUtil(
    val context: Context
) {
    fun setValue(key: String, value: Any) = context.setValue(key, value)
    inline fun <reified T : Any> getValue(key: String, defaultValue: T): T =
        context.getValue(key, defaultValue)
}
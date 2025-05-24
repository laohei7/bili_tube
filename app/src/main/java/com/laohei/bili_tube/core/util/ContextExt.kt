package com.laohei.bili_tube.core.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.app.ActivityCompat
import com.laohei.bili_tube.core.SHARED_FILE

fun Context.setValue(key: String, value: Any) {
    getSharedPreferences(SHARED_FILE, Activity.MODE_PRIVATE).edit().apply {
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Float -> putFloat(key, value)
            is Long -> putLong(key, value)
            is Boolean -> putBoolean(key, value)
        }
        commit()
    }
}

inline fun <reified T: Any> Context.getValue(key: String, defaultValue: T): T {
    val prefs = getSharedPreferences(SHARED_FILE, Activity.MODE_PRIVATE)
    return when (defaultValue) {
        is String -> prefs.getString(key, defaultValue) as T
        is Int -> prefs.getInt(key, defaultValue) as T
        is Float -> prefs.getFloat(key, defaultValue) as T
        is Long -> prefs.getLong(key, defaultValue) as T
        is Boolean -> prefs.getBoolean(key, defaultValue) as T
        else -> defaultValue
    }
}

fun Context.checkedPermission(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(
        this, permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.checkedPermissions(permissions: List<String>): Boolean {
    return permissions.all {
        if (it == Manifest.permission.MANAGE_EXTERNAL_STORAGE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            checkedPermission(it)
        }
    }
}
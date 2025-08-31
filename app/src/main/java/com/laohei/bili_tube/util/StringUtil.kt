package com.laohei.bili_tube.util

fun String.toAbsoluteUrl(): String {
    return when {
        this.startsWith("//") -> "https:$this"
        this.startsWith("http://") || this.startsWith("https://") -> this
        else -> this
    }
}

fun String?.extractBiliJct(): String {
    return this?.let { Regex("bili_jct=([^;]+)").find(it)?.groupValues?.get(1) } ?: ""
}

fun String.isValidChinesePhoneNumber(): Boolean {
    return length == 11 && all { it.isDigit() }
}
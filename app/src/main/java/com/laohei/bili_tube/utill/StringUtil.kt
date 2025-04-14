package com.laohei.bili_tube.utill

fun String.completeUrl(): String {
    return when {
        this.startsWith("//") -> "https:$this"
        this.startsWith("http://") || this.startsWith("https://") -> this
        else -> this
    }
}
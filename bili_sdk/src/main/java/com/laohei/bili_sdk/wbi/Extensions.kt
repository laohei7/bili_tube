package com.laohei.bili_sdk.wbi

import io.ktor.http.encodeURLQueryComponent
import java.security.MessageDigest

private val hexDigits = "0123456789abcdef".toCharArray()

fun ByteArray.toHexString() = buildString(this.size shl 1) {
    this@toHexString.forEach { byte ->
        append(hexDigits[byte.toInt() ushr 4 and 15])
        append(hexDigits[byte.toInt() and 15])
    }
}

fun String.toMD5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(this.toByteArray())
    return digest.toHexString()
}

fun Map<String, Any?>.toQueryString() =
    this.filterValues { it != null }.entries.joinToString("&") { (k, v) ->
        "${k.encodeURLQueryComponent()}=${v!!.toString().encodeURLQueryComponent()}"
    }
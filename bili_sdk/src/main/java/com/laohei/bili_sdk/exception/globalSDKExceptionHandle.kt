package com.laohei.bili_sdk.exception

import android.util.Log
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException

internal fun globalSDKExceptionHandle(
    tag: String,
    exception: Throwable
) {
    when (exception) {
        is IllegalArgumentException -> Log.e(tag, "JSON解析失败", exception)
        is ClientRequestException -> Log.e(tag, "请求失败", exception)
        is HttpRequestTimeoutException -> Log.e(tag, "请求超时", exception)
        else -> Log.e(tag, "未知错误", exception)
    }
}
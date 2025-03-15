package com.laohei.bili_sdk.wbi

import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

private fun JsonElement?.get(): String {
    check(this != null) { "No contents found" }
    return this.jsonPrimitive.content.split('/').last().removeSuffix(".png")
}

private val mixinKeyEncTab = intArrayOf(
    46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
    33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
    61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
    36, 20, 34, 44, 52
)

@Serializable
data class WbiParams(
    val imgKey: String,
    val subKey: String,
) {
    companion object {
        private val TAG = WbiParams::class.simpleName
        var wbi: WbiParams? = null
            private set

        private val mutex = Mutex()

        suspend fun initWbi(imgKey: String, subKey: String) {
            mutex.withLock {
                if (wbi == null) {
                    wbi = WbiParams(imgKey, subKey)
                    Log.d(TAG, "initWbi: $wbi")
                }
            }
        }
    }

    // 此处整合了切分参数(直接传入{img_url:string, sub_url:string}即可), 不需要可以删掉
    constructor(wbiImg: JsonObject) : this(wbiImg["img_url"].get(), wbiImg["sub_url"].get())

    private val mixinKey: String
        get() = (imgKey + subKey).let { s ->
            buildString {
                repeat(32) {
                    append(s[mixinKeyEncTab[it]])
                }
            }
        }

    // 创建对象(GET获取或者读缓存, 比如Redis)之后, 直接调用此函数处理
    fun enc(params: Map<String, Any?>): String {
        val sorted = params.filterValues { it != null }.toSortedMap()
        return buildString {
            append(sorted.toQueryString())
            val wts = System.currentTimeMillis() / 1000
            sorted["wts"] = wts
            append("&wts=")
            append(wts)
            append("&w_rid=")
            append((sorted.toQueryString() + mixinKey).toMD5())
        }
    }
}
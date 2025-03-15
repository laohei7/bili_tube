package com.laohei.bili_sdk.dynamic

import android.util.Log
import com.laohei.bili_sdk.apis.WEB_DYNAMIC_URL
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.dynamic.DynamicModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class WebDynamic(
    private val client: HttpClient
) {
    companion object {
        private val TAG = WebDynamic::class.simpleName
    }

    suspend fun dynamicList(
        cookie: String? = null,
        type: String = "all",
        page: Int = 1,
        offset: Long? = null
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(WEB_DYNAMIC_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                    parameters.append("type", type)
                    parameters.append("page", page.toString())
                    offset?.let {
                        parameters.append("offset", it.toString())
                    }
                }
                Log.d(TAG, "getVideoReplies: $url")
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<DynamicModel>>(this.bodyAsText())
        }
    }
}
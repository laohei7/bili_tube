package com.laohei.bili_sdk.history

import com.laohei.bili_sdk.apis.WATCH_LATER_URL
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.history.WatchLaterModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class GetWatchLater(
    private val client: HttpClient
) {
    suspend fun watchLaterList(
        cookie: String? = null,
        pn: Int = 1,
        ps: Int = 20
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(WATCH_LATER_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                    parameters.append("pn", pn.toString())
                    parameters.append("ps", ps.toString())
                }
            }
        } catch (_: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<WatchLaterModel>>(this.bodyAsText())
        }
    }
}
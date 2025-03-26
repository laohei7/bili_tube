package com.laohei.bili_sdk.history

import com.laohei.bili_sdk.apis.HISTORY_URL
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.history.HistoryModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class GetHistory(
    private val client: HttpClient
) {
    suspend fun historyList(
        cookie: String? = null,
        ps: Int = 20,
        max: Long? = null,
        business: String? = null,
        viewAt: Long? = null
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(HISTORY_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                    max?.apply {
                        parameters.append("max", this.toString())
                    }
                    business?.apply {
                        parameters.append("business", this)
                    }
                    viewAt?.apply {
                        parameters.append("view_at", this.toString())
                    }
                    parameters.append("ps", ps.toString())
                }
            }
        } catch (_: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<HistoryModel>>(this.bodyAsText())
        }
    }
}
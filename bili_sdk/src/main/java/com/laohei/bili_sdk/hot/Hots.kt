package com.laohei.bili_sdk.hot

import com.laohei.bili_sdk.apis.HOT_VIDEOS
import com.laohei.bili_sdk.model.BiliHots
import com.laohei.bili_sdk.model.BiliResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class Hots(private val client: HttpClient) {

    companion object {
        private val TAG = Hots::class.simpleName
    }

    suspend fun hotVideos(
        cookie: String? = null,
        pn: Int = 1, ps: Int = 20
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(HOT_VIDEOS) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                    parameters.append("pn", pn.toString())
                    parameters.append("ps", ps.toString())
                }
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<BiliHots>>(this.bodyAsText())
        }
    }

}
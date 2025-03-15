package com.laohei.bili_sdk.recommend

import com.laohei.bili_sdk.apis.RECOMMEND_VIDEOS
import com.laohei.bili_sdk.model.BiliRandom
import com.laohei.bili_sdk.model.BiliResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class Recommend(private val client: HttpClient) {


    suspend fun recommendVideos(cookie: String? = null) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(RECOMMEND_VIDEOS) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                }
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<BiliRandom>>(this.bodyAsText())
        }
    }
}
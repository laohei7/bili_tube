package com.laohei.bili_sdk.video

import android.util.Log
import com.laohei.bili_sdk.apis.VIDEO_REPLY_URL
import com.laohei.bili_sdk.model.BiliReply
import com.laohei.bili_sdk.model.BiliResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class VideoReply(
    private val client: HttpClient
) {
    companion object {
        private val TAG = VideoReply::class.simpleName
    }

    suspend fun getVideoReplies(
        cookie: String? = null, type: Int = 1, oid: String, ps: Int = 20, pn: Int = 1
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(VIDEO_REPLY_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                    parameters.append("type", type.toString())
                    parameters.append("oid", oid)
                    parameters.append("pn", pn.toString())
                    parameters.append("ps", ps.toString())
                }
                Log.d(TAG, "getVideoReplies: $url")
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<BiliReply>>(bodyAsText())
        }
    }
}
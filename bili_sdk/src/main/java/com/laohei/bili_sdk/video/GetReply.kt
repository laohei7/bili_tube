package com.laohei.bili_sdk.video

import android.util.Log
import com.laohei.bili_sdk.apis.VIDEO_REPLY_URL
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.reply.ReplyModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class GetReply(
    private val client: HttpClient
) {
    companion object {
        private val TAG = GetReply::class.simpleName
        private const val DBG = true
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
            try {
                Json.decodeFromString<BiliResponse<ReplyModel>>(bodyAsText())
            } catch (e: Exception) {
                if (DBG) {
                    Log.d(TAG, "getVideoReplies: ${e.message}")
                }
                null
            }

        }
    }
}
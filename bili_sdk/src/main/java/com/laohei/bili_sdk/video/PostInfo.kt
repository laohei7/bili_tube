package com.laohei.bili_sdk.video

import android.util.Log
import com.laohei.bili_sdk.apis.VIDEO_COIN_ADD_URL
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.video.AddCoinModel
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class PostInfo(
    private val client: HttpClient
) {
    companion object {
        private val TAG = PostInfo::class.simpleName
    }

    suspend fun videoCoin(
        aid: Long,
        bvid: String,
        multiply: Int,
        cookie: String? = null,
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.post(VIDEO_COIN_ADD_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, cookie)
                    }
                }
                Log.d(TAG, "videoInfo: $url")
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("aid", aid.toString())
                            append("bvid", bvid)
                            append("multiply", multiply.toString())
                            append(
                                "csrf",
                                cookie?.substringAfter("bili_jct=")?.substringBefore(";") ?: ""
                            )
                        }
                    ))
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<AddCoinModel>>(bodyAsText())
        }
    }
}
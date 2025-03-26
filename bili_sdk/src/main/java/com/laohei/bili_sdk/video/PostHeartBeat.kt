package com.laohei.bili_sdk.video

import android.util.Log
import com.laohei.bili_sdk.apis.VIDEO_HEART_BEAT_URL
import com.laohei.bili_sdk.apis.VIDEO_HISTORY_REPORT
import com.laohei.bili_sdk.model.BiliResponseNoData
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

class PostHeartBeat(
    private val client: HttpClient
) {
    companion object {
        private val TAG = PostHeartBeat::class.simpleName
    }

    suspend fun uploadVideoHistory(
        cookie: String? = null,
        aid: String,
        cid: String,
        progress: Long = 0L,
    ) = withContext(Dispatchers.IO) {
        Log.d(TAG, "uploadVideoHistory: upload start $progress")
        val response = try {
            client.post(VIDEO_HISTORY_REPORT) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                }
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(FormDataContent(
                    Parameters.build {
                        append("aid", aid)
                        append("cid", cid)
                        append("progress", progress.toString())
                        append(
                            "csrf",
                            cookie?.substringAfter("bili_jct=")?.substringBefore(";") ?: ""
                        )
                    }
                ))
            }
        } catch (_: Exception) {
            null
        }
        response?.apply {
            Log.d(
                TAG,
                "uploadVideoHistory: ${Json.decodeFromString<BiliResponseNoData>(bodyAsText())}"
            )
        }
    }

    suspend fun uploadVideoHeartBeat(
        cookie: String? = null,
        aid: String,
        bvid: String,
        cid: String,
        playedTime: Long = 0,
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.post(VIDEO_HEART_BEAT_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                }
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(FormDataContent(
                    Parameters.build {
                        append("aid", aid)
                        append("bvid", bvid)
                        append("cid", cid)
                        append("played_time", playedTime.toString())
                    }
                ))
            }
        } catch (_: Exception) {
            null
        }
        response?.apply {
            Log.d(
                TAG,
                "uploadVideoHeartBeat: ${Json.decodeFromString<BiliResponseNoData>(bodyAsText())}"
            )
        }
    }
}
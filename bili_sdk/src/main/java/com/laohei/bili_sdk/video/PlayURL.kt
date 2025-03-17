package com.laohei.bili_sdk.video

import android.util.Log
import com.laohei.bili_sdk.apis.VIDEO_PLAY_URL
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.video.VideoURLModel
import com.laohei.bili_sdk.wbi.Wbi
import com.laohei.bili_sdk.wbi.WbiParams
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class PlayURL(private val client: HttpClient) {

    companion object {
        private val TAG = PlayURL::class.simpleName
    }

    suspend fun videoUrl(
        aid: Long,
        bvid: String,
        cid: Long,
        qn: Int = 127,
        fnval: Int = 4048,
        cookie: String? = null
    ) = withContext(Dispatchers.IO) {
        if (WbiParams.wbi == null) {
            Log.d(TAG, "videoUrl: ${WbiParams.wbi}")
            Wbi.getWbiRequest(client).wbi(cookie)
        }
        val param = WbiParams.wbi!!.enc(
            mapOf("aid" to aid, "bvid" to bvid, "cid" to cid, "qn" to qn, "fnval" to fnval)
        )
        val response = try {
            client.get("${VIDEO_PLAY_URL}?$param") {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                }
                Log.d(TAG, "videoUrl: $url")
            }
        } catch (_: Exception) {
            null
        }
//        Log.d(TAG, "videoUrl: ${response?.bodyAsText()}")
        response?.run {
            Json.decodeFromString<BiliResponse<VideoURLModel>>(bodyAsText())
        }
    }

}
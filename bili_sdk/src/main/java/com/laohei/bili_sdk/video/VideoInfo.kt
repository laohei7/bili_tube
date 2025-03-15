package com.laohei.bili_sdk.video

import android.util.Log
import com.laohei.bili_sdk.apis.VIDEO_DETAIL_URL
import com.laohei.bili_sdk.apis.VIDEO_INFO
import com.laohei.bili_sdk.model.BiliVideoInfo
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.video.VideoDetailModel
import com.laohei.bili_sdk.wbi.Wbi
import com.laohei.bili_sdk.wbi.WbiParams
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class VideoInfo(
    private val client: HttpClient
) {
    companion object {
        private val TAG = VideoInfo::class.simpleName
    }

    suspend fun videoInfo(
        aid: Long,
        bvid: String,
        cookie: String? = null
    ) = withContext(Dispatchers.IO) {
        if (WbiParams.wbi == null) {
            Log.d(TAG, "videoUrl: ${WbiParams.wbi}")
            Wbi.getWbiRequest(client).wbi(cookie)
        }
        val param = WbiParams.wbi!!.enc(
            mapOf("aid" to aid, "bvid" to bvid)
        )
        val response = try {
            client.get("$VIDEO_INFO?$param") {
                url {
                    Log.d(TAG, "videoInfo: $url")
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                }
            }
        } catch (e: Exception) {
            null
        }
        Log.d(TAG, "videoInfo: ${response?.bodyAsText()}")
        response?.body<BiliResponse<BiliVideoInfo>>()
    }

    suspend fun getVideoDetail(
        aid: Long,
        bvid: String,
        cookie: String? = null
    ) = withContext(Dispatchers.IO) {
        if (WbiParams.wbi == null) {
            Log.d(TAG, "videoUrl: ${WbiParams.wbi}")
            Wbi.getWbiRequest(client).wbi(cookie)
        }
        val param = WbiParams.wbi!!.enc(
            mapOf("aid" to aid, "bvid" to bvid)
        )
        val response = try {
            client.get("$VIDEO_DETAIL_URL?$param") {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                }
                Log.d(TAG, "videoInfo: $url")
            }
        } catch (e: Exception) {
            null
        }
        Log.d(TAG, "videoInfo: ${response?.bodyAsText()}")
        response?.run {
            Json.decodeFromString<BiliResponse<VideoDetailModel>>(bodyAsText())
        }
    }
}
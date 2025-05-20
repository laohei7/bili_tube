package com.laohei.bili_sdk.video

import android.util.Log
import com.laohei.bili_sdk.apis.BANGUMI_PLAY_URL
import com.laohei.bili_sdk.apis.BILIBILI
import com.laohei.bili_sdk.apis.VIDEO_PLAY_URL
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.common.BiliResponse2
import com.laohei.bili_sdk.module_v2.video.VideoURLModel
import com.laohei.bili_sdk.wbi.GetWbi
import com.laohei.bili_sdk.wbi.WbiParams
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class GetURL(private val client: HttpClient) {

    companion object {
        private val TAG = GetURL::class.simpleName
        private const val DBG = true
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
            if (DBG) {
                Log.d(TAG, "videoUrl: ${WbiParams.wbi}")
            }
            GetWbi.getWbiRequest(client).wbi(cookie)
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
                if (DBG) {
                    Log.d(TAG, "videoUrl: $url")
                }
            }
        } catch (_: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<VideoURLModel>>(bodyAsText())
        }
    }

    suspend fun mediaUrl(
        avid: Long? = null,
        bvid: String? = null,
        cid: Long? = null,
        epId: Long? = null,
        qn: Int = 127,
        fnval: Int = 4048,
        cookie: String? = null
    ) = withContext(Dispatchers.IO) {
        if (cid == null && epId == null) {
            return@withContext null
        }
        if (WbiParams.wbi == null) {
            if (DBG) {
                Log.d(TAG, "mediaUrl: ${WbiParams.wbi}")
            }
            GetWbi.getWbiRequest(client).wbi(cookie)
        }
        val param = WbiParams.wbi!!.enc(
            buildMap {
                avid?.let { put("avid", it) }
                bvid?.let { put("bvid", it) }
                cid?.let { put("cid", it) }
                epId?.let { put("ep_id", it) }
                put("qn", qn)
                put("fnval", fnval)
            }
        )
        val response = try {
            client.get("${BANGUMI_PLAY_URL}?$param") {
                cookie?.apply {
                    header(HttpHeaders.Cookie, this)
                }
                header(HttpHeaders.Referrer, BILIBILI)
                if (DBG) {
                    Log.d(TAG, "mediaUrl: $url")
                }
            }
        } catch (_: Exception) {
            null
        }
        response?.run {
            if (DBG) {
                Log.d(TAG, "mediaUrl: ${bodyAsText()}")
            }
            Json.decodeFromString<BiliResponse2<VideoURLModel>>(bodyAsText())
        }
    }
}
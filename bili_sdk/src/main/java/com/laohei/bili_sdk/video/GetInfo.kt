package com.laohei.bili_sdk.video

import android.util.Log
import com.laohei.bili_sdk.apis.BANGUMI_DETAIL_URL
import com.laohei.bili_sdk.apis.VIDEO_DETAIL_URL
import com.laohei.bili_sdk.apis.VIDEO_HAS_COIN_URL
import com.laohei.bili_sdk.apis.VIDEO_HAS_FAVORED_URL
import com.laohei.bili_sdk.apis.VIDEO_HAS_LIKE_URL
import com.laohei.bili_sdk.apis.VIDEO_INFO
import com.laohei.bili_sdk.apis.VIDEO_LIKE_URL
import com.laohei.bili_sdk.model.BiliVideoInfo
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.common.BiliResponse2
import com.laohei.bili_sdk.module_v2.common.BiliResponseNoData
import com.laohei.bili_sdk.module_v2.video.BangumiDetailModel
import com.laohei.bili_sdk.module_v2.video.CoinModel
import com.laohei.bili_sdk.module_v2.video.FavouredModel
import com.laohei.bili_sdk.module_v2.video.VideoDetailModel
import com.laohei.bili_sdk.wbi.GetWbi
import com.laohei.bili_sdk.wbi.WbiParams
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
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

class GetInfo(
    private val client: HttpClient
) {
    companion object {
        private val TAG = GetInfo::class.simpleName
        private const val DBG = true
    }

    suspend fun videoInfo(
        aid: Long,
        bvid: String,
        cookie: String? = null
    ) = withContext(Dispatchers.IO) {
        if (WbiParams.wbi == null) {
            Log.d(TAG, "videoUrl: ${WbiParams.wbi}")
            GetWbi.getWbiRequest(client).wbi(cookie)
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
            GetWbi.getWbiRequest(client).wbi(cookie)
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

    suspend fun bangumiDetail(
        seasonId: Long? = null,
        epId: Long? = null,
        cookie: String? = null
    ) = withContext(Dispatchers.IO) {
        when {
            seasonId == null && epId == null -> return@withContext null
            else -> {}
        }
        val response = try {
            client.get(BANGUMI_DETAIL_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                    seasonId?.let {
                        parameters.append("season_id", it.toString())
                    }
                    epId?.let {
                        parameters.append("ep_id", it.toString())
                    }
                }
                if (DBG) {
                    Log.d(TAG, "bangumiDetail: $url")
                }
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse2<BangumiDetailModel>>(bodyAsText())
        }
    }

    suspend fun hasLike(
        aid: Long,
        bvid: String,
        cookie: String? = null
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(VIDEO_HAS_LIKE_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                        parameters.append("aid", aid.toString())
                        parameters.append("bvid", bvid)
                    }
                }
                Log.d(TAG, "videoInfo: $url")
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<Int>>(bodyAsText())
        }
    }

    suspend fun hasCoin(
        aid: Long,
        bvid: String,
        cookie: String? = null
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(VIDEO_HAS_COIN_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                        parameters.append("aid", aid.toString())
                        parameters.append("bvid", bvid)
                    }
                }
                Log.d(TAG, "videoInfo: $url")
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<CoinModel>>(bodyAsText())
        }
    }

    suspend fun hasFavoured(
        aid: Long,
        cookie: String? = null
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(VIDEO_HAS_FAVORED_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                        parameters.append("aid", aid.toString())
                    }
                }
                Log.d(TAG, "videoInfo: $url")
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<FavouredModel>>(bodyAsText())
        }
    }

    suspend fun videoLike(
        aid: Long,
        bvid: String,
        like: Int,
        cookie: String? = null,
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.post(VIDEO_LIKE_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                }
                Log.d(TAG, "videoInfo: $url")
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("aid", aid.toString())
                            append("bvid", bvid)
                            append("like", like.toString())
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
            Json.decodeFromString<BiliResponseNoData>(bodyAsText())
        }
    }
}
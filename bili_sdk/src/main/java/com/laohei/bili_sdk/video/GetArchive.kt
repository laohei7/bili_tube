package com.laohei.bili_sdk.video

import android.util.Log
import com.laohei.bili_sdk.apis.VIDEO_ARCHIVE_URL
import com.laohei.bili_sdk.apis.VIDEO_PAGELIST_URL
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.video.VideoArchiveModel
import com.laohei.bili_sdk.module_v2.video.VideoPageListModel
import com.laohei.bili_sdk.wbi.WbiParams
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class GetArchive(
    private val client: HttpClient
) {
    companion object {
        private val TAG = GetArchive::class.simpleName
        private const val DBG = true
    }

    suspend fun videoArchive(
        mid: Long,
        seasonId: Long,
        pageNum: Int = 1,
        pageSize: Int = 30,
        sortReverse: Boolean = true,
        cookie: String? = null
    ) = withContext(Dispatchers.IO) {
        val url = WbiParams.wbi?.run {
            val param = this.enc(
                mapOf(
                    "mid" to mid.toString(),
                    "season_id" to seasonId.toString(),
                    "page_num" to pageNum.toString(),
                    "page_size" to pageSize.toString(),
                    "sort_reverse" to sortReverse.toString()
                )
            )
            "$VIDEO_ARCHIVE_URL?$param"
        } ?: VIDEO_ARCHIVE_URL

        val response = try {
            client.get(url) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                    if (WbiParams.wbi == null) {
                        parameters.append("mid", mid.toString())
                        parameters.append("season_id", seasonId.toString())
                        parameters.append("page_num", pageNum.toString())
                        parameters.append("page_size", pageSize.toString())
                        parameters.append("sort_reverse", sortReverse.toString())
                    }
                }
                Log.d(TAG, "videoArchive: $url")
            }
        } catch (_: Exception) {
            null
        }
        response?.run {
            Log.d(TAG, "videoArchive: ${bodyAsText()}")
            Json.decodeFromString<BiliResponse<VideoArchiveModel>>(bodyAsText())
        }
    }

    suspend fun videoPageList(bvid: String, cookie: String? = null) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(VIDEO_PAGELIST_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                    parameters.append("bvid", bvid)
                }
                if (DBG) {
                    Log.d(TAG, "videoPageList: $url")
                }
            }
        } catch (_: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<List<VideoPageListModel>>>(bodyAsText())
        }
    }
}
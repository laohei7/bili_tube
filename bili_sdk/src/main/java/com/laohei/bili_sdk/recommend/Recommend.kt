package com.laohei.bili_sdk.recommend

import com.laohei.bili_sdk.apis.RECOMMEND_VIDEOS
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.recomment.RecommendModel
import com.laohei.bili_sdk.wbi.WbiParams
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class Recommend(private val client: HttpClient) {

    companion object {
        private val TAG = Recommend::class.simpleName
    }

    suspend fun recommendVideos(
        cookie: String? = null,
        refreshType: Int = 4,
        lastShowList: String? = null,
        feedVersion: String = "V8",
        freshIdx: Int = 1,
        freshIdx1h: Int = 1,
        brush: Int = 1,
        webLocation: Int = 1430650,
        homepageVer: Int = 1,
        fetchRow: Int = 1,
        ps: Int = 12,
        yNum: Int = 3
    ) = withContext(Dispatchers.IO) {
        val response = try {
            val url = if (WbiParams.wbi != null) {
                val param = WbiParams.wbi!!.enc(
                    mapOf(
                        "fresh_type" to refreshType,
                        "fresh_idx" to freshIdx,
                        "fresh_idx_1h" to freshIdx1h,
                        "web_location" to webLocation,
                        "y_num" to yNum,
                        "homepage_ver" to homepageVer,
                        "feed_version" to feedVersion,
                        "brush" to brush,
                        "fetch_row" to fetchRow,
                        "ps" to ps,
                        "last_showlist" to lastShowList
                    )
                )
                "${RECOMMEND_VIDEOS}?$param"
            } else {
                RECOMMEND_VIDEOS
            }

            client.get(url) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                }
//                Log.d(TAG, "recommendVideos: $url")
            }
        } catch (_: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<RecommendModel>>(this.bodyAsText())
        }
    }
}
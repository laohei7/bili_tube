package com.laohei.bili_sdk.apis.impl

import com.laohei.bili_sdk.apis.URL_DYNAMIC
import com.laohei.bili_sdk.apis.URL_HOT
import com.laohei.bili_sdk.apis.URL_RECOMMEND
import com.laohei.bili_sdk.apis.VideoApi
import com.laohei.bili_sdk.exception.globalSDKExceptionHandle
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.dynamic.DynamicModel
import com.laohei.bili_sdk.module_v2.hot.HotModel
import com.laohei.bili_sdk.module_v2.recomment.RecommendModel
import com.laohei.bili_sdk.wbi.WbiParams
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class VideoApiImpl(
    private val client: HttpClient
) : VideoApi {

    companion object {
        private val TAG = VideoApiImpl::class.simpleName
        private const val DBG = true
    }

    override suspend fun getRecommends(
        cookie: String?,
        refreshType: Int,
        lastShowList: String?,
        feedVersion: String,
        freshIdx: Int,
        freshIdx1h: Int,
        brush: Int,
        webLocation: Int,
        homepageVer: Int,
        fetchRow: Int,
        ps: Int,
        yNum: Int
    ): BiliResponse<RecommendModel> = withContext(Dispatchers.IO) {
        runCatching {
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
                "${URL_RECOMMEND}?$param"
            } else {
                URL_RECOMMEND
            }

            val response = client.get(url) {
                cookie?.apply {
                    header(HttpHeaders.Cookie, this)
                }
            }
            Json.decodeFromString<BiliResponse<RecommendModel>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse(
                    code = 400,
                    message = "ERROR",
                    data = RecommendModel(
                        item = emptyList(),
                        mid = -1
                    )
                )
            }
        )

    }

    override suspend fun getHots(
        cookie: String?,
        pn: Int,
        ps: Int
    ): BiliResponse<HotModel> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get(URL_HOT) {
                cookie?.apply {
                    header(HttpHeaders.Cookie, this)
                }
                parameter("pn", pn.toString())
                parameter("ps", ps.toString())
            }
            Json.decodeFromString<BiliResponse<HotModel>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse(
                    code = 400,
                    message = "ERROR",
                    data = HotModel(emptyList())
                )
            }
        )
    }

    override suspend fun getDynamics(
        cookie: String?,
        type: String,
        page: Int,
        offset: Long?
    ): BiliResponse<DynamicModel> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get(URL_DYNAMIC) {
                cookie?.apply {
                    header(HttpHeaders.Cookie, this)
                }
                parameter("type", type)
                parameter("page", page.toString())
                offset?.let {
                    parameter("offset", it.toString())
                }
            }
            Json.decodeFromString<BiliResponse<DynamicModel>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse(
                    code = 400,
                    message = "ERROR",
                    data = DynamicModel(
                        hasMore = false,
                        items = emptyList(),
                        offset = 0,
                        updateNum = 0,
                        updateBaseline = ""
                    )
                )
            }
        )
    }
}
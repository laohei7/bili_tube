package com.laohei.bili_sdk.apis.impl

import com.laohei.bili_sdk.apis.URL_BANGUMI_FILTER
import com.laohei.bili_sdk.apis.BangumiApi
import com.laohei.bili_sdk.apis.URL_RELATED_BANGUMI
import com.laohei.bili_sdk.apis.URL_BANGUMI_TIMELINE
import com.laohei.bili_sdk.exception.globalSDKExceptionHandle
import com.laohei.bili_sdk.module_v2.bangumi.AnimeScheduleModel
import com.laohei.bili_sdk.module_v2.bangumi.BangumiModel
import com.laohei.bili_sdk.module_v2.bangumi.RelatedBangumiModel
import com.laohei.bili_sdk.module_v2.common.BiliResponse2
import com.laohei.bili_sdk.module_v2.common.BiliResponse3
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class BangumiApiImpl(
    private val client: HttpClient
) : BangumiApi {

    companion object {
        private val TAG = BangumiApiImpl::class.simpleName
        private const val DBG = true
    }

    override suspend fun searchBangumis(
        st: Int,
        order: Int,
        sort: Int,
        page: Int,
        seasonType: Int,
        pageSize: Int,
        type: Int,
        seasonVersion: String,
        spokenLanguageType: String,
        area: String,
        isFinish: String,
        copyright: String,
        seasonStatus: String,
        seasonMonth: String,
        styleId: String,
        cookie: String?
    ): BiliResponse3<BangumiModel> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get(URL_BANGUMI_FILTER) {
                cookie?.apply {
                    header(HttpHeaders.Cookie, this)
                }
                parameter("st", st.toString())
                parameter("order", order.toString())
                parameter("sort", sort.toString())
                parameter("page", page.toString())
                parameter("season_type", seasonType.toString())
                parameter("pagesize", pageSize.toString())
                parameter("type", type.toString())
                parameter("season_version", seasonVersion)
                if (st == 1) {
                    parameter("spoken_language_type", spokenLanguageType)
                    parameter("area", area)
                }
                parameter("is_finish", isFinish)
                parameter("copyright", copyright)
                parameter("season_status", seasonStatus)
                parameter("season_month", seasonMonth)
                parameter("style_id", styleId)
            }
            Json.decodeFromString<BiliResponse3<BangumiModel>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse3(
                    code = 400,
                    message = "ERROR",
                    data = BangumiModel(0, 0, 0, 0, emptyList())
                )
            }
        )
    }

    override suspend fun relatedBangumis(
        seasonId: Long,
        cookie: String?
    ): BiliResponse3<RelatedBangumiModel> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get(URL_RELATED_BANGUMI) {
                cookie?.apply {
                    header(HttpHeaders.Cookie, this)
                }
                parameter("season_id", seasonId.toString())
            }
            Json.decodeFromString<BiliResponse3<RelatedBangumiModel>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse3(
                    code = 400,
                    message = "ERROR",
                    data = RelatedBangumiModel(emptyList())
                )
            }
        )
    }

    override suspend fun getTimeline(
        types: Int,
        before: Int?,
        after: Int?,
        cookie: String?
    ): BiliResponse2<List<AnimeScheduleModel>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get(
                URL_BANGUMI_TIMELINE
            ) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                    parameters.append("types", types.toString())
                    before?.let {
                        parameters.append("before", it.toString())
                    }
                    after?.let {
                        parameters.append("after", it.toString())
                    }
                }
            }
            Json.decodeFromString<BiliResponse2<List<AnimeScheduleModel>>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse2(
                    code = 400,
                    message = "ERROR",
                    result = emptyList()
                )
            }
        )
    }
}
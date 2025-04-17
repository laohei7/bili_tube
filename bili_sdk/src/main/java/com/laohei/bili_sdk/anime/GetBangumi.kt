package com.laohei.bili_sdk.anime

import com.laohei.bili_sdk.apis.BANGUMI_FILTER_URL
import com.laohei.bili_sdk.apis.RELATED_BANGUMI_URL
import com.laohei.bili_sdk.module_v2.bangumi.BangumiModel
import com.laohei.bili_sdk.module_v2.bangumi.RelatedBangumiModel
import com.laohei.bili_sdk.module_v2.common.BiliResponse3
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class GetBangumi(
    private val client: HttpClient
) {

    companion object {
        private const val DBG = true
        private val TAG = GetBangumi::class.simpleName
    }

    suspend fun bangumis(
        st: Int = 1,
        order: Int = 3,
        sort: Int = 0,
        page: Int = 1,
        seasonType: Int = 1,
        pageSize: Int = 20,
        type: Int = 1,
        seasonVersion: String = "-1",
        spokenLanguageType: String = "-1",
        area: String = "-1",
        isFinish: String = "-1",
        copyright: String = "-1",
        seasonStatus: String = "-1",
        seasonMonth: String = "-1",
        styleId: String = "-1",
        cookie: String? = null,
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(BANGUMI_FILTER_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                    parameters.append("st", st.toString())
                    parameters.append("order", order.toString())
                    parameters.append("sort", sort.toString())
                    parameters.append("page", page.toString())
                    parameters.append("season_type", seasonType.toString())
                    parameters.append("pagesize", pageSize.toString())
                    parameters.append("type", type.toString())
                    parameters.append("season_version", seasonVersion)
                    if (st == 1) {
                        parameters.append("spoken_language_type", spokenLanguageType)
                        parameters.append("area", area)
                    }
                    parameters.append("is_finish", isFinish)
                    parameters.append("copyright", copyright)
                    parameters.append("season_status", seasonStatus)
                    parameters.append("season_month", seasonMonth)
                    parameters.append("style_id", styleId)
                }
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse3<BangumiModel>>(bodyAsText())
        }
    }

    suspend fun relatedBangumis(
        seasonId: Long,
        cookie: String? = null,
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(RELATED_BANGUMI_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                    parameters.append("season_id", seasonId.toString())
                }
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse3<RelatedBangumiModel>>(bodyAsText())
        }
    }
}
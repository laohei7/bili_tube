package com.laohei.bili_sdk.apis.impl

import android.util.Log
import com.laohei.bili_sdk.apis.BILIBILI
import com.laohei.bili_sdk.apis.HistoryApi
import com.laohei.bili_sdk.apis.URL_ADD_TO_VIEW
import com.laohei.bili_sdk.apis.URL_CLEAR_HISTORY
import com.laohei.bili_sdk.apis.URL_CLEAR_TO_VIEW
import com.laohei.bili_sdk.apis.URL_DEL_HISTORY
import com.laohei.bili_sdk.apis.URL_DEL_TO_VIEW
import com.laohei.bili_sdk.apis.URL_HISTORY
import com.laohei.bili_sdk.apis.URL_TO_VIEW
import com.laohei.bili_sdk.apis.URL_VIDEO_HISTORY_REPORT
import com.laohei.bili_sdk.exception.globalSDKExceptionHandle
import com.laohei.bili_sdk.model_v2.common.BiliResponse
import com.laohei.bili_sdk.model_v2.common.BiliResponseNoData
import com.laohei.bili_sdk.model_v2.history.HistoryCursor
import com.laohei.bili_sdk.model_v2.history.HistoryModel
import com.laohei.bili_sdk.model_v2.history.ToViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
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

class HistoryApiImpl(
    private val client: HttpClient
) : HistoryApi {

    companion object {
        private val TAG = HistoryApiImpl::class.simpleName
        private const val DBG = true
    }

    override suspend fun getToView(
        cookie: String?,
        pn: Int,
        ps: Int
    ): BiliResponse<ToViewModel> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get(URL_TO_VIEW) {
                cookie?.apply {
                    header(HttpHeaders.Cookie, this)
                }
                parameter("pn", pn.toString())
                parameter("ps", ps.toString())
                if (DBG) {
                    Log.d(TAG, "getToView: $url")
                }
            }
            Json.decodeFromString<BiliResponse<ToViewModel>>(response.bodyAsText())
        }.getOrElse {
            if (DBG) {
                globalSDKExceptionHandle(TAG.toString(), it)
            }
            BiliResponse.ERROR
        }
    }

    override suspend fun addToView(
        cookie: String?,
        aid: Long,
        bvid: String,
        csrf: String?
    ): BiliResponseNoData = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.post(URL_ADD_TO_VIEW) {
                cookie?.let {
                    header(HttpHeaders.Cookie, it)
                }
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("aid", aid.toString())
                            append("bvid", bvid)
                            csrf?.let { append("csrf", it) }
                        }
                    ))
            }
            Json.decodeFromString<BiliResponseNoData>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponseNoData.ERROR
            }
        )
    }

    override suspend fun delToView(
        cookie: String?,
        viewed: Boolean,
        aid: Long?,
        csrf: String?
    ): BiliResponseNoData = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.post(URL_DEL_TO_VIEW) {
                cookie?.let {
                    header(HttpHeaders.Cookie, it)
                }
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("viewed", viewed.toString())
                            aid?.let { append("aid", it.toString()) }
                            csrf?.let { append("csrf", it) }
                        }
                    )
                )
            }
            response.body<BiliResponseNoData>()
        }.getOrElse {
            BiliResponseNoData.ERROR
        }
    }

    override suspend fun clearToView(
        cookie: String?,
        csrf: String?
    ): BiliResponseNoData =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = client.post(URL_CLEAR_TO_VIEW) {
                    cookie?.let {
                        header(HttpHeaders.Cookie, it)
                    }
                    contentType(ContentType.Application.FormUrlEncoded)
                    setBody(
                        FormDataContent(
                            Parameters.build {
                                csrf?.let { append("csrf", it) }
                            }
                        )
                    )
                }
                response.body<BiliResponseNoData>()
            }.getOrElse {
                BiliResponseNoData.ERROR
            }
        }

    override suspend fun getHistories(
        cookie: String?,
        ps: Int,
        max: Long?,
        business: String?,
        viewAt: Long?
    ): BiliResponse<HistoryModel> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get(URL_HISTORY) {
                cookie?.let {
                    header(HttpHeaders.Cookie, it)
                }
                max?.let {
                    parameter("max", it.toString())
                }
                business?.let {
                    parameter("business", it)
                }
                viewAt?.let {
                    parameter("view_at", it.toString())
                }
                parameter("ps", ps.toString())
                if (DBG) {
                    Log.d(TAG, "getHistories: $url")
                }
            }
            Json.decodeFromString<BiliResponse<HistoryModel>>(response.bodyAsText())
        }.getOrElse {
            if (DBG) {
                globalSDKExceptionHandle(TAG.toString(), it)
            }
            BiliResponse(
                code = 400,
                message = "EMPTY",
                data = HistoryModel(
                    cursor = HistoryCursor(),
                    list = emptyList()
                )
            )
        }
    }

    override suspend fun postHistory(
        cookie: String?,
        aid: String,
        cid: String,
        progress: Long,
        biliJct: String
    ): BiliResponseNoData = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.post(URL_VIDEO_HISTORY_REPORT) {
                cookie?.apply {
                    header(HttpHeaders.Cookie, this)
                }
                header(HttpHeaders.Referrer, BILIBILI)
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("aid", aid)
                            append("cid", cid)
                            append("progress", progress.toString())
                            append("csrf", biliJct)
                        }
                    )
                )
            }
            Json.decodeFromString<BiliResponseNoData>(response.bodyAsText())
        }
    }.fold(
        onSuccess = { it },
        onFailure = {
            if (DBG) {
                globalSDKExceptionHandle(TAG.toString(), it)
            }
            BiliResponseNoData(
                code = 400,
                message = "EMPTY",
            )
        }
    )

    override suspend fun delHistory(
        cookie: String?,
        kid: String,
        csrf: String?
    ): BiliResponseNoData = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.post(URL_DEL_HISTORY) {
                cookie?.let {
                    header(HttpHeaders.Cookie, it)
                }
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("kid", kid)
                            csrf?.let { append("csrf", it) }
                        }
                    )
                )
            }
            response.body<BiliResponseNoData>()
        }.getOrElse {
            BiliResponseNoData.ERROR
        }
    }

    override suspend fun clearHistory(cookie: String?, csrf: String?): BiliResponseNoData =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = client.post(URL_CLEAR_HISTORY) {
                    cookie?.let {
                        header(HttpHeaders.Cookie, it)
                    }
                    contentType(ContentType.Application.FormUrlEncoded)
                    setBody(
                        FormDataContent(
                            Parameters.build {
                                csrf?.let { append("csrf", it) }
                            }
                        )
                    )
                }
                response.body<BiliResponseNoData>()
            }.getOrElse {
                BiliResponseNoData.ERROR
            }
        }
}
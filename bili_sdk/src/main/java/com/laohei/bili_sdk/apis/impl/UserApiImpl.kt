package com.laohei.bili_sdk.apis.impl

import android.util.Log
import com.laohei.bili_sdk.apis.URL_USER_RELATION_MODIFY
import com.laohei.bili_sdk.apis.UserApi
import com.laohei.bili_sdk.apis.UserRelationAction
import com.laohei.bili_sdk.module_v2.common.BiliResponseNoData
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.header
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

class UserApiImpl(
    private val client: HttpClient
) : UserApi {

    companion object {
        private val TAG = UserApiImpl::class.simpleName
        private const val DBG = true
    }

    override suspend fun postRelationModify(
        cookie: String?,
        mid: Long,
        act: UserRelationAction,
        csrf: String?
    ): BiliResponseNoData = withContext(Dispatchers.IO) {
        val response = try {
            client.post(URL_USER_RELATION_MODIFY) {
                cookie?.apply {
                    header(HttpHeaders.Cookie, cookie)
                }
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("fid", mid.toString())
                            append("act", act.id.toString())
                            csrf?.let { append("csrf", it) }
                        }
                    )
                )
            }
        } catch (e: Exception) {
            if (DBG) {
                Log.d(TAG, "postRelationModify: ${e.message}")
            }
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponseNoData>(bodyAsText())
        } ?: BiliResponseNoData.ERROR
    }
}
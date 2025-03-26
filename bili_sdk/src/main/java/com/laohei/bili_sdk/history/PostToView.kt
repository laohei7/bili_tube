package com.laohei.bili_sdk.history

import com.laohei.bili_sdk.apis.ADD_TO_VIEW_URL
import com.laohei.bili_sdk.apis.VIDEO_FOLDER_DEAL_URL
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.common.BiliResponseNoData
import com.laohei.bili_sdk.module_v2.folder.FolderDealModel
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent
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

class PostToView(
    private val client: HttpClient
) {
    suspend fun addToView(
        aid: Long,
        bvid: String,
        cookie: String? = null
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.post(ADD_TO_VIEW_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, cookie)
                    }
                }
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("aid", aid.toString())
                            append("bvid", bvid)
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
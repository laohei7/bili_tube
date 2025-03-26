package com.laohei.bili_sdk.folder

import android.util.Log
import com.laohei.bili_sdk.apis.VIDEO_FOLDER_DEAL_URL
import com.laohei.bili_sdk.module_v2.common.BiliResponse
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

class PostFolder(
    private val client: HttpClient
) {

    companion object{
        private val TAG = PostFolder::class.simpleName
    }

    suspend fun folderDeal(
        rid: Long,
        type: Int = 2,
        addMediaIds: Set<Long>,
        delMediaIds: Set<Long>,
        cookie: String? = null,
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.post(VIDEO_FOLDER_DEAL_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, cookie)
                    }
                }
                Log.d(TAG, "videoInfo: $url")
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("rid", rid.toString())
                            append("type", type.toString())
                            append("add_media_ids", addMediaIds.joinToString(","))
                            append("del_media_ids", delMediaIds.joinToString(","))
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
            Json.decodeFromString<BiliResponse<FolderDealModel>>(bodyAsText())
        }
    }
}
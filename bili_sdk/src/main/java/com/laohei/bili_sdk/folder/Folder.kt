package com.laohei.bili_sdk.folder

import com.laohei.bili_sdk.apis.FOLDER_URL
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.folder.FolderModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class Folder(
    private val client: HttpClient
) {
    suspend fun folderList(
        cookie: String? = null,
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(FOLDER_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                }
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<List<FolderModel>>>(this.bodyAsText())
        }
    }
}
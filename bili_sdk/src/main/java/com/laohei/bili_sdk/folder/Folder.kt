package com.laohei.bili_sdk.folder

import android.util.Log
import com.laohei.bili_sdk.apis.FOLDER_SIMPLE_URL
import com.laohei.bili_sdk.apis.FOLDER_URL
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.folder.FolderModel
import com.laohei.bili_sdk.module_v2.folder.FolderSimpleModel
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

    companion object {
        private val TAG = Folder::class.simpleName
    }

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
                Log.d(TAG, "folderList: $url")
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<List<FolderModel>>>(this.bodyAsText())
        }
    }

    suspend fun folderSimpleList(
        cookie: String? = null,
        rid:Long,
        upMid: Long
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(FOLDER_SIMPLE_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                    parameters.append("rid", rid.toString())
                    parameters.append("up_mid", upMid.toString())
                }
                Log.d(TAG, "folderSimpleList: $url")
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<FolderSimpleModel>>(this.bodyAsText())
        }
    }
}
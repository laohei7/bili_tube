package com.laohei.bili_sdk.folder

import android.util.Log
import com.laohei.bili_sdk.apis.URL_SIMPLE_FOLDER
import com.laohei.bili_sdk.apis.URL_FOLDER
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.folder.FolderModel
import com.laohei.bili_sdk.module_v2.folder.SimpleFolderModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class GetFolder(
    private val client: HttpClient
) {

    companion object {
        private val TAG = GetFolder::class.simpleName
    }

    suspend fun folderList(
        cookie: String? = null,
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(URL_FOLDER) {
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
            client.get(URL_SIMPLE_FOLDER) {
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
            Json.decodeFromString<BiliResponse<SimpleFolderModel>>(this.bodyAsText())
        }
    }
}
package com.laohei.bili_sdk.user

import android.util.Log
import com.laohei.bili_sdk.apis.URL_USER_UPLOADED_VIDEO
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.user.UploadedVideoModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class GetUploadedVideo(
    private val client: HttpClient
) {

    companion object {
        private val TAG = GetUploadedVideo::class.simpleName
    }

    suspend fun uploadedVideos(
        cookie: String?,
        vmid: Long,
        aid: Long? = null
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(URL_USER_UPLOADED_VIDEO) {
                cookie?.let {
                    header(HttpHeaders.Cookie, cookie)
                }
                parameter("vmid", vmid.toString())
                aid?.let {
                    parameter("aid", it.toString())
                }
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            try {
                Json.decodeFromString<BiliResponse<UploadedVideoModel>>(bodyAsText())
            } catch (e: SerializationException) {
                Log.d(TAG, "uploadedVideos: ${e.message}")
                null
            }
        }
    }
}
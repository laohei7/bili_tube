package com.laohei.bili_sdk.wbi

import com.laohei.bili_sdk.apis.URL_USER_PROFILE
import com.laohei.bili_sdk.exception.globalSDKExceptionHandle
import com.laohei.bili_sdk.model.BiliResponse
import com.laohei.bili_sdk.model.BiliWbi
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class GetWbi(private val client: HttpClient) {

    companion object {
        private const val DBG = true
        private val TAG = GetWbi::class.simpleName

        private var wbi: GetWbi? = null

        fun getWbiRequest(client: HttpClient): GetWbi {
            if (wbi == null) {
                wbi = GetWbi(client)
            }
            return wbi!!
        }
    }

    suspend fun wbi(
        cookie: String? = null,
        setWbiBlock: (suspend (BiliWbi) -> Unit)? = null
    ) = withContext(Dispatchers.IO) {
        val biliWbi = runCatching {
            val response = client.get(URL_USER_PROFILE) {
                cookie?.apply {
                    headers.append(HttpHeaders.Cookie, this)
                }
            }
            Json.decodeFromString<BiliResponse<BiliWbi>>(response.bodyAsText()).data
        }.getOrElse {
            if (DBG) {
                globalSDKExceptionHandle(TAG, it)
            }
            null
        }

        biliWbi?.let {
            setWbiBlock?.invoke(it)
        }
    }

}
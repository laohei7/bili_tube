package com.laohei.bili_sdk.wbi

import android.util.Log
import com.laohei.bili_sdk.apis.WBI
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
        private val TAG = GetWbi::class.simpleName

        private var wbi: GetWbi? = null

        fun getWbiRequest(client: HttpClient): GetWbi {
            if (wbi == null) {
                wbi = GetWbi(client)
            }
            return wbi!!
        }
    }

    suspend fun wbi(cookie: String? = null, setWbiBlock: (suspend (BiliWbi) -> Unit)? = null) =
        withContext(Dispatchers.IO) {
            val response = try {
                client.get(WBI) {
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
                val biliWbi = Json.decodeFromString<BiliResponse<BiliWbi>>(bodyAsText()).data
                if (WbiParams.wbi == null) {
                    WbiParams.initWbi(biliWbi.wbiImg.imgUrl, biliWbi.wbiImg.subUrl)
                }
                Log.d(TAG, "videoUrl: ${WbiParams.wbi}")
                setWbiBlock?.invoke(biliWbi)
            }
        }

}
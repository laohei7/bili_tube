package com.laohei.bili_sdk.login

import android.util.Log
import com.laohei.bili_sdk.apis.CHECK_SCAN_STATUS
import com.laohei.bili_sdk.apis.REQUEST_QRCODE
import com.laohei.bili_sdk.model.BiliQRCode
import com.laohei.bili_sdk.model.BiliQRCodeStatus
import com.laohei.bili_sdk.model.BiliResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class QRLogin(private val client: HttpClient) {
    companion object {
        private val TAG = QRLogin::class.simpleName
    }

    suspend fun requestQRCode() = withContext(Dispatchers.IO) {
        val response = try {
            client.get(REQUEST_QRCODE)
        } catch (e: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<BiliQRCode>>(this.bodyAsText())
        }
    }

    suspend fun checkScanStatus(
        qrcodeKey: String,
        setCookieBlock: (suspend (Headers) -> Unit)? = null
    ) = withContext(Dispatchers.IO) {
        val status: BiliQRCodeStatus
        while (true) {
            delay(1500)
            val response = try {
                client.get(CHECK_SCAN_STATUS) {
                    url {
                        parameters.append("qrcode_key", qrcodeKey)
                    }
                }
            } catch (e: Exception) {
                null
            }
            val currentStatus = response?.run {
                Json.decodeFromString<BiliResponse<BiliQRCodeStatus>>(this.bodyAsText())
                    .data
            }
            Log.d(TAG, "checkScanStatus: $currentStatus")

            when (currentStatus?.code) {
                86090, 86101 -> continue
                0 -> {
//                    Log.d(TAG, "checkScanStatus: ${response.headers.entries()}")
//                    Log.d(TAG, "checkScanStatus: ${response.headers.getAll(HttpHeaders.SetCookie)}")
                    setCookieBlock?.invoke(response.headers)
                    status = currentStatus
                    break
                }

                else -> {
                    status = currentStatus ?: BiliQRCodeStatus.networkError()
                    break
                }
            }
        }
        status
    }
}
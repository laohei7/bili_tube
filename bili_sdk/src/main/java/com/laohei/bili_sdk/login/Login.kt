package com.laohei.bili_sdk.login

import android.util.Log
import com.laohei.bili_sdk.apis.URL_CAPTCHA
import com.laohei.bili_sdk.apis.URL_CHECK_SCAN_STATUS
import com.laohei.bili_sdk.apis.URL_REQUEST_QRCODE
import com.laohei.bili_sdk.apis.URL_SEND_SMS_CODE
import com.laohei.bili_sdk.apis.URL_SMS_LOGIN
import com.laohei.bili_sdk.model.BiliQRCode
import com.laohei.bili_sdk.model.BiliQRCodeStatus
import com.laohei.bili_sdk.module_v2.captcha.CaptchaModel
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.login.LoginSuccessModel
import com.laohei.bili_sdk.module_v2.login.SMSCodeModel
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class Login(private val client: HttpClient) {
    companion object {
        private val TAG = Login::class.simpleName
        const val LOGIN_SOURCE_WEB = "main_web"
        const val LOGIN_SOURCE_MINI = "main_mini"
        const val LOGIN_SOURCE_HEADER = "main-fe-header"
    }

    suspend fun requestQRCode() = withContext(Dispatchers.IO) {
        val response = try {
            client.get(URL_REQUEST_QRCODE)
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
                client.get(URL_CHECK_SCAN_STATUS) {
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

    suspend fun getCaptcha(
        source: String = LOGIN_SOURCE_HEADER,
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(URL_CAPTCHA) {
                url {
                    parameter("source", source)
                }
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<CaptchaModel>>(bodyAsText())
        }
    }

    suspend fun sendSMSCode(
        cookie: String? = null,
        cid: String,
        tel: String,
        source: String = LOGIN_SOURCE_HEADER,
        token: String,
        challenge: String,
        validate: String,
        seccode: String
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.post(URL_SEND_SMS_CODE) {
                cookie?.apply {
                    header(HttpHeaders.Cookie, this)
                }
                header(
                    HttpHeaders.Referrer,
                    "https://www.bilibili.com"
                )
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("cid", cid)
                            append("tel", tel)
                            append("source", source)
                            append("token", token)
                            append("challenge", challenge)
                            append("validate", validate)
                            append("seccode", seccode)
                        }
                    )
                )
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<SMSCodeModel?>>(bodyAsText())
        }
    }

    suspend fun smsLogin(
        cid: String,
        tel: String,
        code: String,
        source: String = LOGIN_SOURCE_HEADER,
        captchaKey: String,
        goUrl: String? = null,
        keep: Boolean = true,
        headersCallback: suspend (Headers) -> Unit,
        resultCallback: suspend (LoginSuccessModel) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.post(URL_SMS_LOGIN) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("cid", cid)
                            append("tel", tel)
                            append("source", source)
                            append("code", code)
                            append("captcha_key", captchaKey)
                            goUrl?.apply {
                                append("go_url", goUrl)
                            }
                            append("keep", keep.toString())
                        }
                    )
                )
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            val res = Json.decodeFromString<BiliResponse<LoginSuccessModel?>>(bodyAsText())
            res.data?.let {
                headersCallback.invoke(headers)
                resultCallback(it)
            }
            res.message
        }
    }
}
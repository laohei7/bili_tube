package com.laohei.bili_sdk.apis.impl

import com.laohei.bili_sdk.apis.AuthApi
import com.laohei.bili_sdk.apis.URL_CAPTCHA
import com.laohei.bili_sdk.apis.URL_CHECK_SCAN_STATUS
import com.laohei.bili_sdk.apis.URL_REQUEST_QRCODE
import com.laohei.bili_sdk.apis.URL_SEND_SMS_CODE
import com.laohei.bili_sdk.apis.URL_SMS_LOGIN
import com.laohei.bili_sdk.exception.globalSDKExceptionHandle
import com.laohei.bili_sdk.model.BiliQRCode
import com.laohei.bili_sdk.model.BiliQRCodeStatus
import com.laohei.bili_sdk.module_v2.captcha.CaptchaModel
import com.laohei.bili_sdk.module_v2.captcha.GeetestModel
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

class AuthApiImpl(
    private val client: HttpClient
) : AuthApi {

    companion object {
        private val TAG = AuthApiImpl::class.simpleName
        private const val DBG = true
    }

    override suspend fun requestQRCode(): BiliResponse<BiliQRCode> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get(URL_REQUEST_QRCODE)
            Json.decodeFromString<BiliResponse<BiliQRCode>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse(
                    code = 400,
                    message = "ERROR",
                    data = BiliQRCode("", "")
                )
            }
        )
    }

    override suspend fun checkScanStatus(
        qrcodeKey: String,
        setCookieBlock: (suspend (Headers) -> Unit)?
    ): BiliQRCodeStatus = withContext(Dispatchers.IO) {
        runCatching {
            val status: BiliQRCodeStatus
            while (true) {
                delay(1500)
                val response = client.get(URL_CHECK_SCAN_STATUS) {
                    parameter("qrcode_key", qrcodeKey)
                }
                val currentStatus =
                    Json.decodeFromString<BiliResponse<BiliQRCodeStatus>>(response.bodyAsText())
                        .data
                when (currentStatus.code) {
                    86090, 86101 -> continue
                    0 -> {
                        setCookieBlock?.invoke(response.headers)
                        status = currentStatus
                        break
                    }

                    else -> {
                        status = currentStatus
                        break
                    }
                }
            }
            status
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliQRCodeStatus.networkError()
            }
        )
    }

    override suspend fun getCaptcha(source: String): BiliResponse<CaptchaModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = client.get(URL_CAPTCHA) {
                    parameter("source", source)
                }
                Json.decodeFromString<BiliResponse<CaptchaModel>>(response.bodyAsText())
            }.fold(
                onSuccess = { it },
                onFailure = {
                    if (DBG) {
                        globalSDKExceptionHandle(TAG.toString(), it)
                    }
                    BiliResponse(
                        code = 400,
                        message = "ERROR",
                        data = CaptchaModel("", "", GeetestModel("", ""))
                    )
                }
            )
        }

    override suspend fun sendSMSCode(
        cookie: String?,
        cid: String,
        tel: String,
        source: String,
        token: String,
        challenge: String,
        validate: String,
        seccode: String
    ): BiliResponse<SMSCodeModel?> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.post(URL_SEND_SMS_CODE) {
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
            Json.decodeFromString<BiliResponse<SMSCodeModel?>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse(
                    code = 400,
                    message = "ERROR",
                    data = SMSCodeModel("")
                )
            }
        )
    }

    override suspend fun smsLogin(
        cid: String,
        tel: String,
        code: String,
        source: String,
        captchaKey: String,
        goUrl: String?,
        keep: Boolean,
        headersCallback: suspend (Headers) -> Unit,
        resultCallback: suspend (LoginSuccessModel) -> Unit
    ): String = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.post(URL_SMS_LOGIN) {
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
            val res = Json.decodeFromString<BiliResponse<LoginSuccessModel?>>(response.bodyAsText())
            res.data?.let {
                headersCallback.invoke(response.headers)
                resultCallback(it)
            }
            res.message
        }.fold(
            onSuccess = { it },
            onFailure = {
                "ERROR"
            }
        )
    }
}
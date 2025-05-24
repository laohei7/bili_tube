package com.laohei.bili_sdk.apis

import com.laohei.bili_sdk.model.BiliQRCode
import com.laohei.bili_sdk.model.BiliQRCodeStatus
import com.laohei.bili_sdk.module_v2.captcha.CaptchaModel
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.login.LoginSuccessModel
import com.laohei.bili_sdk.module_v2.login.SMSCodeModel
import io.ktor.http.Headers

interface AuthApi {

    companion object {
        const val LOGIN_SOURCE_WEB = "main_web"
        const val LOGIN_SOURCE_MINI = "main_mini"
        const val LOGIN_SOURCE_HEADER = "main-fe-header"
    }

    suspend fun requestQRCode(): BiliResponse<BiliQRCode>

    suspend fun checkScanStatus(
        qrcodeKey: String,
        setCookieBlock: (suspend (Headers) -> Unit)? = null
    ): BiliQRCodeStatus

    suspend fun getCaptcha(
        source: String = LOGIN_SOURCE_HEADER,
    ): BiliResponse<CaptchaModel>

    suspend fun sendSMSCode(
        cookie: String? = null,
        cid: String,
        tel: String,
        source: String = LOGIN_SOURCE_HEADER,
        token: String,
        challenge: String,
        validate: String,
        seccode: String
    ): BiliResponse<SMSCodeModel?>

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
    ): String
}
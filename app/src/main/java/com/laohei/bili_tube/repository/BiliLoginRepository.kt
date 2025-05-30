package com.laohei.bili_tube.repository

import android.content.Context
import com.laohei.bili_sdk.apis.AuthApi
import com.laohei.bili_sdk.apis.InternationalizationApi
import com.laohei.bili_sdk.module_v2.login.LoginSuccessModel
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.dataStore
import io.ktor.http.Headers
import kotlinx.coroutines.flow.firstOrNull

class BiliLoginRepository(
    private val authApi: AuthApi,
    private val internationalizationApi: InternationalizationApi,
    private val context: Context
) {
    suspend fun getCountries() = internationalizationApi.getCountries()

    suspend fun getCaptcha(
        source: String = AuthApi.LOGIN_SOURCE_HEADER,
    ) = authApi.getCaptcha(source = source)

    suspend fun sendSMSCode(
        cid: String,
        tel: String,
        source: String = AuthApi.LOGIN_SOURCE_HEADER,
        token: String,
        challenge: String,
        validate: String,
        seccode: String
    ) = authApi.sendSMSCode(
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
        cid = cid,
        tel = tel,
        source = source,
        token = token,
        challenge = challenge,
        validate = validate,
        seccode = seccode,
    )

    suspend fun smsLogin(
        cid: String,
        tel: String,
        code: String,
        source: String = AuthApi.LOGIN_SOURCE_HEADER,
        captchaKey: String,
        goUrl: String? = null,
        keep: Boolean = true,
        headersCallback: suspend (Context, Headers) -> Unit,
        resultCallback: suspend (Context, LoginSuccessModel) -> Unit
    ): String? {
        val msg = authApi.smsLogin(
            cid = cid,
            tel = tel,
            code = code,
            source = source,
            captchaKey = captchaKey,
            goUrl = goUrl,
            keep = keep,
            headersCallback = { headers -> headersCallback(context, headers) },
            resultCallback = { res -> resultCallback(context, res) }
        )
        return msg.run {
            when {
                this == "0" -> context.getString(R.string.str_login_success)
                this == "ERROR" -> context.getString(R.string.str_login_failed)
                else -> this
            }
        }
    }

}
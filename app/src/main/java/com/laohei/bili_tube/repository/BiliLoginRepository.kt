package com.laohei.bili_tube.repository

import android.content.Context
import com.laohei.bili_sdk.login.Login
import com.laohei.bili_sdk.login.Login.Companion.LOGIN_SOURCE_HEADER
import com.laohei.bili_sdk.module_v2.login.LoginSuccessModel
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.dataStore
import io.ktor.http.Headers
import kotlinx.coroutines.flow.firstOrNull

class BiliLoginRepository(
    private val login: Login,
    private val context: Context
) {
    suspend fun getCaptcha(
        source: String = LOGIN_SOURCE_HEADER,
    ) = login.getCaptcha(
        source = source,
    )

    suspend fun sendSMSCode(
        cid: String,
        tel: String,
        source: String = LOGIN_SOURCE_HEADER,
        token: String,
        challenge: String,
        validate: String,
        seccode: String
    ) = login.sendSMSCode(
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
        source: String = LOGIN_SOURCE_HEADER,
        captchaKey: String,
        goUrl: String? = null,
        keep: Boolean = true,
        headersCallback: suspend (Context, Headers) -> Unit,
        resultCallback: suspend (Context, LoginSuccessModel) -> Unit
    ): String? {
        val msg = login.smsLogin(
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
            if (this == "0") {
                context.getString(R.string.str_login_success)
            } else {
                this
            }
        }
    }

}
package com.laohei.bili_tube.features.login

import com.laohei.bili_sdk.model.BiliQRCode
import com.laohei.bili_sdk.module_v2.captcha.CaptchaModel
import com.laohei.bili_sdk.module_v2.captcha.GeetestSuccessModel
import com.laohei.bili_sdk.module_v2.location.CountryItem
import com.laohei.bili_sdk.module_v2.login.SMSCodeModel
import com.laohei.bili_tube.core.ChainCountryId

data class LoginUIState(
    // Login by sms
    val phoneNumber: String = "",
    val isPhoneNumberError: Boolean = false,
    val code: String = "",
    val isCodeError: Boolean = false,
    val smsCode: SMSCodeModel? = null,

    // country id list
    val selectedCountryId: String = ChainCountryId,
    val countryItems: List<CountryItem> = emptyList(),

    // Login by qrcode
    val qrCode: BiliQRCode? = null,

    // geetest
    val captchaModel: CaptchaModel? = null,
    val geetestSuccessModel: GeetestSuccessModel? = null,
)

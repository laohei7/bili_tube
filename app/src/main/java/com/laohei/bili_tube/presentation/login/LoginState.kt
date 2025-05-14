package com.laohei.bili_tube.presentation.login

import com.laohei.bili_sdk.module_v2.captcha.CaptchaModel
import com.laohei.bili_sdk.module_v2.captcha.GeetestSuccessModel
import com.laohei.bili_sdk.module_v2.location.CountryItem
import com.laohei.bili_sdk.module_v2.login.SMSCodeModel

data class LoginState(
    val phoneNumber: String = "",
    val isPhoneNUmberError: Boolean = false,
    val code: String = "",
    val isCodeError: Boolean = false,
    val loginType: LoginType = LoginType.SMS,
    val selectedCountryItem: CountryItem = CountryItem.chain,
    val countryItems: List<CountryItem> = listOf(CountryItem.chain),
    val captchaModel: CaptchaModel? = null,
    val geetestSuccessModel: GeetestSuccessModel? = null,
    val smsCodeModel: SMSCodeModel? = null
)

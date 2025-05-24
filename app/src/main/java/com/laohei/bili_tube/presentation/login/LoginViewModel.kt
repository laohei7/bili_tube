package com.laohei.bili_tube.presentation.login

import androidx.compose.ui.util.fastJoinToString
import androidx.core.text.isDigitsOnly
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laohei.bili_sdk.module_v2.captcha.CaptchaModel
import com.laohei.bili_sdk.module_v2.captcha.GeetestSuccessModel
import com.laohei.bili_sdk.module_v2.location.CountryItem
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.core.IS_LOGIN_KEY
import com.laohei.bili_tube.core.REFRESH_TOKEN_KEY
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.dataStore
import com.laohei.bili_tube.repository.BiliLoginRepository
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class LoginViewModel(
    private val biliLoginRepository: BiliLoginRepository
) : ViewModel() {

    companion object {
        private val TAG = LoginViewModel::class.simpleName
        private const val DBG = true
    }

    private val _mState = MutableStateFlow(LoginState())
    val state = _mState.onStart {
        initCountryList()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _mState.value
    )

    private suspend fun initCountryList() {
        biliLoginRepository.getCountries().run {
            _mState.update {
                it.copy(countryItems = this.data.common + this.data.others)
            }
        }
    }

    fun switchLoginType(loginType: LoginType) {
        _mState.update { it.copy(loginType = loginType, captchaModel = null) }
    }

    fun changeCountry(countryItem: CountryItem) {
        _mState.update { it.copy(selectedCountryItem = countryItem) }
    }

    fun captcha(callback: (CaptchaModel) -> Unit) {
        viewModelScope.launch {
            biliLoginRepository.getCaptcha()?.let { res ->
                _mState.update { it.copy(captchaModel = res.data) }
                callback.invoke(res.data)
            }
        }
    }

    fun handleCaptchaResult(result: String) {
        val geetestSuccessModel = Json.decodeFromString<GeetestSuccessModel>(result)
        _mState.update { it.copy(geetestSuccessModel = geetestSuccessModel) }
    }

    fun onPhoneNumberChanged(value: String) {
        _mState.update { it.copy(phoneNumber = value) }
    }

    fun onCodeChanged(value: String) {
        _mState.update { it.copy(code = value) }
    }

    fun getSMSCode() {
        val phoneNumber = _mState.value.phoneNumber
        val validatedPhoneNumber = phoneNumber.isNotBlank() && phoneNumber.isDigitsOnly()
                && phoneNumber.length == 11
        if (validatedPhoneNumber) {
            _mState.update { it.copy(isPhoneNUmberError = false) }
        } else {
            _mState.update { it.copy(isPhoneNUmberError = true) }
            return
        }
        viewModelScope.launch {
            val currentState = _mState.value
            biliLoginRepository.sendSMSCode(
                cid = currentState.selectedCountryItem.countryId,
                tel = currentState.phoneNumber,
                token = currentState.captchaModel!!.token,
                challenge = currentState.geetestSuccessModel!!.geetestChallenge,
                validate = currentState.geetestSuccessModel.geetestValidate,
                seccode = currentState.geetestSuccessModel.geetestSeccode
            )?.let { res ->
                when {
                    res.code == 0 -> {
                        _mState.update { it.copy(smsCodeModel = res.data) }
                    }

                    else -> {
                        EventBus.send(Event.AppEvent.ToastEvent(res.message))
                    }
                }
            }
        }
    }

    fun smsLogin() {
        val code = _mState.value.code
        val validatedCode = code.isNotBlank() && code.isDigitsOnly()
        if (validatedCode) {
            _mState.update { it.copy(isCodeError = false) }
        } else {
            _mState.update { it.copy(isCodeError = true) }
            return
        }
        viewModelScope.launch {
            val currentState = _mState.value
            val msg = biliLoginRepository.smsLogin(
                cid = currentState.selectedCountryItem.countryId,
                tel = currentState.phoneNumber,
                code = currentState.code,
                captchaKey = currentState.smsCodeModel!!.captchaKey,
                headersCallback = { ctx, headers ->
                    val cookie = ctx.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
                    val cookieList = cookie?.split("; ")?.toMutableList() ?: mutableListOf()
                    headers.getAll(HttpHeaders.SetCookie)?.run {
                        cookieList.addAll(this)
                        cookieList.distinct()
                    }
                    ctx.dataStore.edit { settings ->
                        settings[COOKIE_KEY] = cookieList.fastJoinToString("; ")
                    }
                },
                resultCallback = { ctx, res ->
                    ctx.dataStore.edit { settings ->
                        settings[REFRESH_TOKEN_KEY] = res.refreshToken
                        settings[IS_LOGIN_KEY] = true
                    }
                }
            )
            msg?.run {
                EventBus.send(Event.AppEvent.ToastEvent(this))
            }
        }
    }


}
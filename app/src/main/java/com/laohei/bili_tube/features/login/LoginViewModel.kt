package com.laohei.bili_tube.features.login

import android.util.Log
import androidx.compose.ui.util.fastJoinToString
import androidx.core.text.isDigitsOnly
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laohei.bili_sdk.model_v2.captcha.CaptchaModel
import com.laohei.bili_sdk.model_v2.captcha.GeetestSuccessModel
import com.laohei.bili_sdk.model_v2.location.CountryItem
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.core.IS_LOGIN_KEY
import com.laohei.bili_tube.core.REFRESH_TOKEN_KEY
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.data.local.datastore.dataStore
import com.laohei.bili_tube.data.repository.BiliLoginRepository
import com.laohei.bili_tube.util.isValidChinesePhoneNumber
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Job
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

    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState = _uiState.onStart {
        initCountryList()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _uiState.value
    )
    private var _qrcodeLoginJob: Job? = null

    private suspend fun initCountryList() {
        biliLoginRepository.getCountries().run {
            _uiState.update {
                it.copy(countryItems = this.data.common + this.data.others)
            }
        }
    }

    fun onCountryChange(countryItem: CountryItem) {
        _uiState.update { it.copy(selectedCountryId = countryItem.countryId) }
    }

    fun captcha(callback: (CaptchaModel) -> Unit) {
        viewModelScope.launch {
            biliLoginRepository.getCaptcha().let { res ->
                _uiState.update { it.copy(captchaModel = res.data) }
                callback.invoke(res.data)
            }
        }
    }

    fun handleCaptchaResult(result: String) {
        val geetestSuccessModel = Json.decodeFromString<GeetestSuccessModel>(result)
        _uiState.update { it.copy(geetestSuccessModel = geetestSuccessModel) }
        getSMSCode()
    }

    fun onPhoneNumberChange(value: String) {
        _uiState.update { it.copy(phoneNumber = value) }
    }

    fun onCodeChange(value: String) {
        _uiState.update { it.copy(code = value) }
    }

    fun getSMSCode() {
        val phoneNumber = _uiState.value.phoneNumber
        val validatedPhoneNumber = phoneNumber.isValidChinesePhoneNumber()
        if (validatedPhoneNumber) {
            _uiState.update { it.copy(isPhoneNumberError = false) }
        } else {
            _uiState.update { it.copy(isPhoneNumberError = true) }
            return
        }
        viewModelScope.launch {
            val currentState = _uiState.value
            biliLoginRepository.sendSMSCode(
                cid = currentState.selectedCountryId,
                tel = currentState.phoneNumber,
                token = currentState.captchaModel!!.token,
                challenge = currentState.geetestSuccessModel!!.geetestChallenge,
                validate = currentState.geetestSuccessModel.geetestValidate,
                seccode = currentState.geetestSuccessModel.geetestSeccode
            ).let { res ->
                Log.d(TAG, "getSMSCode: $res")
                when {
                    res.code == 0 -> {
                        _uiState.update { it.copy(smsCode = res.data) }
                    }

                    else -> {
                        EventBus.send(Event.AppEvent.ToastTextEvent(res.message))
                    }
                }
            }
        }
    }

    fun onSmsLogin() {
        val code = _uiState.value.code
        val validatedCode = code.isNotBlank() && code.isDigitsOnly() && code.length == 6
        if (validatedCode) {
            _uiState.update { it.copy(isCodeError = false) }
        } else {
            _uiState.update { it.copy(isCodeError = true) }
            return
        }
        viewModelScope.launch {
            val currentState = _uiState.value
            val msg = biliLoginRepository.smsLogin(
                cid = currentState.selectedCountryId,
                tel = currentState.phoneNumber,
                code = currentState.code,
                captchaKey = currentState.smsCode!!.captchaKey,
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
                EventBus.send(Event.AppEvent.ToastTextEvent(this))
            }
        }
    }

    fun validatedPhoneNumber(): Boolean {
        val phone = uiState.value.phoneNumber
        val validated = phone.isValidChinesePhoneNumber()
        _uiState.update { it.copy(isPhoneNumberError = validated.not()) }
        return validated
    }

    fun requestQrcode() {
        _qrcodeLoginJob = viewModelScope.launch {
            val qrcode = biliLoginRepository.requestQrcode()
            _uiState.update { it.copy(qrCode = qrcode) }
            biliLoginRepository.checkScanStatus(
                qrcodeKey = qrcode.qrcodeKey,
                headersCallback = { ctx, headers ->
                    val cookie = headers.getAll(HttpHeaders.SetCookie)
                        ?.fastJoinToString("; ") ?: ""
                    ctx.dataStore.edit { settings ->
                        settings[COOKIE_KEY] = cookie
                    }
                },
                resultCallback = { ctx, status ->
                    ctx.dataStore.edit { settings ->
                        if (status.code == 0) {
                            settings[REFRESH_TOKEN_KEY] = status.refreshToken
                            settings[IS_LOGIN_KEY] = true
                        } else {
                            EventBus.send(Event.AppEvent.ToastTextEvent(status.message))
                        }
                    }
                }
            )
        }
    }

    fun onQrcodeLoginCancel() {
        _qrcodeLoginJob?.cancel()
        _uiState.update { it.copy(qrCode = null) }
    }


}
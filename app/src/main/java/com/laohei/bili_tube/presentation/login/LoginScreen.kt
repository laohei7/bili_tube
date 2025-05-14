package com.laohei.bili_tube.presentation.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastJoinToString
import androidx.compose.ui.viewinterop.AndroidView
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.geetest.sdk.GT3ConfigBean
import com.geetest.sdk.GT3ErrorBean
import com.geetest.sdk.GT3GeetestUtils
import com.geetest.sdk.GT3Listener
import com.geetest.sdk.views.GT3GeetestButton
import com.laohei.bili_sdk.login.Login
import com.laohei.bili_sdk.model.BiliQRCode
import com.laohei.bili_sdk.model.BiliQRCodeStatus
import com.laohei.bili_sdk.module_v2.location.CountryItem
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.core.IS_LOGIN_KEY
import com.laohei.bili_tube.core.REFRESH_TOKEN_KEY
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.core.painter.rememberQrBitmapPainter
import com.laohei.bili_tube.dataStore
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val codeInteractionSource = remember { MutableInteractionSource() }
    val codeFocusState by codeInteractionSource.collectIsFocusedAsState()
    var gt3ConfigBean by remember { mutableStateOf<GT3ConfigBean?>(null) }
    var gt3GeetestUtils by remember { mutableStateOf<GT3GeetestUtils?>(null) }

    val gT3ConfigBeanListener = remember {
        object : GT3Listener() {
            override fun onReceiveCaptchaCode(p0: Int) {

            }

            override fun onStatistics(p0: String?) {

            }

            override fun onClosed(p0: Int) {

            }

            override fun onSuccess(p0: String?) {

            }

            override fun onFailed(p0: GT3ErrorBean?) {

            }

            override fun onButtonClick() {
                viewModel.captcha {
                    gt3ConfigBean?.api1Json = JSONObject(
                        mapOf(
                            "gt" to it.geetest.gt,
                            "challenge" to it.geetest.challenge,
                            "success" to 1
                        )
                    )
                    gt3GeetestUtils?.getGeetest()
                }
            }

            override fun onDialogResult(result: String?) {
                super.onDialogResult(result)
                result?.run {
                    viewModel.handleCaptchaResult(this)
                    gt3GeetestUtils?.showSuccessDialog()
                }
            }
        }
    }

    LaunchedEffect(state.loginType) {
        if (state.loginType == LoginType.QRCODE) {
            return@LaunchedEffect
        }
        gt3ConfigBean = GT3ConfigBean().apply {
            listener = gT3ConfigBeanListener
        }
        gt3GeetestUtils = GT3GeetestUtils(context).apply {
            init(gt3ConfigBean)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LoginTopBar(loginType = state.loginType, hasCodeFocus = codeFocusState)

        AnimatedContent(
            targetState = state.loginType,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 160.dp),
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) { target ->
            when (target) {
                LoginType.SMS -> {
                    SMSLoginWidget(
                        phoneNumber = state.phoneNumber,
                        isPhoneNUmberError = state.isPhoneNUmberError,
                        code = state.code,
                        isCodeError = state.isCodeError,
                        isSendSMSCode = state.smsCodeModel != null,
                        selectedCountry = state.selectedCountryItem,
                        countryItems = state.countryItems,
                        couldGetCode = state.geetestSuccessModel != null,
                        gt3GeetestUtils = gt3GeetestUtils,
                        codeInteractionSource = codeInteractionSource,
                        switchLoginType = viewModel::switchLoginType,
                        onChangedCountry = viewModel::changeCountry,
                        onPhoneNumberChanged = viewModel::onPhoneNumberChanged,
                        onCodeChanged = viewModel::onCodeChanged,
                        getSMSCode = viewModel::getSMSCode,
                        onLogin = viewModel::smsLogin
                    )
                }

                LoginType.QRCODE -> {
                    QRCodeLoginWidget(
                        switchLoginType = viewModel::switchLoginType
                    )
                }

                LoginType.PASSWORD -> {
                    PasswordLoginWidget(
                        codeInteractionSource = codeInteractionSource,
                        switchLoginType = viewModel::switchLoginType
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoxScope.LoginTopBar(
    loginType: LoginType,
    hasCodeFocus: Boolean
) {
    Image(
        painter = when {
            hasCodeFocus -> painterResource(R.drawable.login_left_close)
            else -> painterResource(R.drawable.login_left)
        },
        contentDescription = "left",
        modifier = Modifier
            .align(Alignment.TopStart)
            .statusBarsPadding()
    )
    Image(
        painter = when {
            hasCodeFocus -> painterResource(R.drawable.login_right_close)
            else -> painterResource(R.drawable.login_right)
        },
        contentDescription = "right",
        modifier = Modifier
            .align(Alignment.TopEnd)
            .statusBarsPadding()
    )
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        ),
        title = {
            Text(
                text = when (loginType) {
                    LoginType.SMS -> stringResource(R.string.str_sms_login)
                    LoginType.QRCODE -> stringResource(R.string.str_scan_login)
                    LoginType.PASSWORD -> stringResource(R.string.str_password_login)
                }
            )
        },
    )
}

@Composable
private fun SMSLoginWidget(
    phoneNumber: String,
    isPhoneNUmberError: Boolean,
    code: String,
    isCodeError: Boolean,
    isSendSMSCode: Boolean,
    selectedCountry: CountryItem,
    countryItems: List<CountryItem>,
    couldGetCode: Boolean,
    gt3GeetestUtils: GT3GeetestUtils?,
    codeInteractionSource: MutableInteractionSource? = null,
    switchLoginType: (LoginType) -> Unit,
    onChangedCountry: (CountryItem) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    onCodeChanged: (String) -> Unit,
    getSMSCode: () -> Unit,
    onLogin: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    var countDownTimer by remember { mutableIntStateOf(60) }
    var enabledSendBtn by remember { mutableStateOf(true) }
    LaunchedEffect(isSendSMSCode) {
        if (isSendSMSCode.not()) {
            return@LaunchedEffect
        }
        enabledSendBtn = false
        countDownTimer = 60
        while (countDownTimer > 0) {
            delay(1000L)
            countDownTimer--
        }
        enabledSendBtn = true
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChanged,
            singleLine = true,
            isError = isPhoneNUmberError,
            label = {
                Text(text = stringResource(R.string.str_phone_number))
            },
            leadingIcon = {
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable { expanded = true }
                    ) {
                        Text(text = "+${selectedCountry.countryId}")
                        Icon(
                            imageVector = Icons.Outlined.ArrowDropDown,
                            contentDescription = Icons.Outlined.ArrowDropDown.name,
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        modifier = Modifier.heightIn(max = 400.dp),
                        onDismissRequest = { expanded = false }
                    ) {
                        countryItems.fastForEach {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(
                                            R.string.str_country_name,
                                            it.cname,
                                            it.countryId
                                        )
                                    )
                                },
                                onClick = {
                                    onChangedCountry(it)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = code,
            onValueChange = onCodeChanged,
            isError = isCodeError,
            singleLine = true,
            interactionSource = codeInteractionSource,
            label = {
                Text(text = stringResource(R.string.str_sms_code))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Numbers,
                    contentDescription = Icons.Outlined.Numbers.name,
                )
            },
            trailingIcon = {
                TextButton(
                    enabled = enabledSendBtn,
                    onClick = {
                        if (couldGetCode.not()) {
                            scope.launch {
                                EventBus.send(Event.AppEvent.ToastEvent(context.getString(R.string.str_captcha_hint)))
                            }
                        } else {
                            getSMSCode()
                        }
                    }) {
                    Text(
                        text = when {
                            enabledSendBtn -> stringResource(R.string.str_get_sms_code)
                            else -> stringResource(R.string.str_resend_sms_code, countDownTimer)
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        AndroidView(
            factory = {
                GT3GeetestButton(it)
            },
            update = {
                gt3GeetestUtils?.run {
                    it.setGeetestUtils(this)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
        Button(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp)
        ) {
            Text(text = stringResource(R.string.str_login))
        }
        TextButton(onClick = { switchLoginType(LoginType.PASSWORD) }) {
            Text(text = stringResource(R.string.str_password_login))
        }
        TextButton(onClick = { switchLoginType(LoginType.QRCODE) }) {
            Text(text = stringResource(R.string.str_scan_login))
        }
    }
}

@Composable
private fun PasswordLoginWidget(
    codeInteractionSource: MutableInteractionSource? = null,
    switchLoginType: (LoginType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            singleLine = true,
            label = {
                Text(text = stringResource(R.string.str_account))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = Icons.Outlined.AccountCircle.name,
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = "",
            onValueChange = {},
            singleLine = true,
            interactionSource = codeInteractionSource,
            label = {
                Text(text = stringResource(R.string.str_password))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Password,
                    contentDescription = Icons.Outlined.Password.name,
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        AndroidView(
            factory = {
                GT3GeetestButton(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp)
        ) {
            Text(text = stringResource(R.string.str_login))
        }
        TextButton(onClick = { switchLoginType(LoginType.SMS) }) {
            Text(text = stringResource(R.string.str_sms_login))
        }
        TextButton(onClick = { switchLoginType(LoginType.QRCODE) }) {
            Text(text = stringResource(R.string.str_scan_login))
        }
    }
}

@Composable
private fun QRCodeLoginWidget(
    switchLoginType: (LoginType) -> Unit
) {
    val context = LocalContext.current
    val qrcodeLogin = koinInject<Login>()
    var biliQRCode by remember { mutableStateOf<BiliQRCode?>(null) }
    var biliQRCodeStatus by remember { mutableStateOf<BiliQRCodeStatus?>(null) }

    LaunchedEffect(Unit) {
        qrcodeLogin.requestQRCode()?.apply {
            biliQRCode = this.data
        }
    }

    LaunchedEffect(biliQRCode) {
        biliQRCodeStatus = biliQRCode?.run {
            qrcodeLogin.checkScanStatus(qrcodeKey) { headers ->
                val cookie = headers.getAll(HttpHeaders.SetCookie)
                    ?.fastJoinToString("; ") ?: ""
                context.dataStore.edit { settings ->
                    settings[COOKIE_KEY] = cookie
                }
            }
        }
        biliQRCodeStatus?.run {
            context.dataStore.edit { settings ->
                settings[REFRESH_TOKEN_KEY] = refreshToken
                settings[IS_LOGIN_KEY] = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = biliQRCode == null,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
        ) { target ->
            if (target) {
                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Image(
                    painter = rememberQrBitmapPainter(biliQRCode!!.url),
                    contentDescription = biliQRCode!!.qrcodeKey
                )
            }
        }

        TextButton(onClick = { switchLoginType(LoginType.PASSWORD) }) {
            Text(text = stringResource(R.string.str_password_login))
        }
        TextButton(onClick = { switchLoginType(LoginType.SMS) }) {
            Text(text = stringResource(R.string.str_sms_login))
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    LoginScreen()
}
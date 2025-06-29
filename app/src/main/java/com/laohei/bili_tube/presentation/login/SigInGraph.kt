package com.laohei.bili_tube.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.geetest.sdk.GT3ConfigBean
import com.geetest.sdk.GT3ErrorBean
import com.geetest.sdk.GT3GeetestUtils
import com.geetest.sdk.GT3Listener
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.utill.validatedPhoneNumber
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignInGraph(
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val signInNavController = rememberNavController()
    val currentDestination by signInNavController.currentBackStackEntryAsState()
    val codeInteractionSource = remember { MutableInteractionSource() }
    val codeFocusState by codeInteractionSource.collectIsFocusedAsState()
    var gt3ConfigBean by remember { mutableStateOf<GT3ConfigBean?>(null) }
    var gt3GeetestUtils by remember { mutableStateOf<GT3GeetestUtils?>(null) }
    val destination = currentDestination?.destination

    val titleId by remember {
        derivedStateOf {
            when {
                destination?.hasRoute<Route.Login.SMS>() == true -> R.string.str_sms_login
                destination?.hasRoute<Route.Login.Password>() == true -> R.string.str_password_login
                destination?.hasRoute<Route.Login.QRCode>() == true -> R.string.str_scan_login
                else -> R.string.str_sms_login
            }
        }
    }

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

    LaunchedEffect(currentDestination) {
        when {
            destination?.hasRoute<Route.Login.SMS>() == true ||
                    destination?.hasRoute<Route.Login.Password>() == true -> {
                gt3ConfigBean = GT3ConfigBean().apply {
                    listener = gT3ConfigBeanListener
                }
                gt3GeetestUtils = GT3GeetestUtils(context).apply {
                    init(gt3ConfigBean)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LoginTopBar(
            title = stringResource(titleId),
            hasCodeFocus = codeFocusState,
            upPress = when {
                destination?.hasRoute<Route.Login.SMS>() == true -> null
                else -> {
                    { signInNavController.navigateUp() }
                }
            }
        )

        NavHost(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 160.dp),
            navController = signInNavController,
            startDestination = Route.Login.SMS
        ) {
            composable<Route.Login.SMS> {
                SMSLoginScreen(
                    phoneNumber = state.phoneNumber,
                    isPhoneNUmberError = state.isPhoneNUmberError,
                    code = state.code,
                    isCodeError = state.isCodeError,
                    isSendSMSCode = state.smsCodeModel != null,
                    selectedCountry = state.selectedCountryItem,
                    countryItems = state.countryItems,
                    codeInteractionSource = codeInteractionSource,
                    switchLoginType = {
                        signInNavController.navigate(it)
                    },
                    onChangedCountry = viewModel::changeCountry,
                    onPhoneNumberChanged = viewModel::onPhoneNumberChanged,
                    onCodeChanged = viewModel::onCodeChanged,
                    getSMSCode = {
                        val isValidatedNumber = state.phoneNumber.validatedPhoneNumber()
                        if (isValidatedNumber.not()) {
                            scope.launch {
                                EventBus.send(Event.AppEvent.ToastEvent(context.getString(R.string.str_phone_number_error)))
                            }
                            return@SMSLoginScreen
                        }
                        gt3GeetestUtils?.startCustomFlow()
                    },
                    onLogin = viewModel::smsLogin
                )
            }
            composable<Route.Login.Password> {
                PasswordLoginScreen(
                    codeInteractionSource = codeInteractionSource,
                )
            }
            composable<Route.Login.QRCode> {
                QRCodeLoginScreen()
            }
        }
    }
}
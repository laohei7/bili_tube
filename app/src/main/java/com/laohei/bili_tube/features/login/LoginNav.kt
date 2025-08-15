package com.laohei.bili_tube.features.login

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.laohei.bili_tube.features.login.nav.LoginRoute
import org.json.JSONObject
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginNav(
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val signInNavController = rememberNavController()
    val currentDestination by signInNavController.currentBackStackEntryAsState()
    val codeInteractionSource = remember { MutableInteractionSource() }
    var gt3ConfigBean by remember { mutableStateOf<GT3ConfigBean?>(null) }
    var gt3GeetestUtils by remember { mutableStateOf<GT3GeetestUtils?>(null) }
    val destination = currentDestination?.destination

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
            destination?.hasRoute<LoginRoute.SMS>() == true ||
                    destination?.hasRoute<LoginRoute.Password>() == true -> {
                gt3ConfigBean = GT3ConfigBean().apply {
                    listener = gT3ConfigBeanListener
                }
                gt3GeetestUtils = GT3GeetestUtils(context).apply {
                    init(gt3ConfigBean)
                }
            }
        }
    }

    fun onSendCode() {
        if (viewModel.validatedPhoneNumber()) {
            gt3GeetestUtils?.startCustomFlow()
        }
    }

    NavHost(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        navController = signInNavController,
        startDestination = LoginRoute.SMS
    ) {
        composable<LoginRoute.SMS> {
            SmsLoginScreen(
                uiState = uiState,
                onPhoneChange = viewModel::onPhoneNumberChange,
                onCountryChange = viewModel::onCountryChange,
                onCodeChange = viewModel::onCodeChange,
                onCodeSend = {
                    onSendCode()
                },
                onLogin = viewModel::onSmsLogin,
                navigateToLoginRoute = {
                    signInNavController.navigate(it)
                }
            )
        }
        composable<LoginRoute.Password> {
            PasswordLoginScreen(
                codeInteractionSource = codeInteractionSource,
            )
        }
        composable<LoginRoute.QRCode> {
            QrcodeLoginScreen(
                uiState = uiState,
                requestQrcode = viewModel::requestQrcode,
                onCancel = viewModel::onQrcodeLoginCancel,
                upPress = {
                    signInNavController.navigateUp()
                }
            )
        }
    }
}
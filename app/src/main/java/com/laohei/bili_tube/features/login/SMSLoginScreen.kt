package com.laohei.bili_tube.features.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.laohei.bili_sdk.module_v2.location.CountryItem
import com.laohei.bili_tube.R
import com.laohei.bili_tube.features.login.component.CodeInput
import com.laohei.bili_tube.features.login.component.LoginBtn
import com.laohei.bili_tube.features.login.component.LoginMethod
import com.laohei.bili_tube.features.login.component.LoginTitle
import com.laohei.bili_tube.features.login.component.PhoneInput
import com.laohei.bili_tube.features.login.component.SmsBackground
import com.laohei.bili_tube.features.login.nav.LoginRoute
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.foundation.DeviceConfiguration
import com.laohei.bili_tube.ui.theme.LargePadding
import com.laohei.bili_tube.utill.underDevelopment

@Composable
fun SmsLoginScreen(
    uiState: LoginUIState,
    onPhoneChange: (String) -> Unit,
    onCountryChange: (CountryItem) -> Unit,
    onCodeChange: (String) -> Unit,
    onCodeSend: () -> Unit,
    onLogin: () -> Unit,
    navigateToLoginRoute: (LoginRoute) -> Unit
) {
    AdaptiveLayout { type, _, _ ->
        when (type) {
            DeviceConfiguration.MOBILE_PORTRAIT,
            DeviceConfiguration.TABLE_PORTRAIT -> {
                PortraitContent(
                    uiState = uiState,
                    onPhoneChange = onPhoneChange,
                    onCountryChange = onCountryChange,
                    onCodeChange = onCodeChange,
                    onCodeSend = onCodeSend,
                    onLogin = onLogin,
                    navigateToLoginRoute = navigateToLoginRoute
                )
            }

            DeviceConfiguration.MOBILE_LANDSCAPE,
            DeviceConfiguration.TABLE_LANDSCAPE,
            DeviceConfiguration.DESKTOP -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LandscapeContent(
                        uiState = uiState,
                        onPhoneChange = onPhoneChange,
                        onCountryChange = onCountryChange,
                        onCodeChange = onCodeChange,
                        onCodeSend = onCodeSend,
                        onLogin = onLogin,
                        navigateToLoginRoute = navigateToLoginRoute
                    )
                }
            }
        }
    }

}

@Composable
private fun PortraitContent(
    uiState: LoginUIState,
    onPhoneChange: (String) -> Unit,
    onCountryChange: (CountryItem) -> Unit,
    onCodeChange: (String) -> Unit,
    onCodeSend: () -> Unit,
    onLogin: () -> Unit,
    navigateToLoginRoute: (LoginRoute) -> Unit
) {
    val scope = rememberCoroutineScope()
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize(),
        constraintSet = portraitDecoupledConstraints()
    ) {
        SmsBackground(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(1 / 3f)
        )
        LoginTitle(stringResource(R.string.str_sms_login))
        PhoneInput(
            phone = uiState.phoneNumber,
            isPhoneError = uiState.isPhoneNumberError,
            selectedCountryId = uiState.selectedCountryId,
            countries = uiState.countryItems,
            onPhoneChange = onPhoneChange,
            onCountryChange = onCountryChange
        )
        CodeInput(
            code = uiState.code,
            isCodeError = uiState.isCodeError,
            isCodeSend = uiState.smsCode != null,
            onCodeChange = onCodeChange,
            onCodeSend = onCodeSend
        )
        LoginBtn(onLogin = onLogin)
        LoginMethod {
            IconButton(
                onClick = {
                    navigateToLoginRoute(LoginRoute.QRCode)
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.QrCode,
                    contentDescription = Icons.Rounded.QrCode.name
                )
            }
            IconButton(
                onClick = {
                    underDevelopment(scope)
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = Icons.Rounded.AccountCircle.name
                )
            }
        }
    }
}

@Composable
private fun LandscapeContent(
    uiState: LoginUIState,
    onPhoneChange: (String) -> Unit,
    onCountryChange: (CountryItem) -> Unit,
    onCodeChange: (String) -> Unit,
    onCodeSend: () -> Unit,
    onLogin: () -> Unit,
    navigateToLoginRoute: (LoginRoute) -> Unit
) {
    val scope = rememberCoroutineScope()
    ConstraintLayout(
        modifier = Modifier
            .sizeIn(maxWidth = 800.dp)
            .fillMaxHeight(0.7f),
        constraintSet = landscapeDecoupledConstraints()
    ) {
        SmsBackground(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(1 / 2f)
        )
        LoginTitle(stringResource(R.string.str_sms_login))
        PhoneInput(
            modifier = Modifier.sizeIn(maxWidth = 500.dp),
            phone = uiState.phoneNumber,
            isPhoneError = uiState.isPhoneNumberError,
            selectedCountryId = uiState.selectedCountryId,
            countries = uiState.countryItems,
            onPhoneChange = onPhoneChange,
            onCountryChange = onCountryChange
        )
        CodeInput(
            modifier = Modifier.sizeIn(maxWidth = 500.dp),
            code = uiState.code,
            isCodeError = uiState.isCodeError,
            isCodeSend = uiState.smsCode != null,
            onCodeChange = onCodeChange,
            onCodeSend = onCodeSend
        )
        LoginBtn(onLogin = onLogin)
        LoginMethod {
            IconButton(
                onClick = {
                    navigateToLoginRoute(LoginRoute.QRCode)
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.QrCode,
                    contentDescription = Icons.Rounded.QrCode.name
                )
            }
            IconButton(
                onClick = {
                    underDevelopment(scope)
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = Icons.Rounded.AccountCircle.name
                )
            }
        }
    }
}

private fun portraitDecoupledConstraints(): ConstraintSet {
    return ConstraintSet {
        val loginBtn = createRefFor("login_btn")
        val background = createRefFor("background")
        val title = createRefFor("title")
        val phoneInput = createRefFor("phone_input")
        val codeInput = createRefFor("code_input")
        val loginMethod = createRefFor("login_method")

        constrain(background) {
            top.linkTo(parent.top, 0.dp)
        }
        constrain(title) {
            bottom.linkTo(background.bottom, 164.5.dp)
            start.linkTo(parent.start, LargePadding * 3)
        }
        constrain(codeInput) {
            top.linkTo(background.bottom)
        }
        constrain(phoneInput) {
            bottom.linkTo(codeInput.top, LargePadding * 2)
        }
        constrain(loginBtn) {
            bottom.linkTo(parent.bottom, 120.dp)
            end.linkTo(parent.end, LargePadding * 2)
        }
        constrain(loginMethod) {
            start.linkTo(parent.start, LargePadding)
            top.linkTo(loginBtn.top)
            bottom.linkTo(loginBtn.bottom)
        }
    }
}

private fun landscapeDecoupledConstraints(): ConstraintSet {
    return ConstraintSet {
        val loginBtn = createRefFor("login_btn")
        val background = createRefFor("background")
        val title = createRefFor("title")
        val phoneInput = createRefFor("phone_input")
        val codeInput = createRefFor("code_input")
        val loginMethod = createRefFor("login_method")

        constrain(background) {
            top.linkTo(parent.top, 0.dp)
        }
        constrain(title) {
            top.linkTo(background.top)
            bottom.linkTo(background.bottom)
            start.linkTo(parent.start, LargePadding * 3)
        }
        constrain(codeInput) {
            top.linkTo(background.bottom)
            start.linkTo(title.end)
            end.linkTo(background.end)
        }
        constrain(phoneInput) {
            bottom.linkTo(codeInput.top, LargePadding * 2)
            start.linkTo(title.end)
            end.linkTo(background.end)
        }
        constrain(loginBtn) {
            top.linkTo(codeInput.bottom, LargePadding * 3)
            end.linkTo(codeInput.end)
            start.linkTo(codeInput.start)
        }
        constrain(loginMethod) {
            start.linkTo(parent.start, LargePadding * 3)
            top.linkTo(background.bottom)
            bottom.linkTo(parent.bottom)
        }
    }
}

@Preview(device = Devices.TABLET, showSystemUi = true, showBackground = true)
@Preview(device = Devices.DESKTOP, showSystemUi = true, showBackground = true)
@Composable
private fun LandscapeContentPreview() {
    var uiState by remember { mutableStateOf(LoginUIState()) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LandscapeContent(
            uiState = uiState,
            onPhoneChange = {},
            onCountryChange = { },
            onCodeChange = {},
            onCodeSend = {},
            onLogin = {},
            navigateToLoginRoute = {}
        )
    }

}
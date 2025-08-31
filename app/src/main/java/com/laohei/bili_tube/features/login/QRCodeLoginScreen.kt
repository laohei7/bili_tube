package com.laohei.bili_tube.features.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices.DESKTOP
import androidx.compose.ui.tooling.preview.Devices.PIXEL_7_PRO
import androidx.compose.ui.tooling.preview.Devices.TABLET
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.laohei.bili_tube.R
import com.laohei.bili_tube.features.login.component.BackBtn
import com.laohei.bili_tube.features.login.component.LoginTitle
import com.laohei.bili_tube.features.login.component.Qrcode
import com.laohei.bili_tube.features.login.component.SmsBackground
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.foundation.DeviceConfiguration
import com.laohei.bili_tube.ui.theme.PaddingLg

@Composable
fun QrcodeLoginScreen(
    uiState: LoginUIState,
    requestQrcode: () -> Unit,
    onCancel: () -> Unit,
    upPress: () -> Unit
) {
    DisposableEffect(Unit) {
        requestQrcode()

        onDispose {
            onCancel()
        }
    }

    AdaptiveLayout { type, _, _ ->
        when (type) {
            DeviceConfiguration.MOBILE_PORTRAIT,
            DeviceConfiguration.TABLE_PORTRAIT -> {
                PortraitContent(
                    uiState = uiState,
                    upPress = upPress
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
                        upPress = upPress
                    )
                }
            }
        }
    }
}

@Composable
private fun PortraitContent(
    uiState: LoginUIState,
    upPress: () -> Unit
) {
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
        LoginTitle(stringResource(R.string.str_scan_login))
        BackBtn(onClick = upPress)
        Qrcode(isLoading = uiState.qrCode == null, content = uiState.qrCode?.url ?: "")
    }
}

@Composable
private fun LandscapeContent(
    uiState: LoginUIState,
    upPress: () -> Unit
) {
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
        LoginTitle(stringResource(R.string.str_scan_login))
        BackBtn(onClick = upPress)
        Qrcode(isLoading = uiState.qrCode == null, content = uiState.qrCode?.url ?: "")
    }
}


private fun portraitDecoupledConstraints(): ConstraintSet {
    return ConstraintSet {
        val background = createRefFor("background")
        val title = createRefFor("title")
        val qrcode = createRefFor("qrcode")
        val backBtn = createRefFor("back_btn")

        constrain(background) {
            top.linkTo(parent.top, 0.dp)
        }
        constrain(title) {
            bottom.linkTo(background.bottom, 164.5.dp)
            start.linkTo(parent.start, PaddingLg * 3)
        }
        constrain(backBtn) {
            start.linkTo(parent.start, PaddingLg)
            bottom.linkTo(title.top)
        }
        constrain(qrcode) {
            bottom.linkTo(background.bottom)
            top.linkTo(background.bottom)
            start.linkTo(background.start)
            end.linkTo(background.end)
        }

    }
}

private fun landscapeDecoupledConstraints(): ConstraintSet {
    return ConstraintSet {
        val background = createRefFor("background")
        val title = createRefFor("title")
        val qrcode = createRefFor("qrcode")
        val backBtn = createRefFor("back_btn")

        constrain(background) {
            top.linkTo(parent.top, 0.dp)
        }
        constrain(title) {
            top.linkTo(background.top)
            bottom.linkTo(background.bottom)
            start.linkTo(parent.start, PaddingLg * 3)
        }
        constrain(backBtn) {
            start.linkTo(parent.start, PaddingLg)
            top.linkTo(background.top, PaddingLg)
        }
        constrain(qrcode) {
            bottom.linkTo(background.bottom)
            top.linkTo(background.bottom)
            start.linkTo(background.start)
            end.linkTo(background.end)
        }

    }
}


@Preview(showBackground = true, showSystemUi = true, device = TABLET)
@Preview(showBackground = true, showSystemUi = true, device = DESKTOP)
@Preview(showBackground = true, showSystemUi = true, device = PIXEL_7_PRO)
@Composable
private fun QrcodeLoginScreenPreview() {
    var uiState by remember { mutableStateOf(LoginUIState()) }
    QrcodeLoginScreen(
        uiState = uiState,
        requestQrcode = {},
        upPress = {},
        onCancel = {}
    )
}
package com.laohei.bili_tube.presentation.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastJoinToString
import androidx.datastore.preferences.core.edit
import com.laohei.bili_sdk.apis.AuthApi
import com.laohei.bili_sdk.model.BiliQRCode
import com.laohei.bili_sdk.model.BiliQRCodeStatus
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.core.IS_LOGIN_KEY
import com.laohei.bili_tube.core.REFRESH_TOKEN_KEY
import com.laohei.bili_tube.core.painter.rememberQrBitmapPainter
import com.laohei.bili_tube.dataStore
import io.ktor.http.HttpHeaders
import org.koin.compose.koinInject


@Composable
internal fun QRCodeLoginScreen() {
    val context = LocalContext.current
    val authApi = koinInject<AuthApi>()
    var biliQRCode by remember { mutableStateOf<BiliQRCode?>(null) }
    var biliQRCodeStatus by remember { mutableStateOf<BiliQRCodeStatus?>(null) }

    LaunchedEffect(Unit) {
        authApi.requestQRCode().apply {
            biliQRCode = this.data
        }
    }

    LaunchedEffect(biliQRCode) {
        biliQRCodeStatus = biliQRCode?.run {
            authApi.checkScanStatus(qrcodeKey) { headers ->
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
    }
}
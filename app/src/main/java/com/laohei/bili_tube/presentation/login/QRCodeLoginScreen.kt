package com.laohei.bili_tube.presentation.login

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastJoinToString
import androidx.datastore.preferences.core.edit
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.laohei.bili_sdk.login.QRLogin
import com.laohei.bili_sdk.model.BiliQRCode
import com.laohei.bili_sdk.model.BiliQRCodeStatus
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.dataStore
import com.laohei.bili_tube.utill.setValue
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import android.graphics.Color as original

private const val TAG = "QRCodeLoginScreen"

@Composable
fun QRCodeLoginScreen() {
    val context = LocalContext.current
    val qrcodeLogin = koinInject<QRLogin>()
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
                Log.d(TAG, "QRCodeLoginScreen: $cookie")
                context.dataStore.edit { settings ->
                    settings[COOKIE_KEY] = cookie
                }
                context.setValue(COOKIE_KEY.name, cookie)
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (maxWidth < 800.dp) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                Text(
                    text = "扫描登录",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                biliQRCode?.run {
                    Image(
                        painter = rememberQrBitmapPainter(this.url),
                        contentDescription = this.qrcodeKey
                    )
                } ?: run {
                    CircularProgressIndicator()
                }
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(80.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "扫描登录",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                biliQRCode?.run {
                    Image(
                        painter = rememberQrBitmapPainter(this.url),
                        contentDescription = this.qrcodeKey
                    )
                } ?: run {
                    CircularProgressIndicator()
                }
            }
        }

    }

}

@Composable
fun rememberQrBitmapPainter(
    content: String,
    size: Dp = 200.dp,
    padding: Dp = 0.dp
): BitmapPainter {

    val density = LocalDensity.current
    val sizePx = with(density) { size.roundToPx() }
    val paddingPx = with(density) { padding.roundToPx() }

    var bitmap by remember(content) {
        mutableStateOf<Bitmap?>(null)
    }

    LaunchedEffect(bitmap) {
        if (bitmap != null) return@LaunchedEffect

        launch(Dispatchers.IO) {
            val qrCodeWriter = QRCodeWriter()

            val encodeHints = mutableMapOf<EncodeHintType, Any?>()
                .apply {
                    this[EncodeHintType.MARGIN] = paddingPx
                }

            val bitmapMatrix = try {
                qrCodeWriter.encode(
                    content, BarcodeFormat.QR_CODE,
                    sizePx, sizePx, encodeHints
                )
            } catch (ex: WriterException) {
                null
            }
            val matrixWidth = bitmapMatrix?.width ?: sizePx
            val matrixHeight = bitmapMatrix?.height ?: sizePx

            val newBitmap = Bitmap.createBitmap(
                bitmapMatrix?.width ?: sizePx,
                bitmapMatrix?.height ?: sizePx,
                Bitmap.Config.ARGB_8888,
            )

            for (x in 0 until matrixWidth) {
                for (y in 0 until matrixHeight) {
                    val shouldColorPixel = bitmapMatrix?.get(x, y) ?: false
                    val pixelColor = if (shouldColorPixel) original.BLACK else original.WHITE

                    newBitmap.setPixel(x, y, pixelColor)
                }
            }

            bitmap = newBitmap
        }
    }

    return remember(bitmap) {
        val currentBitmap = bitmap ?: Bitmap.createBitmap(
            sizePx, sizePx,
            Bitmap.Config.ARGB_8888,
        ).apply { eraseColor(original.TRANSPARENT) }

        BitmapPainter(currentBitmap.asImageBitmap())
    }
}


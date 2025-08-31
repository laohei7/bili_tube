package com.laohei.bili_tube.features.login.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.ui.component.painter.rememberQrBitmapPainter
import com.laohei.bili_tube.ui.theme.PaddingSm

@Composable
internal fun Qrcode(
    isLoading: Boolean,
    content: String
) {
    AnimatedContent(
        modifier = Modifier.layoutId("qrcode"),
        targetState = isLoading,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
    ) { target ->
        val shape = RoundedCornerShape(PaddingSm)
        val contentModifier = Modifier
            .size(200.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
        if (target) {
            Box(
                modifier = contentModifier,
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Image(
                modifier = contentModifier,
                painter = rememberQrBitmapPainter(content),
                contentDescription = "qrcode_content"
            )
        }
    }
}
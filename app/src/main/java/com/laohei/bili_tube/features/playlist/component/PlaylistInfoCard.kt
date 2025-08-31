package com.laohei.bili_tube.features.playlist.component

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.palette.graphics.Palette
import coil3.asDrawable
import coil3.compose.AsyncImage
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.USERNAME_KEY
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.core.extension.getValue
import com.laohei.bili_tube.ui.util.toNonHardwareBitmap
import kotlinx.coroutines.launch


@Composable
internal fun PlaylistInfoCard(
    isDark: Boolean = isSystemInDarkTheme(),
    param: AppRoute.PlaylistContent
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var dominantColor by remember { mutableStateOf(Color.LightGray) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .background(dominantColor)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .blur(20.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 64.dp)
                .align(Alignment.Center)
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.background.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .sizeIn(maxWidth = 600.dp)
                .fillMaxHeight()
                .padding(16.dp),
        ) {
            AsyncImage(
                model = param.cover,
                contentDescription = "cover",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                onSuccess = {
                    val drawable = it.result.image.asDrawable(context.resources)
                    scope.launch {
                        drawable.toBitmapOrNull()?.toNonHardwareBitmap()?.let { bitmap ->
                            Palette.from(bitmap).generate { palette ->
                                dominantColor = if (isDark) {
                                    palette?.getLightMutedColor(Color.LightGray.toArgb())?.run {
                                        Color(this)
                                    } ?: Color.LightGray
                                } else {
                                    palette?.getDominantColor(Color.LightGray.toArgb())?.run {
                                        Color(this)
                                    } ?: Color.LightGray
                                }
                            }
                        }
                    }
                },
                placeholder = painterResource(R.drawable.icon_loading_16_9),
                error = painterResource(R.drawable.icon_loading_16_9),
                contentScale = ContentScale.Crop
            )
            Text(
                text = param.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = context.getValue(USERNAME_KEY.name, stringResource(R.string.str_unknown)),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Row {
                Text(
                    text = stringResource(R.string.str_video_count, param.count),
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = Icons.Outlined.Lock.name,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = when {
                        param.isPrivate -> stringResource(R.string.str_private)
                        else -> stringResource(R.string.str_public)
                    },
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
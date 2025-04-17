package com.laohei.bili_tube.presentation.home.anime.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_tube.R


@Composable
internal fun BangumiWidget(
    modifier: Modifier = Modifier,
    cover: String,
    title: String,
    label: String,
) {
    val context = LocalContext.current
    val coverRequest = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(cover)
            .crossfade(false)
            .placeholder(R.drawable.icon_loading)
            .error(R.drawable.icon_loading)
            .build()
    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = coverRequest,
            contentDescription = "",
            modifier = Modifier
                .aspectRatio(3 / 4f)
                .clip(RoundedCornerShape(8.dp))
        )
        Text(
            text = title,
            maxLines = 2,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall,
            color = Color.LightGray
        )
    }
}

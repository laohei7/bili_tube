package com.laohei.common_ui.info

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder

@Composable
fun DRAWItemAuthorInfoBar(
    modifier: Modifier = Modifier,
    face: String,
    ownerName: String,
    pubDate: String,
    @DrawableRes placeholder: Int,
    @DrawableRes error: Int,
    trailingIcon: ImageVector,
    trailingOnClick: () -> Unit
) {
    val context = LocalContext.current
    val faceRequest = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(face)
            .crossfade(false)
            .placeholder(placeholder)
            .error(error)
            .build()
    )
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = faceRequest,
            contentDescription = ownerName,
            modifier = Modifier
                .padding(top = 4.dp, end = 18.dp)
                .size(42.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = ownerName,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = pubDate,
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

        }

        Surface(
            shape = CircleShape,
            color = Color.Transparent,
            onClick = { trailingOnClick.invoke() }
        ) {
            Icon(
                imageVector = trailingIcon,
                contentDescription = trailingIcon.name,
                modifier = Modifier.padding(4.dp),
                tint = Color.White
            )
        }
    }
}
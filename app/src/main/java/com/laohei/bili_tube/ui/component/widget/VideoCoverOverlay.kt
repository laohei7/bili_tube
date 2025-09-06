package com.laohei.bili_tube.ui.component.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.theme.CornerRadiusXs
import com.laohei.bili_tube.ui.theme.PaddingSm
import com.laohei.bili_tube.ui.theme.PaddingXs

@Composable
fun VideoCoverOverlay(
    modifier: Modifier = Modifier,
    coverUrl: String,
    contentDescription: String?,
    duration: String,
    coverAspectRatio: Float = 16f / 9f,
    watchProgress: Float? = null,
    coverShape: Shape = RoundedCornerShape(0.dp),
    placeholderRes: Int = R.drawable.icon_loading_375_211,
    errorRes: Int = R.drawable.icon_loading_375_211,
) {
    Layout(
        modifier = modifier,
        content = {
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(coverUrl)
                    .crossfade(true)
                    .placeholder(placeholderRes)
                    .error(errorRes)
                    .build()
            )

            Image(
                painter = painter,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.clip(coverShape)
            )

            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                contentColor = Color.White,
                shape = RoundedCornerShape(CornerRadiusXs)
            ) {
                Text(
                    text = duration,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(PaddingXs)
                )
            }

            watchProgress?.let {
                LinearProgressIndicator(
                    progress = { watchProgress },
                    trackColor = Color.White.copy(alpha = 0.2f),
                    color = Color.Red
                )
            }
        }
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val height = (width / coverAspectRatio).toInt()

        val imagePlaceable = measurables[0].measure(
            Constraints.fixed(width, height)
        )
        val durationPlaceable = measurables[1].measure(Constraints())
        val progressPlaceable = measurables.getOrNull(2)?.measure(Constraints())

        layout(width, height) {
            imagePlaceable.place(0, 0)

            progressPlaceable?.place(
                0, height - progressPlaceable.height
            )

            durationPlaceable.place(
                x = width - durationPlaceable.width - PaddingSm.roundToPx(),
                y = height - durationPlaceable.height - PaddingSm.roundToPx()
            )
        }
    }
}

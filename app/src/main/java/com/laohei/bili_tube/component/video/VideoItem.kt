package com.laohei.bili_tube.component.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.laohei.bili_tube.R


@Composable
fun VideoItem(
    isSingleLayout: Boolean = false,
    key: String,
    cover: String,
    title: String,
    face: String,
    ownerName: String,
    view: String,
    date: String,
    duration: String,
    onClick: () -> Unit,
    onMenuClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) { onClick.invoke() },
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Box {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(cover)
                    .crossfade(true)
                    .build(),
                contentDescription = key,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(if (isSingleLayout) 0.dp else 12.dp)),
                contentScale = ContentScale.Crop,
                loading = {
                    Image(
                        painter = painterResource(R.drawable.icon_loading),
                        contentDescription = "loading img",
                    )
                }
            )

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 8.dp, end = 8.dp),
                color = Color.Black.copy(alpha = 0.5f),
                contentColor = Color.White,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = duration, style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(3.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(start = 12.dp, end = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(face)
                    .crossfade(true)
                    .build(),
                contentDescription = ownerName,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(42.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .background(Color.LightGray)
                            .clip(CircleShape)
                    )
                }
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = buildAnnotatedString {
                        append(ownerName)
                        if (view.isNotBlank()) {
                            append(" · ")
                            append(view)
                            append("次观看")
                        }
                        if (date.isNotBlank()) {
                            append(" · ")
                            append(date)
                        }
                    },
                    maxLines = 1,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.background,
                onClick = {onMenuClick?.invoke()}
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = Icons.Default.MoreVert.name,
                    modifier = Modifier.padding(4.dp)
                )
            }

        }
    }
}
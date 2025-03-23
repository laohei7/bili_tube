package com.laohei.bili_tube.presentation.player.component.archive

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laohei.bili_sdk.module_v2.video.ArchiveMeta
import com.laohei.bili_tube.component.lottie.LottieIconPlaying


@Composable
internal fun ArchiveMetaItem(
    archiveMeta: ArchiveMeta,
    currentArchiveIndex: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(top = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick.invoke() }
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = archiveMeta.name,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            LottieIconPlaying(
                Modifier
                    .padding(end = 4.dp)
                    .size(16.dp)
            )
            Text(
                text = "$currentArchiveIndex/${archiveMeta.total}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp
                ),
                maxLines = 1,
                color = Color.LightGray
            )
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = Icons.AutoMirrored.Outlined.KeyboardArrowRight.name,
                tint = Color.LightGray,
            )
        }
    }
}

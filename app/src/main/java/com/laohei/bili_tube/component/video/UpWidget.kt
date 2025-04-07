package com.laohei.bili_tube.component.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laohei.bili_tube.R

@Composable
internal fun UpWidget(ownerName:String){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.icon_up),
            contentDescription = "up",
            modifier = Modifier.height(12.dp),
            colorFilter = ColorFilter.tint(Color.Gray)
        )

        Text(
            text = ownerName,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall
                .copy(fontSize = 10.sp),
            color = Color.Gray
        )
    }
}
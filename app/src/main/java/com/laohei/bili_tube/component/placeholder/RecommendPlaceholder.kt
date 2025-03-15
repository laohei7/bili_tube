package com.laohei.bili_tube.component.placeholder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
internal fun RecommendPlaceholder(
    isSingleLayout: Boolean = true
) {
    Card(
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
                .clip(RoundedCornerShape(if (isSingleLayout) 0.dp else 12.dp))
                .background(color = Color.LightGray),
        )

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(start = 12.dp, end = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(color = Color.LightGray),
            )

            Column(
                modifier = Modifier.weight(1f).height(IntrinsicSize.Min),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(CircleShape)
                        .background(color = Color.LightGray),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(12.dp)
                        .clip(CircleShape)
                        .background(color = Color.LightGray),
                )
            }
        }
    }
}
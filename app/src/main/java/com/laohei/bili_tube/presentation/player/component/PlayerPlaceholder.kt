package com.laohei.bili_tube.presentation.player.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.component.placeholder.RecommendPlaceholder

@Preview
@Composable
fun PlayerPlaceholder(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ListItem(
                headlineContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                },
                supportingContent = {
                    Box(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .fillMaxWidth(0.8f)
                            .height(12.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                }
            )

            ListItem(
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                },
                headlineContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                },
                trailingContent = {
                    Surface(
                        shape = CircleShape,
                        color = Color.LightGray,
                        contentColor = Color.LightGray,
                        modifier = Modifier
                            .clip(CircleShape)
                    ) {
                        Text(
                            text = "订阅",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier)
                Box(
                    Modifier
                        .width(90.dp)
                        .height(28.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
                repeat(3) {
                    Box(
                        Modifier
                            .width(60.dp)
                            .height(28.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                }
                Spacer(modifier = Modifier)
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        color = Color.LightGray,
                        shape = CardDefaults.shape
                    )
                    .clip(CardDefaults.shape),
            )
            Spacer(Modifier.height(8.dp))
            repeat(10) {
                RecommendPlaceholder()
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
package com.laohei.bili_tube.component.appbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.R

@Composable
fun LogoTopAppBar(
    alpha: Float = 1f,
    searchOnClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .height(40.dp)
            .background(
                color = Color.Transparent
            )
            .padding(horizontal = 12.dp)
            .graphicsLayer {
                this.alpha = alpha
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(
                if (isSystemInDarkTheme()) R.drawable.logo_dark
                else R.drawable.logo_light
            ),
            contentDescription = "logo",
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
//            IconButton(
//                onClick = {},
//                colors = IconButtonDefaults.iconButtonColors(
//                    contentColor = MaterialTheme.colorScheme.onBackground
//                )
//            ) {
//                BadgedBox(
//                    badge = {
//                        Badge {
//                            Text(text = "2")
//                        }
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Notifications,
//                        contentDescription = Icons.Default.Notifications.name
//                    )
//                }
//            }


            IconButton(
                onClick = { searchOnClick.invoke() },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = Icons.Default.Search.name
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LogoTopAppBarPreview() {
    LogoTopAppBar()
}
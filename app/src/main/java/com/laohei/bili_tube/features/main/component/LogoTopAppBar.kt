package com.laohei.bili_tube.features.main.component

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
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
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
import com.laohei.bili_tube.ui.theme.PaddingMd
import com.laohei.bili_tube.ui.theme.PaddingNone

@Composable
internal fun LogoTopAppBar(
    alpha: Float = 1f,
    isShowMenu: Boolean = false,
    onSearchClick: () -> Unit,
    onMenuClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .height(40.dp)
            .background(
                color = Color.Transparent
            )
            .padding(
                start = if (isShowMenu) PaddingNone else PaddingMd,
                end = PaddingMd
            )
            .graphicsLayer {
                this.alpha = alpha
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (isShowMenu) {
            IconButton(
                onClick = { onMenuClick?.invoke() },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = Icons.Rounded.Menu.name
                )
            }
        }

        Image(
            painter = painterResource(
                if (isSystemInDarkTheme()) R.drawable.logo_dark
                else R.drawable.logo_light
            ),
            contentDescription = "logo",
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.weight(1f)
        ) {
            IconButton(
                onClick = { onSearchClick.invoke() },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = Icons.Rounded.Search.name
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LogoTopAppBarPreview() {
    LogoTopAppBar(onMenuClick = {}, onSearchClick = {})
}

@Preview(showBackground = true)
@Composable
private fun LogoTopAppBarDesktopPreview() {
    LogoTopAppBar(isShowMenu = true, onMenuClick = {}, onSearchClick = {})
}
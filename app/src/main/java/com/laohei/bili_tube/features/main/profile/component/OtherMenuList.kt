package com.laohei.bili_tube.features.main.profile.component

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.laohei.bili_tube.R
import com.laohei.bili_tube.nav.AppRoute


@Composable
fun OtherMenuList(
    navigateToAppRoute: (AppRoute) -> Unit
) {
    ListItem(
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.VideoLibrary,
                contentDescription = Icons.Outlined.VideoLibrary.name,
            )
        },
        headlineContent = {
            Text(text = stringResource(R.string.str_manuscript_management))
        }
    )
    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
    ListItem(
        modifier = Modifier.clickable {
            navigateToAppRoute(AppRoute.Download)
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.Download,
                contentDescription = Icons.Outlined.Download.name,
            )
        },
        headlineContent = {
            Text(text = stringResource(R.string.str_download_management))
        }
    )
}


package com.laohei.bili_tube.features.main.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.nav.AppRoute


@Composable
fun ProfileTopBar(
    navigateToAppRoute: (AppRoute) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(40.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = {
                navigateToAppRoute(AppRoute.Search)
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = Icons.Outlined.Search.name,
            )
        }
        IconButton(onClick = {
            navigateToAppRoute(AppRoute.SettingNav)
        }) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = Icons.Outlined.Settings.name,
            )
        }
    }
}
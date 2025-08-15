package com.laohei.bili_tube.features.login.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.layoutId

@Composable
internal fun LoginBtn(
    onLogin: () -> Unit
) {
    FloatingActionButton(
        onClick = { onLogin() },
        shape = CircleShape,
        modifier = Modifier
            .layoutId("login_btn")
            .size(50.dp),
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = Icons.AutoMirrored.Rounded.ArrowForward.name,
        )
    }
}
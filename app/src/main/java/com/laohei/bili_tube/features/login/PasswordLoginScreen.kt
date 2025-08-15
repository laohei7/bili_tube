package com.laohei.bili_tube.features.login

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.geetest.sdk.views.GT3GeetestButton
import com.laohei.bili_tube.R


@Composable
internal fun PasswordLoginScreen(
    codeInteractionSource: MutableInteractionSource? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            singleLine = true,
            label = {
                Text(text = stringResource(R.string.str_account))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = Icons.Outlined.AccountCircle.name,
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = "",
            onValueChange = {},
            singleLine = true,
            interactionSource = codeInteractionSource,
            label = {
                Text(text = stringResource(R.string.str_password))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Password,
                    contentDescription = Icons.Outlined.Password.name,
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        AndroidView(
            factory = {
                GT3GeetestButton(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp)
        ) {
            Text(text = stringResource(R.string.str_login))
        }
    }
}
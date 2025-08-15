package com.laohei.bili_tube.features.login.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.constraintlayout.compose.layoutId
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.theme.SmallPadding

@Composable
internal fun LoginMethod(
    content: @Composable RowScope.() -> Unit
) {
    Column(
        modifier = Modifier.layoutId("login_method"),
        verticalArrangement = Arrangement.spacedBy(SmallPadding)
    ) {
        Text(
            text = stringResource(R.string.str_login_method),
            style = MaterialTheme.typography.labelLarge,
            textDecoration = TextDecoration.Underline
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(SmallPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

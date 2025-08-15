package com.laohei.bili_tube.features.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.theme.Pink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BoxScope.LoginTopBar(
    title: String,
    hasCodeFocus: Boolean,
    upPress: (() -> Unit)?
) {
    Image(
        painter = when {
            hasCodeFocus -> painterResource(R.drawable.login_left_close)
            else -> painterResource(R.drawable.login_left)
        },
        contentDescription = "left",
        modifier = Modifier
            .align(Alignment.TopStart)
            .statusBarsPadding()
    )
    Image(
        painter = when {
            hasCodeFocus -> painterResource(R.drawable.login_right_close)
            else -> painterResource(R.drawable.login_right)
        },
        contentDescription = "right",
        modifier = Modifier
            .align(Alignment.TopEnd)
            .statusBarsPadding()
    )
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        ),
        title = {
            Text(text = title)
        },
        navigationIcon = {
            upPress?.let {
                IconButton(
                    onClick = it,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Pink
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = Icons.AutoMirrored.Outlined.ArrowBack.name,
                    )
                }
            }
        }
    )
}
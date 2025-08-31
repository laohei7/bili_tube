package com.laohei.bili_tube.ui.component.state

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.theme.PaddingLg

@Composable
fun UnderConstructionPlaceholder(
    imageRes: Int = R.drawable.bili_emoji2,
    text: String = stringResource(R.string.str_under_development)
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PaddingLg, Alignment.CenterVertically)
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}


@Preview
@Composable
private fun UnderConstructionPlaceholderPreview() {
    UnderConstructionPlaceholder()
}
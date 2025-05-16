package com.laohei.bili_tube.component.text

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.laohei.bili_tube.R
import com.laohei.bili_tube.utill.toViewString

@Composable
fun VerticalDataText(
    data: Long,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = data.toViewString(),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VerticalDataTextPreview() {
    VerticalDataText(
        data = 10000000,
        label = stringResource(R.string.str_following)
    )
}
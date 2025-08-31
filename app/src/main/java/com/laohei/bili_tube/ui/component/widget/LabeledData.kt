package com.laohei.bili_tube.ui.component.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.theme.PaddingXs
import com.laohei.bili_tube.util.toViewString

@Composable
fun LabeledData(
    data: Long,
    label: String,
    dataStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    labelStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    spacing: Dp = PaddingXs
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        Text(
            text = data.toViewString(),
            style = dataStyle
        )
        Text(
            text = label,
            style = labelStyle
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FollowingPreview() {
    LabeledData(
        data = 10000000,
        label = stringResource(R.string.str_following)
    )
}
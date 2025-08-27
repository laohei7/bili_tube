package com.laohei.bili_tube.ui.component.icons

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.util.fastForEach
import com.laohei.bili_tube.ui.preview.FakeIcon
import com.laohei.bili_tube.ui.preview.FakeIconData

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true)
@Composable
private fun IconsPreview(
    @PreviewParameter(FakeIconData::class) icons: List<FakeIcon>
) {
    FlowRow {
        icons.fastForEach { fakeIcon ->
            Icon(
                imageVector = fakeIcon.icon,
                contentDescription = fakeIcon.icon.name,
                tint = fakeIcon.color
            )
        }
    }
}
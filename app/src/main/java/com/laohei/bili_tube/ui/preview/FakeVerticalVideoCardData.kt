package com.laohei.bili_tube.ui.preview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class FakeVerticalVideoCardData : PreviewParameterProvider<ImageVector?> {
    override val values: Sequence<ImageVector?>
        get() = sequenceOf(
            Icons.Rounded.MoreVert,
            null
        )
}
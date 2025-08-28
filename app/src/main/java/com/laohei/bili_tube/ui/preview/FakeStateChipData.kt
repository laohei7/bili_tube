package com.laohei.bili_tube.ui.preview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.ThumbDown
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import com.laohei.bili_tube.ui.theme.SmallPadding
import com.laohei.bili_tube.utill.toViewString


data class FakeStateChip(
    val icon: ImageVector,
    val label: String,
    val trailingIcon: ImageVector? = null,
    val contentPadding: Dp = SmallPadding,
    val iconColor: Color = Color.Unspecified,
    val labelColor: Color = Color.Unspecified,
    val trailingIconColor: Color = Color.Unspecified,
    val dividerColor: Color = Color.Gray,
)

class FakeStateChipData : PreviewParameterProvider<FakeStateChip> {
    override val values: Sequence<FakeStateChip>
        get() = sequenceOf(
            FakeStateChip(
                icon = Icons.Rounded.ThumbUp,
                label = 99999.toViewString(),
                trailingIcon = Icons.Rounded.ThumbDown
            ),
            FakeStateChip(
                icon = Icons.Rounded.Star,
                label = 99999.toViewString(),
            )
        )
}
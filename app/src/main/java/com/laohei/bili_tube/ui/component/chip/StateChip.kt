package com.laohei.bili_tube.ui.component.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.laohei.bili_tube.ui.preview.FakeStateChip
import com.laohei.bili_tube.ui.preview.FakeStateChipData
import com.laohei.bili_tube.ui.theme.ExtremeSmallPadding
import com.laohei.bili_tube.ui.theme.LargePadding
import com.laohei.bili_tube.ui.theme.MediumPadding


@Composable
fun StateChip(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector,
    label: String,
    trailingIcon: ImageVector? = null,
    horizontalPadding: Dp = MediumPadding,
    verticalPadding: Dp = ExtremeSmallPadding,
    iconColor: Color = Color.Unspecified,
    labelColor: Color = Color.Unspecified,
    trailingIconColor: Color = Color.Unspecified,
    dividerColor: Color = Color.Red,
    onClick: () -> Unit,
    onTrailingClick: (() -> Unit)? = null
) {
    Layout(
        modifier = modifier.clickable(enabled) { onClick() },
        content = {
            Surface(
                modifier = Modifier.size(24.dp),
                color = Color.Transparent,
                contentColor = iconColor
            ) {
                Icon(
                    modifier = Modifier.padding(ExtremeSmallPadding / 2),
                    imageVector = icon,
                    contentDescription = icon.name,
                    tint = iconColor
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = labelColor,
                maxLines = 1,
                modifier = Modifier.padding(start = ExtremeSmallPadding)
            )
            if (trailingIcon != null) {
                VerticalDivider(
                    modifier = Modifier
                        .layoutId("divider")
                        .padding(horizontal = LargePadding)
                        .padding(vertical = verticalPadding)
                        .clip(CircleShape),
                    color = dividerColor,
                    thickness = 1.5.dp
                )
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = CircleShape,
                    onClick = { onTrailingClick?.invoke() },
                    color = Color.Transparent,
                    contentColor = trailingIconColor
                ) {
                    Icon(
                        modifier = Modifier.padding(ExtremeSmallPadding / 2),
                        imageVector = trailingIcon,
                        contentDescription = trailingIcon.name,
                        tint = trailingIconColor
                    )
                }
            }
        }
    ) { measurables, constraints ->
        val nonDividerMeasurables = measurables
            .filterIndexed { index, _ -> measurables[index].layoutId != "divider" }
        val nonDividerPlaceables = nonDividerMeasurables.map { it.measure(constraints) }

        val maxHeight = nonDividerPlaceables.maxOfOrNull { it.height } ?: 0

        val dividerPlaceable =
            measurables.filterIndexed { index, _ -> measurables[index].layoutId == "divider" }
                .map {
                    it.measure(constraints.copy(minHeight = maxHeight, maxHeight = maxHeight))
                }.firstOrNull()


        val (verticalPx, horizontalPx) = verticalPadding.roundToPx() to horizontalPadding.roundToPx()
        val totalWidth = nonDividerPlaceables.sumOf { it.width } + (dividerPlaceable?.width
            ?: 0) + horizontalPx * 2


        val layoutWidth = constraints.maxWidth.coerceAtMost(totalWidth)
        val layoutHeight = maxHeight + verticalPx * 2

        layout(layoutWidth, layoutHeight) {
            var xPosition = horizontalPx
            nonDividerPlaceables.fastForEachIndexed { index, placeable ->
                if (dividerPlaceable != null && index == 2) {
                    dividerPlaceable.placeRelative(
                        x = xPosition,
                        y = layoutHeight / 2 - dividerPlaceable.height / 2
                    )
                    xPosition += dividerPlaceable.width
                }
                placeable.placeRelative(
                    x = xPosition,
                    y = layoutHeight / 2 - placeable.height / 2
                )
                xPosition += placeable.width
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StateChipPreview(
    @PreviewParameter(FakeStateChipData::class) chip: FakeStateChip
) {
    StateChip(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainer),
        icon = chip.icon,
        label = chip.label,
        trailingIcon = chip.trailingIcon,
        onClick = {}
    )
}
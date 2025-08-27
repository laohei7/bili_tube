package com.laohei.bili_tube.ui.preview

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class FakeNineGridLayoutData : PreviewParameterProvider<List<Color>> {
    override val values: Sequence<List<Color>>
        get() = sequenceOf(
            listOf(Color.Yellow),
            listOf(Color.Yellow, Color.Blue),
            listOf(Color.Yellow, Color.Blue, Color.Cyan),
            listOf(Color.Yellow, Color.Blue, Color.Cyan, Color.Red),
            listOf(Color.Yellow, Color.Blue, Color.Cyan, Color.Red, Color.Magenta),
            listOf(Color.Yellow, Color.Blue, Color.Cyan, Color.Red, Color.Magenta, Color.Gray),
            listOf(
                Color.Green, Color.Blue,
                Color.Yellow, Color.Cyan, Color.Magenta,
                Color.Gray, Color.LightGray, Color.DarkGray, Color.Red,
            ),
            listOf(
                Color.Green, Color.Blue,
                Color.Yellow, Color.Cyan, Color.Magenta,
                Color.Gray, Color.LightGray, Color.DarkGray, Color.Red, Color.Blue
            ),
        )
}
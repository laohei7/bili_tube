package com.laohei.bili_tube.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class FakeRecommendationPlaceholderData : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean>
        get() = sequenceOf(true, false)
}
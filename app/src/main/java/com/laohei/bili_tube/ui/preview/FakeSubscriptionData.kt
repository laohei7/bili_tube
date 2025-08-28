package com.laohei.bili_tube.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class FakeSubscriptionData : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean>
        get() = sequenceOf(false, true)
}
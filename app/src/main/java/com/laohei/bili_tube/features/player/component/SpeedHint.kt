package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.component.animation.lottie.AnimatedSpeedIcon
import com.laohei.bili_tube.ui.theme.PaddingXs
import com.laohei.bili_tube.ui.theme.PaddingSm

@Composable
internal fun SpeedHint(
    modifier: Modifier = Modifier,
    speed: Float
) {
    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.5f),
        contentColor = Color.White,
        shape = RoundedCornerShape(PaddingSm)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = PaddingSm,
                vertical = PaddingXs
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                PaddingXs,
                Alignment.CenterHorizontally
            )
        ) {
            Text(
                text = stringResource(R.string.str_speed_hint_template, speed),
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,

                )

            AnimatedSpeedIcon(
                modifier = Modifier
                    .size(20.dp)
                    .rotate(180f)
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun SpeedHintPreview() {
    SpeedHint(speed = 2.0f)
}
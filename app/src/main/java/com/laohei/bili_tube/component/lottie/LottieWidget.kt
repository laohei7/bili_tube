package com.laohei.bili_tube.component.lottie

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.laohei.bili_tube.R

@Composable
fun LottieWidget(
    modifier: Modifier = Modifier,
    @RawRes id: Int
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(id))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
    )
    LottieAnimation(
        modifier = modifier,
        composition = composition,
        progress = { progress },
    )
}

@Composable
fun LottieIconSpeed(
    modifier: Modifier=Modifier
) {
    LottieWidget(
        id = R.raw.lottie_speed,
        modifier = modifier
    )
}

@Composable
fun LottieIconPlaying(
    modifier: Modifier=Modifier
) {
    LottieWidget(
        id = R.raw.lottie_playing,
        modifier = modifier
    )
}

@Preview
@Composable
fun LottieIconLike(
    modifier: Modifier=Modifier
) {
    LottieWidget(
        id = R.raw.lottie_like,
        modifier = modifier
    )
}
package com.laohei.bili_tube.ui.component.animation.lottie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.laohei.bili_tube.R

@Composable
fun LottieAnimationView(
    modifier: Modifier = Modifier,
    spec: LottieCompositionSpec,
    iterateForever: Boolean = true,
    onAnimationEnd: (() -> Unit)? = null
) {
    val composition by rememberLottieComposition(spec)
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = if (iterateForever) LottieConstants.IterateForever else 1,
    )
    var hasEnded by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(progress) {
        if (!iterateForever && progress >= 1f && !hasEnded) {
            hasEnded = true
            onAnimationEnd?.invoke()
        }
    }
    LottieAnimation(
        modifier = modifier,
        composition = composition,
        progress = { progress },
    )
}

// TODO(NOTE: Only enable one Lottie icon in Preview, since multiple previews rendered in parallel may cause errors.)
//@Preview
@Composable
fun AnimatedSpeedIcon(
    modifier: Modifier = Modifier
) {
    LottieAnimationView(
        spec = LottieCompositionSpec.RawRes(R.raw.lottie_speed),
        modifier = modifier
    )
}

//@Preview
@Composable
fun AnimatedPlayingIcon(
    modifier: Modifier = Modifier
) {
    LottieAnimationView(
        spec = LottieCompositionSpec.RawRes(R.raw.lottie_playing),
        modifier = modifier
    )
}

//@Preview
@Composable
fun AnimatedLikeIcon(
    modifier: Modifier = Modifier,
    onAnimationEndCallback: (() -> Unit)? = null
) {
    LottieAnimationView(
        spec = LottieCompositionSpec.RawRes(R.raw.lottie_like),
        modifier = modifier,
        iterateForever = false,
        onAnimationEnd = onAnimationEndCallback
    )
}

//@Preview
@Composable
fun AnimatedLoadingIcon(
    modifier: Modifier = Modifier
) {
    LottieAnimationView(
        spec = LottieCompositionSpec.RawRes(R.raw.lottie_loading_face),
        modifier = modifier
    )
}
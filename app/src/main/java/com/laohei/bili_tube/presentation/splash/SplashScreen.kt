package com.laohei.bili_tube.presentation.splash

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.util.hideSystemUI
import com.laohei.bili_tube.core.util.showSystemUI
import kotlinx.coroutines.delay

private sealed interface SplashStep {
    data object None : SplashStep
    data object Start : SplashStep
    data object Loading : SplashStep
    data object End : SplashStep
}

@Preview
@Composable
fun SplashScreen(
    navigateToRoute: () -> Unit = {}
) {
    val localActivity = LocalActivity.current

    DisposableEffect(Unit) {
        localActivity?.hideSystemUI()
        onDispose {
            localActivity?.showSystemUI()
        }
    }

    var step by remember { mutableStateOf<SplashStep>(SplashStep.None) }

    val transition = updateTransition(targetState = step, label = "imageTransition")
    LaunchedEffect(Unit) {
        step = SplashStep.Start
        delay(800)
        step = SplashStep.Loading
        delay(800)
        step = SplashStep.End
        delay(800)
        navigateToRoute.invoke()
    }
    val alpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) },
        label = "alpha"
    ) {
        when (it) {
            SplashStep.End,
            SplashStep.None -> 0f

            SplashStep.Start,
            SplashStep.Loading -> 1f
        }
    }

    val scale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) },
        label = "scale"
    ) {
        when (it) {
            SplashStep.None -> 0f
            SplashStep.End,
            SplashStep.Loading,
            SplashStep.Start -> 1f

        }
    }

    val loadingWidth by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) },
        label = "width"
    ) {
        when (it) {
            SplashStep.End,
            SplashStep.None,

            SplashStep.Start -> 0f

            SplashStep.Loading -> 0.8f
        }
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = when {
                    isSystemInDarkTheme() -> painterResource(R.drawable.logo_dark)
                    else -> painterResource(R.drawable.logo_light)
                },
                contentDescription = "logo",
                modifier = Modifier.graphicsLayer {
                    this.alpha = alpha
                    this.scaleX = scale
                    this.scaleY = scale
                }
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(loadingWidth)
                    .graphicsLayer {
                        this.alpha = alpha
                    },
                thickness = 3.dp,
                color = Color.Red
            )
        }
    }
}
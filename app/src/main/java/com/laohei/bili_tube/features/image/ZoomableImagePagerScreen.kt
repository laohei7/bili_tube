package com.laohei.bili_tube.features.image

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.component.widget.ZoomableImageViewer

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ZoomableImagePagerScreen(
    galleryParam: AppRoute.Gallery,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    ZoomableImageViewer(
        images = galleryParam.images,
        initialIndex = galleryParam.initialIndex,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    )
}
package com.laohei.bili_tube.ui.component

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import coil3.size.Size
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.theme.ExtremeSmallPadding
import com.laohei.bili_tube.ui.theme.LargePadding
import com.laohei.bili_tube.ui.theme.SmallPadding

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RemoteImageGallery(
    images: List<Pair<String, String>>,
    initialIndex: Int = 0,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val context = LocalContext.current
    val pager = rememberPagerState(initialPage = initialIndex) { images.size }
    val imageIndexLabel by remember {
        derivedStateOf { "${pager.currentPage + 1}/${images.size}" }
    }
    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pager,
            ) { index ->
                val item = images[index]
                val imageRequest = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(item.second)
                        .crossfade(true)
                        .size(Size.ORIGINAL)
                        .placeholder(R.drawable.icon_loading_1_1)
                        .error(R.drawable.icon_loading_1_1)
                        .build()
                )
                Image(
                    painter = imageRequest,
                    contentDescription = "picture-${item.first}",
                    modifier = Modifier
                        .then(
                            if (pager.currentPage == index) {
                                Modifier.sharedElement(
                                    state = rememberSharedContentState(item.first),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            } else {
                                Modifier
                            }
                        )
                        .fillMaxWidth(),
                    contentScale = ContentScale.Fit,
                )
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding(),
                contentColor = Color.White,
                color = Color.Gray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(ExtremeSmallPadding)
            ) {
                Text(
                    text = imageIndexLabel,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(vertical = SmallPadding, horizontal = LargePadding),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}
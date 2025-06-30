package com.laohei.bili_tube.presentation.browser

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.DRAWItemParam
import com.laohei.bili_tube.component.text.RichText
import com.laohei.common_ui.info.DRAWItemAuthorInfoBar

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DRAWImagesBrowser(
    drawItemParam: DRAWItemParam,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    upPress: () -> Unit = {}
) {
    val pager =
        rememberPagerState(initialPage = drawItemParam.initialIndex) { drawItemParam.images.size }
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(true) }

    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            HorizontalPager(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(),
                state = pager
            ) { index ->
                val img = drawItemParam.images[index]
                val imageRequest = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(img)
                        .crossfade(true)
                        .placeholder(R.drawable.icon_loading_1_1)
                        .error(R.drawable.icon_loading_1_1)
                        .build()
                )

                Image(
                    painter = imageRequest,
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .sharedElement(
                            state = rememberSharedContentState(img),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentScale = ContentScale.FillWidth
                )
            }
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            upPress()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "close",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    if (drawItemParam.images.size > 1) {
                        Text(
                            text = "${pager.currentPage + 1}/${drawItemParam.images.size}",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {},
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = "more_vert",
                            tint = Color.White
                        )
                    }
                }
            )




            AnimatedContent(
                targetState = isExpanded,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding(),
                transitionSpec = {
                    fadeIn().togetherWith(fadeOut())
                }
            ) { target ->
                when {
                    target -> {
                        Column(
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            DRAWItemAuthorInfoBar(
                                face = drawItemParam.face,
                                ownerName = drawItemParam.ownerName,
                                pubDate = drawItemParam.date,
                                placeholder = R.drawable.icon_loading_1_1,
                                error = R.drawable.icon_loading_1_1,
                                trailingIcon = Icons.Outlined.KeyboardArrowDown
                            ) { isExpanded = false }
                            RichText(
                                text = drawItemParam.desc,
                                style = MaterialTheme.typography.bodyMedium,
                                emote = emptyMap(),
                                color = Color.White,
                                collapsedMaxLine = 2,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }

                    else -> {
                        IconButton(onClick = { isExpanded = true }) {
                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowUp,
                                contentDescription = Icons.Outlined.KeyboardArrowUp.name,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun DRAWItemImagesPreview() {
    SharedTransitionScope {
        AnimatedVisibility(visible = true) {
            DRAWImagesBrowser(
                drawItemParam = DRAWItemParam(
                    face = "",
                    ownerName = "动漫作业本",
                    date = "11 小时前",
                    desc = "Hello World!!!",
                    images = listOf(""),
                ),
                sharedTransitionScope = this@SharedTransitionScope,
                animatedVisibilityScope = this
            )
        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun DRAWItemImagesPreview2() {
    SharedTransitionScope {
        AnimatedVisibility(visible = true) {
            DRAWImagesBrowser(
                drawItemParam = DRAWItemParam(
                    face = "",
                    ownerName = "动漫作业本",
                    date = "11 小时前",
                    desc = "Hello World!!!",
                    images = listOf("", "", "", ""),
                ),
                sharedTransitionScope = this@SharedTransitionScope,
                animatedVisibilityScope = this
            )
        }
    }
}
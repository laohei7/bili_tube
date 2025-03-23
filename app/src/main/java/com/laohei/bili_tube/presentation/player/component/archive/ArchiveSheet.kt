package com.laohei.bili_tube.presentation.player.component.archive

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.laohei.bili_sdk.module_v2.video.ArchiveItem
import com.laohei.bili_sdk.module_v2.video.ArchiveMeta
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.component.lottie.LottieIconPlaying
import com.laohei.bili_tube.component.sheet.ModalBottomSheet
import com.laohei.bili_tube.component.sheet.rememberModalBottomSheet
import com.laohei.bili_tube.utill.formatTimeString
import com.laohei.bili_tube.utill.toTimeAgoString
import com.laohei.bili_tube.utill.toViewString
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ArchiveSheet(
    modifier: Modifier = Modifier,
    archiveMeta: ArchiveMeta,
    archives: List<ArchiveItem>,
    isShowSheet: Boolean = false,
    currentArchiveIndex: Int = 0,
    lazyListState: LazyListState = rememberLazyListState(),
    bottomPadding: Dp = 0.dp,
    onDismiss: () -> Unit = {},
    maskAlphaChanged: (Float) -> Unit = { _ -> },
    onClick: (Route.Play) -> Unit
) {
    BackHandler(enabled = isShowSheet) {
        onDismiss.invoke()
    }
    val sheetState = rememberModalBottomSheet(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val minHeight = 80.dp
    val maxHeight = 120.dp

    val minHeightPx = with(density) {
        minHeight.toPx()
    }

    val maxHeightPx = with(density) {
        maxHeight.toPx()
    }
    var rawAlpha by remember { mutableFloatStateOf(1f) }
    val alpha by animateFloatAsState(targetValue = rawAlpha, label = "alpha")
    var rawLogoHeight by remember { mutableIntStateOf(with(density) { 0.dp.toPx() }.toInt()) }
    val descriptionHeight by animateIntAsState(
        targetValue = rawLogoHeight,
        label = "descriptionHeight"
    )
    var topHeightPx by remember { mutableFloatStateOf(maxHeightPx) }

    val connection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source == NestedScrollSource.UserInput) {
                    if (available.y < 0) {
                        val dH = minHeightPx - topHeightPx
                        rawAlpha = abs(dH) / (maxHeightPx - minHeightPx)
                        rawLogoHeight =
                            with(density) { lerp((-40).dp, 0.dp, rawAlpha).toPx().toInt() }
                        topHeightPx += if (available.y > dH) {
                            available.y
                        } else {
                            dH
                        }
                    } else {
                        val dH = maxHeightPx - topHeightPx
                        rawAlpha = 1f - abs(dH) / (maxHeightPx - minHeightPx)
                        rawLogoHeight =
                            with(density) { lerp((-40).dp, 0.dp, rawAlpha).toPx().toInt() }
                        topHeightPx += if (available.y < dH) {
                            available.y
                        } else {
                            dH
                        }
                    }
                }
                return Offset.Zero
            }
        }
    }

    if (isShowSheet) {
        LaunchedEffect(sheetState) {
            snapshotFlow { sheetState.requireOffset() }
                .collect { offset ->
                    maskAlphaChanged.invoke(offset)
                }
        }
        LaunchedEffect(currentArchiveIndex) {
            lazyListState.scrollToItem(currentArchiveIndex)
        }
        ModalBottomSheet(
            modifier = modifier
                .fillMaxSize()
                .nestedScroll(connection),
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            containerColor = MaterialTheme.colorScheme.background,
            scrimColor = Color.Transparent,
            onDismissRequest = { onDismiss.invoke() },
        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = archiveMeta.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .basicMarquee()
                )
                Surface(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDismiss.invoke()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = Icons.Outlined.Close.name,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(3.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
                    .offset {
                        IntOffset(0, descriptionHeight)
                    }
                    .graphicsLayer {
                        this.alpha = alpha
                    }
            ) {
                Text(
                    text = stringResource(R.string.str_all_num_episode, archiveMeta.total),
                    maxLines = 1,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.LightGray
                )
                Text(
                    text = archiveMeta.description,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.LightGray,
                    overflow = TextOverflow.Ellipsis
                )
            }
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset {
                        IntOffset(0, descriptionHeight)
                    }
            ) {
                stickyHeader {
                    Text(
                        text = stringResource(R.string.str_select_episode),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 16.dp)
                            .padding(vertical = 8.dp)
                    )
                }
                itemsIndexed(archives) { index, item ->
                    ArchiveWidgetItem(
                        cover = item.pic,
                        title = item.title,
                        view = item.stat.view.toViewString(),
                        duration = item.duration.formatTimeString(false),
                        progress = item.playbackPosition.toFloat() / item.duration,
                        pubdate = item.pubdate.toTimeAgoString(),
                        isCurrentPlaying = index == currentArchiveIndex,
                        onClick = {
                            onClick.invoke(
                                Route.Play(
                                    aid = item.aid,
                                    bvid = item.bvid,
                                    cid = -1
                                )
                            )
                        }
                    )
                }
                item { Spacer(Modifier.height(bottomPadding)) }
            }
        }
    }
}


@Composable
private fun ArchiveWidgetItem(
    cover: String,
    title: String,
    view: String,
    duration: String,
    progress: Float,
    pubdate: String,
    isCurrentPlaying: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                onClick.invoke()
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val shape = remember { RoundedCornerShape(12.dp) }
        val coverModifier = Modifier
            .weight(1f)
            .aspectRatio(16 / 9f)
            .clip(shape)

        Box(
            modifier = coverModifier
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(cover)
                    .crossfade(true)
                    .build(),
                contentDescription = title,
                modifier = Modifier
                    .width(180.dp)
                    .aspectRatio(16 / 9f)
                    .clip(shape),
                contentScale = ContentScale.Crop,
            )
            LinearProgressIndicator(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(),
                progress = { progress },
                trackColor = Color.White.copy(alpha = 0.2f),
                color = Color.Red
            )
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 8.dp, end = 8.dp),
                color = Color.Black.copy(alpha = 0.5f),
                contentColor = Color.White,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = duration,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(3.dp)
                )
            }

            if (isCurrentPlaying) {
                LottieIconPlaying(Modifier.align(Alignment.Center))
            }
        }
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {

                Icon(
                    imageVector = Icons.Outlined.Update,
                    contentDescription = Icons.Outlined.Update.name,
                    modifier = Modifier
                        .size(16.dp),
                    tint = Color.Gray
                )

                Text(
                    text = pubdate,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall
                        .copy(fontSize = 10.sp),
                    color = Color.Gray
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {

                Icon(
                    imageVector = Icons.Outlined.PlayCircleOutline,
                    contentDescription = Icons.Outlined.PlayCircleOutline.name,
                    modifier = Modifier
                        .size(16.dp),
                    tint = Color.Gray
                )

                Text(
                    text = "${view}观看",
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall
                        .copy(fontSize = 10.sp),
                    color = Color.Gray
                )
            }
        }
    }
}
package com.laohei.bili_tube.presentation.dynamic

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Topic
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.laohei.bili_sdk.module_v2.dynamic.DynamicItem
import com.laohei.bili_sdk.module_v2.dynamic.MajorLiveRcmdContent
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.component.appbar.LogoTopAppBar
import com.laohei.bili_tube.component.placeholder.NoMoreData
import com.laohei.bili_tube.component.placeholder.RecommendPlaceholder
import com.laohei.bili_tube.component.text.ExpandedText
import com.laohei.bili_tube.component.video.VideoItem
import com.laohei.bili_tube.component.video.VideoMenuSheet
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.utill.toTimeAgoString
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel

private const val TAG = "DynamicScreen"

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicScreen(
    dynamicViewModel: DynamicViewModel = koinViewModel(),
    navigateToRoute: (Route) -> Unit
) {
    val scope = rememberCoroutineScope()
    val refreshState = rememberPullToRefreshState()
    val gridState = dynamicViewModel.gridState
    val dynamicList = dynamicViewModel.dynamicList.collectAsLazyPagingItems()
    val isLoading = dynamicList.loadState.refresh is LoadState.Loading
    var isShowMenuSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        EventBus.events.collect { event ->
            when (event) {
                is Event.NotificationChildRefresh -> {
                    scope.launch {
                        gridState.scrollToItem(0)
                        dynamicList.refresh()
                    }
                }
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val fixedCount = when {
            maxWidth < 500.dp -> 1
            maxWidth >= 500.dp && maxWidth < 800.dp -> 2
            maxWidth >= 800.dp && maxWidth < 1280.dp -> 3
            else -> 4
        }
        PullToRefreshBox(
            isRefreshing = isLoading,
            state = refreshState,
            onRefresh = { dynamicList.refresh() },
            indicator = {
                Indicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding(),
                    isRefreshing = isLoading,
                    state = refreshState,
                )
            }
        ) {
            val isEmpty = dynamicList.itemCount == 0
            LazyVerticalStaggeredGrid (
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.background
                ),
                state = gridState,
                columns = StaggeredGridCells.Fixed(fixedCount),
                contentPadding = PaddingValues(horizontal = if (fixedCount == 1) 0.dp else 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
//                verticalArrangement = Arrangement.spacedBy(
//                    if (isEmpty) 16.dp else 0.dp
//                )
            ) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    LogoTopAppBar()
                }
                when {
                    isEmpty -> {
                        items(12) {
                            RecommendPlaceholder(isSingleLayout = fixedCount == 1)
                        }
                    }

                    else -> {
                        items(dynamicList.itemCount) { index ->
                            dynamicList[index]?.let {
                                Column (
                                    modifier = Modifier.padding(vertical = 8.dp),
                                ) {
                                    GetDynamicItem(
                                        item = it,
                                        isSingleLayout = fixedCount == 1,
                                        navigateToRoute = navigateToRoute,
                                        onMenuClick = {
                                            isShowMenuSheet = true
                                        }
                                    )

                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item(span =StaggeredGridItemSpan.FullLine) {
                    NoMoreData(dynamicList.loadState.append)
                }
                item(span = StaggeredGridItemSpan.FullLine) {
                    Spacer(
                        modifier = Modifier
                            .navigationBarsPadding()
                    )
                }
            }
        }
        VideoMenuSheet(
            isShowSheet = isShowMenuSheet,
            onDismiss = { isShowMenuSheet = false }
        )
    }
}

@Composable
private fun GetDynamicItem(
    isSingleLayout: Boolean = true,
    item: DynamicItem,
    navigateToRoute: (Route) -> Unit,
    onMenuClick: (() -> Unit)? = null
) {
    val author = item.modules.moduleAuthor
    when (item.type) {
        DynamicItem.DYNAMIC_TYPE_AV -> {
            val archive = item.modules.moduleDynamic.major!!.archive!!
            VideoItem(
                isSingleLayout = isSingleLayout,
                key = archive.bvid,
                cover = archive.cover,
                title = archive.title,
                face = author.face,
                ownerName = author.name,
                view = archive.stat.play,
                date = author.pubTs.toTimeAgoString(),
                duration = archive.durationText,
                onClick = {
                    navigateToRoute.invoke(
                        Route.Play(
                            aid = archive.aid.toLong(),
                            bvid = archive.bvid,
                            cid = -1
                        )
                    )
                },
                onMenuClick = onMenuClick
            )
        }

        DynamicItem.DYNAMIC_TYPE_DRAW -> {
            val draw = item.modules.moduleDynamic.major!!.draw!!
            val desc = item.modules.moduleDynamic.desc?.text ?: ""
            DRAWItem(
                face = author.face,
                ownerName = author.name,
                date = author.pubTs.toTimeAgoString(),
                desc = desc,
                images = draw.items.map { it.src },
                onMenuClick = onMenuClick
            )
        }

        DynamicItem.DYNAMIC_TYPE_COMMON_SQUARE -> {
            val common = item.modules.moduleDynamic.major!!.common!!
            val desc = item.modules.moduleDynamic.desc?.text ?: ""
            CommonItem(
                face = author.face,
                ownerName = author.name,
                date = author.pubTs.toTimeAgoString(),
                desc = desc,
                descSimple = common.desc,
                cover = common.cover,
                title = common.title,
                onMenuClick = onMenuClick,
            )
        }

        DynamicItem.DYNAMIC_TYPE_ARTICLE -> {
            val article = item.modules.moduleDynamic.major!!.article!!
            DRAWItem(
                face = author.face,
                ownerName = author.name,
                date = author.pubTs.toTimeAgoString(),
                desc = article.desc,
                images = article.covers,
                onMenuClick = onMenuClick
            )
        }

        DynamicItem.DYNAMIC_TYPE_LIVE_RCMD -> {
            val content = item.modules.moduleDynamic.major!!.liveRcmd!!.content.run {
                Json.decodeFromString<MajorLiveRcmdContent>(this)
            }
            CommonItem(
                face = author.face,
                ownerName = author.name,
                date = author.pubTs.toTimeAgoString(),
                desc = null,
                descSimple = "",
                cover = content.livePlayInfo.cover,
                title = content.livePlayInfo.title,
                onMenuClick = onMenuClick,
            )

        }

        DynamicItem.DYNAMIC_TYPE_FORWARD -> {
            val desc = item.modules.moduleDynamic.desc!!.text
            val topic = item.modules.moduleDynamic.topic?.name ?: ""

            TopicItem(
                face = author.face,
                ownerName = author.name,
                date = author.pubTs.toTimeAgoString(),
                desc = desc,
                topic = topic,
                onMenuClick = onMenuClick,
            )
        }

        DynamicItem.DYNAMIC_TYPE_WORD -> {
            val desc = item.modules.moduleDynamic.desc!!.text
            TopicItem(
                face = author.face,
                ownerName = author.name,
                date = author.pubTs.toTimeAgoString(),
                desc = desc,
                topic = null,
                onMenuClick = onMenuClick,
            )
        }

        else -> {
            Row {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(author.face)
                        .crossfade(true)
                        .build(),
                    contentDescription = author.name,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier
                                .background(Color.LightGray)
                                .clip(CircleShape)
                        )
                    }
                )
                Text(text = author.name)
            }
        }
    }
}

@Composable
private fun DRAWItem(
    face: String,
    ownerName: String,
    date: String,
    desc: String,
    images: List<String>?,
    onMenuClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.background(
            MaterialTheme.colorScheme.background
        )
    )  {
        AuthorBar(
            face = face,
            ownerName = ownerName,
            date = date,
            onMenuClick = onMenuClick
        )

        ExpandedText(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(top = 18.dp)
                .padding(bottom = 12.dp),
            text = desc, style = MaterialTheme.typography.bodyMedium
        )

        images?.let { list ->
            val isSingle = list.size == 1
            if (isSingle) {
                val image = list.first()
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(image)
                        .crossfade(true)
                        .build(),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentScale = ContentScale.FillHeight,
                    loading = {
                        Box(
                            modifier = Modifier
                                .background(Color.LightGray)
                                .clip(CircleShape)
                        )
                    }
                )
            } else {
                LazyRow {
                    items(list) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(it)
                                .crossfade(true)
                                .build(),
                            contentDescription = ownerName,
                            modifier = Modifier
                                .size(260.dp)
                                .aspectRatio(1f)
                                .padding(horizontal = 12.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop,
                            loading = {
                                Box(
                                    modifier = Modifier
                                        .background(Color.LightGray)
                                        .clip(CircleShape)
                                )
                            }
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun CommonItem(
    face: String,
    ownerName: String,
    date: String,
    title: String,
    descSimple: String,
    desc: String?,
    cover: String,
    onMenuClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.background(
            MaterialTheme.colorScheme.background
        )
    ) {
        AuthorBar(
            face = face,
            ownerName = ownerName,
            date = date,
            onMenuClick = onMenuClick
        )

        desc?.let {
            ExpandedText(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 18.dp)
                    .padding(bottom = 12.dp),
                text = it,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(top = if (desc == null) 12.dp else 0.dp)
                .border(
                    1.dp,
                    Color.LightGray,
                    RoundedCornerShape(8.dp)
                )
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(cover)
                    .crossfade(true)
                    .build(),
                contentDescription = ownerName,
                modifier = Modifier
                    .size(120.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .background(Color.LightGray)
                            .clip(CircleShape)
                    )
                }
            )

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = descSimple,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }
    }
}

@Composable
private fun TopicItem(
    face: String,
    ownerName: String,
    date: String,
    topic: String?,
    desc: String,
    onMenuClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.background(
            MaterialTheme.colorScheme.background
        )
    )  {
        AuthorBar(
            face = face,
            ownerName = ownerName,
            date = date,
            onMenuClick = onMenuClick
        )

        topic?.let {
            AssistChip(
                modifier = Modifier.padding(horizontal = 12.dp),
                onClick = {},
                label = { Text(text = it) },
                colors = AssistChipDefaults.assistChipColors(
                    labelColor = MaterialTheme.colorScheme.primary,
                    leadingIconContentColor = MaterialTheme.colorScheme.primary,
                ),
                border = AssistChipDefaults.assistChipBorder(
                    enabled = true,
                    borderColor = Color.Transparent,
                    borderWidth = 0.dp
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Topic,
                        contentDescription = Icons.Outlined.Topic.name
                    )
                }
            )
        }

        ExpandedText(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(top = 18.dp)
                .padding(bottom = 12.dp),
            text = desc,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun AuthorBar(
    face: String,
    ownerName: String,
    date: String,
    onMenuClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 12.dp, end = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(face)
                .crossfade(true)
                .build(),
            contentDescription = ownerName,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier
                        .background(Color.LightGray)
                        .clip(CircleShape)
                )
            }
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = ownerName,
                maxLines = 1,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = date,
                maxLines = 1,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
        }

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.background,
            onClick = { onMenuClick?.invoke() }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = Icons.Default.MoreVert.name,
                modifier = Modifier.padding(4.dp)
            )
        }

    }
}


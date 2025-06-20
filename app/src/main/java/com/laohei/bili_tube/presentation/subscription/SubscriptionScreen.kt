package com.laohei.bili_tube.presentation.subscription

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Topic
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_sdk.module_v2.dynamic.DynamicItem
import com.laohei.bili_sdk.module_v2.dynamic.MajorLiveRcmdContent
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.PlayParam
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.app.SharedViewModel
import com.laohei.bili_tube.component.appbar.LogoTopAppBar
import com.laohei.bili_tube.component.placeholder.NoMoreData
import com.laohei.bili_tube.component.placeholder.RecommendPlaceholder
import com.laohei.bili_tube.component.text.ExpandedText
import com.laohei.bili_tube.component.text.RichText
import com.laohei.bili_tube.component.video.VideoItem
import com.laohei.bili_tube.component.video.VideoMenuSheet
import com.laohei.bili_tube.component.video.VideoSimpleInfoBar
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.utill.toTimeAgoString
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

private const val TAG = "DynamicScreen"

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    dynamicViewModel: SubscriptionViewModel = koinViewModel(),
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
            LazyVerticalStaggeredGrid(
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.background
                ),
                state = gridState,
                columns = StaggeredGridCells.Fixed(fixedCount),
                contentPadding = PaddingValues(horizontal = if (fixedCount == 1) 0.dp else 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    LogoTopAppBar(
                        searchOnClick = { navigateToRoute.invoke(Route.Search) }
                    )
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
                                when {
                                    fixedCount == 1 -> {
                                        Column(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                        ) {
                                            GetDynamicItem(
                                                item = it,
                                                isSingleLayout = true,
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

                                    else -> {
                                        Card(
                                            modifier = Modifier.padding(8.dp),
                                            elevation = CardDefaults.elevatedCardElevation(
                                                defaultElevation = 2.dp
                                            )
                                        ) {
                                            GetDynamicItem(
                                                item = it,
                                                isSingleLayout = false,
                                                navigateToRoute = navigateToRoute,
                                                onMenuClick = {
                                                    isShowMenuSheet = true
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item(span = StaggeredGridItemSpan.FullLine) {
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
    val sharedViewModel = koinInject<SharedViewModel>()
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
                    sharedViewModel.setPlayParam(
                        PlayParam.Video(
                            aid = archive.aid.toLong(),
                            bvid = archive.bvid,
                            cid = -1
                        )
                    )
                    navigateToRoute.invoke(Route.Play)
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
    @DrawableRes infoPlaceholder: Int = R.drawable.icon_loading_1_1,
    @DrawableRes infoError: Int = R.drawable.icon_loading_1_1,
    onMenuClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.background(
            MaterialTheme.colorScheme.background
        )
    ) {
        VideoSimpleInfoBar(
            face = face,
            title = ownerName,
            pubDate = date,
            placeholder = infoPlaceholder,
            error = infoError,
            trailingOnClick = { onMenuClick?.invoke() }
        )

        RichText(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(top = 18.dp)
                .padding(bottom = 12.dp),
            text = desc, style = MaterialTheme.typography.bodyMedium,
            emote = emptyMap(),
            color = MaterialTheme.colorScheme.onBackground
        )

        images?.let { list ->
            val isSingle = list.size == 1
            if (isSingle) {
                val image = list.first()
                val imageRequest = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(image)
                        .crossfade(true)
                        .placeholder(R.drawable.icon_loading_1_1)
                        .error(R.drawable.icon_loading_1_1)
                        .build()
                )
                Image(
                    painter = imageRequest,
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentScale = ContentScale.FillWidth,
                )
            } else {
                LazyRow {
                    items(list) {
                        val imageRequest = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(it)
                                .crossfade(true)
                                .placeholder(R.drawable.icon_loading_1_1)
                                .error(R.drawable.icon_loading_1_1)
                                .build()
                        )
                        Image(
                            painter = imageRequest,
                            contentDescription = ownerName,
                            modifier = Modifier
                                .size(260.dp)
                                .aspectRatio(1f)
                                .padding(horizontal = 12.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
            }

        }
    }
}

@Preview
@Composable
private fun DRAWItem1() {
    DRAWItem(
        face = "",
        ownerName = "动漫作业本",
        date = "11 小时前",
        desc = "Hello World!!!",
        images = null,
        infoError = R.drawable.bg
    )
}

@Preview
@Composable
private fun DRAWItem2() {
    DRAWItem(
        face = "",
        ownerName = "动漫作业本",
        date = "11 小时前",
        desc = "Hello World!!!",
        images = listOf(""),
        infoError = R.drawable.bg
    )
}

@Preview
@Composable
private fun DRAWItem3() {
    DRAWItem(
        face = "",
        ownerName = "动漫作业本",
        date = "11 小时前",
        desc = "Hello World!!!",
        images = listOf("", "", ""),
        infoError = R.drawable.bg
    )
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
    val context = LocalContext.current
    val coverRequest = remember(cover) {
        ImageRequest.Builder(context)
            .data(cover)
            .crossfade(true)
            .build()
    }
    Column(
        modifier = Modifier.background(
            MaterialTheme.colorScheme.background
        )
    ) {
        VideoSimpleInfoBar(
            face = face,
            title = ownerName,
            pubDate = date,
            trailingOnClick = { onMenuClick?.invoke() }
        )

        desc?.let {
            ExpandedText(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 18.dp)
                    .padding(bottom = 12.dp),
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
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
            AsyncImage(
                model = coverRequest,
                contentDescription = ownerName,
                modifier = Modifier
                    .size(120.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.icon_loading),
                error = painterResource(R.drawable.icon_loading)
            )

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
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
    ) {
        VideoSimpleInfoBar(
            face = face,
            title = ownerName,
            pubDate = date,
            trailingOnClick = { onMenuClick?.invoke() }
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
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
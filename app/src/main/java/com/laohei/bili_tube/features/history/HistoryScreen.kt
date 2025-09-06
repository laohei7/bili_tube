package com.laohei.bili_tube.features.history

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.laohei.bili_sdk.model_v2.history.HistoryItem
import com.laohei.bili_tube.R
import com.laohei.bili_tube.features.history.component.HistoryMenuButton
import com.laohei.bili_tube.model.UIModel
import com.laohei.bili_tube.model.play.PlayParam
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.component.state.LoadingStatePlaceholder
import com.laohei.bili_tube.ui.component.video.HorizontalVideoCard
import com.laohei.bili_tube.ui.theme.PaddingLg
import com.laohei.bili_tube.ui.theme.PaddingMd
import com.laohei.bili_tube.ui.theme.PaddingNone
import com.laohei.bili_tube.ui.theme.PaddingSm
import com.laohei.bili_tube.ui.viewmodel.SharedViewModel
import com.laohei.bili_tube.util.formatAs
import com.laohei.bili_tube.util.toFriendlyDateString
import com.laohei.bili_tube.util.toTimeString
import com.laohei.compose.uicore.ActionButtonListItem
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val TAG = "HistoryScreen"
private const val DBG = true

private val HistoryGridState = LazyGridState()

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel = koinViewModel(),
    navigateToAppRoute: (AppRoute) -> Unit,
    upPress: () -> Unit
) {
    val histories = historyViewModel.histories.collectAsLazyPagingItems()
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val isLoading = histories.loadState.refresh is LoadState.Loading
    val refreshState = rememberPullToRefreshState()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HistoryTopBar(
                scrollBehavior = scrollBehavior,
                upPress = upPress,
                onHistoryAction = historyViewModel::onHistoryAction
            )
        }
    ) { innerPadding ->
        AdaptiveLayout { uiType, width, height ->
            val fixedCount = when {
                maxWidth < 500.dp -> 1
                maxWidth >= 500.dp && maxWidth < 1280.dp -> 2
                else -> 3
            }
            PullToRefreshBox(
                isRefreshing = isLoading,
                state = refreshState,
                onRefresh = { histories.refresh() },
                indicator = {
                    Indicator(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = PaddingLg * 2),
                        isRefreshing = isLoading,
                        state = refreshState,
                    )
                }
            ) {
                HistoryList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(color = MaterialTheme.colorScheme.background),
                    gridState = HistoryGridState,
                    fixedCount = fixedCount,
                    histories = histories,
                    navigateToAppRoute = navigateToAppRoute,
                    onHistoryAction = historyViewModel::onHistoryAction
                )
            }
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
private fun HistoryList(
    modifier: Modifier = Modifier,
    gridState: LazyGridState,
    fixedCount: Int,
    histories: LazyPagingItems<UIModel<out Any?>>,
    navigateToAppRoute: (AppRoute) -> Unit,
    onHistoryAction: (HistoryAction) -> Unit
) {
    val sharedViewModel = koinInject<SharedViewModel>()
    var isOpenBvid by remember { mutableStateOf<String?>(null) }

    fun resetOpenBvid() {
        isOpenBvid = null
    }

    LaunchedEffect(gridState.isScrollInProgress, histories.loadState.refresh) {
        if (isOpenBvid != null) {
            resetOpenBvid()
        }
    }

    LazyVerticalGrid(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (isOpenBvid != null) {
                    resetOpenBvid()
                }
            },
        state = gridState,
        columns = GridCells.Fixed(fixedCount),
        contentPadding = PaddingValues(horizontal = if (fixedCount == 1) PaddingNone else PaddingSm),
        horizontalArrangement = Arrangement.spacedBy(PaddingMd),
        verticalArrangement = Arrangement.spacedBy(PaddingLg)
    ) {
        items(
            histories.itemCount,
            span = { index ->
                histories[index]?.let { item ->
                    when (item) {
                        is UIModel.Header<*> -> {
                            GridItemSpan(fixedCount)
                        }

                        else -> {
                            GridItemSpan(1)
                        }
                    }
                } ?: GridItemSpan(1)
            },
            key = {
                when (val item = histories[it]) {
                    is UIModel.Item<*> -> (item.item as HistoryItem).kid
                    else -> Uuid.random().toString()
                }
            }
        ) { index ->
            histories[index]?.let { item ->
                GetHistoryItem(
                    item = item,
                    sharedViewModel = sharedViewModel,
                    isOpenBvid = isOpenBvid,
                    navigateToAppRoute = navigateToAppRoute,
                    onOpenBvidChange = { isOpenBvid = it },
                    resetOpenBvid = ::resetOpenBvid,
                    onHistoryAction = onHistoryAction
                )
            }
        }
        item(span = { GridItemSpan(fixedCount) }) {
            LoadingStatePlaceholder(histories.loadState.append)
        }
    }
}

@Composable
private fun GetHistoryItem(
    sharedViewModel: SharedViewModel,
    item: UIModel<out Any?>,
    isOpenBvid: String?,
    navigateToAppRoute: (AppRoute) -> Unit,
    onOpenBvidChange: (String?) -> Unit,
    resetOpenBvid: () -> Unit,
    onHistoryAction: (HistoryAction) -> Unit
) {
    val latestIsOpenBvid by rememberUpdatedState(isOpenBvid)
    when (item) {
        is UIModel.Header<*> -> {
            if (item.header == null) {
                return
            }
            ListItem(
                headlineContent = {
                    Text(
                        text = item.header as String,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

        is UIModel.Item<*> -> {
            val historyItem = item.item as HistoryItem
            var isOpen by remember { mutableStateOf(false) }
            val progress = when {
                historyItem.duration > 0L -> (historyItem.progress.toFloat() / historyItem.duration)
                    .coerceIn(0f, 1f)

                else -> 0f
            }
            ActionButtonListItem(
                isOpen = latestIsOpenBvid == historyItem.history.bvid && isOpen,
                actionVerticalSpace = PaddingLg * 2,
                actionHorizontalSpace = PaddingMd,
                onOpenChange = {
                    onOpenBvidChange(if (it) historyItem.history.bvid else null)
                    isOpen = it
                },
                onClick = {
                    if (latestIsOpenBvid != null) {
                        resetOpenBvid()
                    } else {
                        sharedViewModel.setPlayParam(
                            PlayParam.VideoParam(
                                aid = historyItem.history.oid,
                                bvid = historyItem.history.bvid,
                                cid = historyItem.history.cid
                            )
                        )
                        navigateToAppRoute.invoke(AppRoute.Play)
                    }
                },
            ) {
                HorizontalVideoCard(
                    coverUrl = historyItem.cover,
                    title = historyItem.title,
                    ownerName = historyItem.authorName,
                    duration = historyItem.duration.toTimeString(false),
                    progress = progress,
                    viewAt = buildString {
                        append(historyItem.viewAt.toFriendlyDateString(false))
                        append(" ")
                        append(historyItem.viewAt.formatAs(false))
                    },
                    leadingIcon = null
                )
                FilledIconButton(
                    modifier = Modifier.aspectRatio(1f),
                    onClick = {
                        onHistoryAction(HistoryAction.DelHistory("${historyItem.history.business}_${historyItem.history.oid}"))
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = Icons.Rounded.Delete.name
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryTopBar(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    upPress: () -> Unit,
    onHistoryAction: (HistoryAction) -> Unit
) {
    MediumTopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(
                onClick = upPress
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = Icons.AutoMirrored.Outlined.ArrowBack.name,
                )
            }
        },
        title = { Text(text = stringResource(R.string.str_history)) },
        actions = {
            HistoryMenuButton(
                onHistoryAction = onHistoryAction
            )
        }
    )
}

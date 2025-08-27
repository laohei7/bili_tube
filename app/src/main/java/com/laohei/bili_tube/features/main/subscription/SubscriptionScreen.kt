package com.laohei.bili_tube.features.main.subscription

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.laohei.bili_sdk.module_v2.dynamic.DynamicItem
import com.laohei.bili_tube.PlayParam
import com.laohei.bili_tube.R
import com.laohei.bili_tube.SharedViewModel
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.features.main.component.LogoTopAppBar
import com.laohei.bili_tube.features.main.component.VideoMenuSheet
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.component.dialog.CreateFolderDialog
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.component.layout.DeviceConfiguration
import com.laohei.bili_tube.ui.component.placeholder.NoMoreData
import com.laohei.bili_tube.ui.component.placeholder.RecommendPlaceholder
import com.laohei.bili_tube.ui.component.sheet.FolderSheet
import com.laohei.bili_tube.ui.component.video.ArticleItem
import com.laohei.bili_tube.ui.component.video.VerticalVideoItem
import com.laohei.bili_tube.ui.theme.LargePadding
import com.laohei.bili_tube.ui.theme.NonePadding
import com.laohei.bili_tube.ui.theme.SmallPadding
import com.laohei.bili_tube.utill.toTimeAgoString
import com.laohei.bili_tube.utill.underDevelopment
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

private const val TAG = "SubscriptionScreen"
private const val DBG = true

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SubscriptionScreen(
    subscriptionViewModel: SubscriptionViewModel = koinViewModel(),
    sharedViewModel: SharedViewModel = koinInject<SharedViewModel>(),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    navigateToAppRoute: (AppRoute) -> Unit
) {
    val scope = rememberCoroutineScope()

    val subscriptionState by subscriptionViewModel.uiState.collectAsStateWithLifecycle()
    val gridState = subscriptionState.gridState
    val subscriptions = subscriptionViewModel.subscriptions.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        EventBus.events.collect { event ->
            when (event) {
                is Event.NotificationChildRefresh -> {
                    scope.launch {
                        gridState.scrollToItem(0)
                        subscriptions.refresh()
                    }
                }
            }
        }
    }

    SubscriptionContent(
        gridState = gridState,
        subscriptionState = subscriptionState,
        subscriptionViewModel = subscriptionViewModel,
        subscriptions = subscriptions,
        navigateToAppRoute = navigateToAppRoute,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        onImageClick = { index, images ->
            navigateToAppRoute(
                AppRoute.Gallery(
                    initialIndex = index,
                    imagesJson = Json.encodeToString(images)
                )
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun SubscriptionContent(
    gridState: LazyStaggeredGridState,
    subscriptionState: SubscriptionUIState,
    subscriptionViewModel: SubscriptionViewModel,
    subscriptions: LazyPagingItems<DynamicItem>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    navigateToAppRoute: (AppRoute) -> Unit,
    onImageClick: ((Int, List<Pair<String, String>>) -> Unit)? = null
) {
    val refreshState = rememberPullToRefreshState()
    val isLoading = subscriptions.loadState.refresh is LoadState.Loading
    AdaptiveLayout { uiType, width, height ->
        val fixedCount = when (uiType) {
            DeviceConfiguration.MOBILE_PORTRAIT -> 1
            DeviceConfiguration.MOBILE_LANDSCAPE,
            DeviceConfiguration.TABLE_PORTRAIT,
            DeviceConfiguration.TABLE_LANDSCAPE,
            DeviceConfiguration.DESKTOP -> {
                when {
                    maxWidth >= 500.dp && maxWidth < 800.dp -> 2
                    maxWidth >= 800.dp && maxWidth < 1280.dp -> 3
                    else -> 4
                }
            }
        }
        PullToRefreshBox(
            isRefreshing = isLoading,
            state = refreshState,
            onRefresh = { subscriptions.refresh() },
            indicator = {
                Indicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter),
                    isRefreshing = isLoading,
                    state = refreshState,
                )
            }
        ) {
            LazyVerticalStaggeredGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background),
                state = gridState,
                columns = StaggeredGridCells.Fixed(fixedCount),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = LargePadding
            ) {
                when (uiType) {
                    DeviceConfiguration.MOBILE_PORTRAIT,
                    DeviceConfiguration.TABLE_PORTRAIT -> {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            LogoTopAppBar(
                                onSearchClick = { navigateToAppRoute.invoke(AppRoute.Search) }
                            )
                        }
                    }

                    DeviceConfiguration.MOBILE_LANDSCAPE,
                    DeviceConfiguration.TABLE_LANDSCAPE,
                    DeviceConfiguration.DESKTOP -> {

                    }
                }


                subscriptionList(
                    isInitial = subscriptions.itemCount == 0,
                    isSingleLayout = fixedCount == 1,
                    subscriptions = subscriptions,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    navigateToAppRoute = navigateToAppRoute,
                    onSubscriptionAction = subscriptionViewModel::onSubscriptionAction,
                    onImageClick = onImageClick
                )

                item(span = StaggeredGridItemSpan.FullLine) {
                    NoMoreData(subscriptions.loadState.append)
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
            isShowSheet = subscriptionState.showMenuSheet,
            onDismiss = {
                subscriptionViewModel.onSubscriptionAction(
                    SubscriptionAction.MenuUIAction(false)
                )
            },
            onClick = {
                val action = when (it) {
                    R.string.str_save_playlist -> {
                        SubscriptionAction.FolderUIAction(true)
                    }

                    R.string.str_save_watch_later -> {
                        SubscriptionAction.AddToViewAction
                    }

                    else -> SubscriptionAction.NoneAction
                }
                subscriptionViewModel.onSubscriptionAction(action)
            }
        )

        FolderSheet(
            folders = subscriptionState.folders,
            isShowSheet = subscriptionState.showFolderSheet,
            onDismiss = {
                subscriptionViewModel.onSubscriptionAction(SubscriptionAction.FolderUIAction(false))
            },
            onCreateFolder = {
                subscriptionViewModel.onSubscriptionAction(
                    SubscriptionAction.FolderCreatedUIAction(true)
                )
            },
            onAddToFolder = { addAids, delAids ->
                subscriptionViewModel.onSubscriptionAction(
                    SubscriptionAction.AddToFoldersAction(addAids = addAids, delAids = delAids)
                )
            }
        )

        CreateFolderDialog(
            isVisible = subscriptionState.showAddFolder,
            value = subscriptionState.newFolderName,
            onValueChange = subscriptionViewModel::onFolderNameChange,
            onSubmit = subscriptionViewModel::addNewFolder,
            checked = subscriptionState.isPrivateFolder,
            onCheckedChange = subscriptionViewModel::onPrivateChange,
            onDismiss = {
                subscriptionViewModel.onFolderNameChange("")
                subscriptionViewModel.onSubscriptionAction(
                    SubscriptionAction.FolderCreatedUIAction(
                        false
                    )
                )
            }
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
private fun LazyStaggeredGridScope.subscriptionList(
    isInitial: Boolean,
    isSingleLayout: Boolean,
    subscriptions: LazyPagingItems<DynamicItem>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    navigateToAppRoute: (AppRoute) -> Unit,
    onSubscriptionAction: (SubscriptionAction) -> Unit,
    onImageClick: ((Int, List<Pair<String, String>>) -> Unit)? = null
) {
    when {
        isInitial -> {
            items(12) {
                RecommendPlaceholder(isSingleLayout = isSingleLayout)
            }
        }

        else -> {
            items(subscriptions.itemCount) { index ->
                subscriptions[index]?.let {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                    ) {
                        GetDynamicItem(
                            item = it,
                            isSingleLayout = isSingleLayout,
                            navigateToAppRoute = navigateToAppRoute,
                            onSubscriptionAction = onSubscriptionAction,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            onImageClick = onImageClick
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
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun GetDynamicItem(
    isSingleLayout: Boolean = true,
    item: DynamicItem,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    navigateToAppRoute: (AppRoute) -> Unit,
    onSubscriptionAction: (SubscriptionAction) -> Unit,
    onImageClick: ((Int, List<Pair<String, String>>) -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    val sharedViewModel = koinInject<SharedViewModel>()
    val author = item.modules.moduleAuthor
    val shape = when {
        isSingleLayout -> RoundedCornerShape(NonePadding)
        else -> RoundedCornerShape(SmallPadding)
    }
    when (item.type) {
        DynamicItem.DYNAMIC_TYPE_AV -> {
            val archive = item.modules.moduleDynamic.major!!.archive!!
            VerticalVideoItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape),
                coverShape = shape,
                bvid = archive.bvid,
                cover = archive.cover,
                title = archive.title,
                ownerFace = author.face,
                ownerName = author.name,
                view = archive.stat.play,
                pubDate = author.pubTs.toTimeAgoString(),
                duration = archive.durationText,
                trailingIcon = Icons.Outlined.MoreVert,
                onClick = {
                    sharedViewModel.setPlayParam(
                        PlayParam.VideoParam(
                            aid = archive.aid.toLong(),
                            bvid = archive.bvid,
                            cid = -1,
                        )
                    )
                    navigateToAppRoute(AppRoute.Play)
                },
                onTrailingClick = {
                    onSubscriptionAction(
                        SubscriptionAction.MenuUIAction(
                            true,
                            aid = archive.aid.toLong(),
                            bvid = archive.bvid
                        )
                    )
                }
            )
        }

        DynamicItem.DYNAMIC_TYPE_DRAW -> {
            val draw = item.modules.moduleDynamic.major?.draw
            val desc = item.modules.moduleDynamic.desc?.text ?: ""
            ArticleItem(
                articleKey = draw?.id.toString(),
                face = author.face,
                ownerName = author.name,
                date = author.pubTs.toTimeAgoString(),
                desc = desc,
                images = draw?.items?.map { it.src },
                shape = shape,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                onImageClick = onImageClick,
                onTrailingClick = {
                    underDevelopment(scope)
                }
            )
        }

        DynamicItem.DYNAMIC_TYPE_ARTICLE -> {
            item.modules.moduleDynamic.major?.article?.let { article ->
                ArticleItem(
                    articleKey = article.id.toString(),
                    face = author.face,
                    ownerName = author.name,
                    date = author.pubTs.toTimeAgoString(),
                    desc = article.desc,
                    images = article.covers,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onImageClick = onImageClick,
                    onTrailingClick = {
//                    onSubscriptionAction(SubscriptionAction.MenuUIAction(true))
                        underDevelopment(scope)
                    }
                )
            }
        }

        else -> {

        }
    }
}
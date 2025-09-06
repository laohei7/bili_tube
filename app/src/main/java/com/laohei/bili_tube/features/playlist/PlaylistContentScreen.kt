package com.laohei.bili_tube.features.playlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.laohei.bili_tube.features.playlist.component.FolderMediaList
import com.laohei.bili_tube.features.playlist.component.PlaylistContentTopBar
import com.laohei.bili_tube.features.playlist.component.WatchLaterList
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.component.widget.ShareSheet
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistContentScreen(
    param: AppRoute.PlaylistContent,
    upPress: () -> Unit,
    navigateToAppRoute: (AppRoute) -> Unit
) {
    val viewModel = koinViewModel<PlaylistContentViewModel> {
        parametersOf(param)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            PlaylistContentTopBar(
                upPress = upPress,
                isToView = uiState.param.isToView,
                onPlaylistContentAction = viewModel::onPlaylistContentAction
            )
        }
    ) { innerPadding ->
        when {
            param.isToView -> {
                WatchLaterList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    param = uiState.param,
                    gridState = uiState.watchLaterListState,
                    watchLaterList = uiState.watchLaterList,
                    navigateToAppRoute = navigateToAppRoute,
                    onPlaylistContentAction = viewModel::onPlaylistContentAction
                )
            }

            else -> {
                val folderMediaList = uiState.folderMediaFlow.collectAsLazyPagingItems()
                FolderMediaList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    param = uiState.param,
                    gridState = uiState.folderMediaListState,
                    resources = folderMediaList,
                    navigateToAppRoute = navigateToAppRoute
                )
            }
        }

        ShareSheet(
            isSheetVisible = uiState.isShareLinkVisible,
            shareContent = uiState.shareLink,
            onDismissRequest = {
                viewModel.onPlaylistContentAction(PlaylistContentAction.ShareLink(false))
            }
        )
    }
}

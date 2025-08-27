package com.laohei.bili_tube.features.main.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.FACE_URL_KEY
import com.laohei.bili_tube.core.USERNAME_KEY
import com.laohei.bili_tube.features.main.profile.component.FolderList
import com.laohei.bili_tube.features.main.profile.component.OtherMenuList
import com.laohei.bili_tube.features.main.profile.component.ProfileTopBar
import com.laohei.bili_tube.features.main.profile.component.ShortHistoryList
import com.laohei.bili_tube.features.main.profile.component.UserAssistWidget
import com.laohei.bili_tube.features.main.profile.component.VIPWidget
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.component.dialog.CreateFolderDialog
import com.laohei.bili_tube.ui.component.layout.AdaptiveLayout
import com.laohei.bili_tube.ui.component.layout.DeviceConfiguration
import com.laohei.bili_tube.ui.component.text.VerticalDataText
import com.laohei.bili_tube.utill.getValue
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    navigateToAppRoute: (AppRoute) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AdaptiveLayout { uiType, width, height ->
        when (uiType) {
            DeviceConfiguration.MOBILE_PORTRAIT,
            DeviceConfiguration.TABLE_PORTRAIT -> {
                PortraitContent(
                    state = state,
                    navigateToAppRoute = navigateToAppRoute,
                    onProfileAction = viewModel::onProfileAction
                )
            }

            DeviceConfiguration.MOBILE_LANDSCAPE,
            DeviceConfiguration.TABLE_LANDSCAPE,
            DeviceConfiguration.DESKTOP -> {
                LandscapeContent(
                    state = state,
                    navigateToAppRoute = navigateToAppRoute,
                    onProfileAction = viewModel::onProfileAction
                )
            }
        }

        CreateFolderDialog(
            isVisible = state.isShowAddFolder,
            value = state.folderName,
            onValueChange = viewModel::onFolderNameChange,
            onSubmit = viewModel::addNewFolder,
            checked = state.isPrivateFolder,
            onCheckedChange = viewModel::onPrivateChange,
            onDismiss = {
                viewModel.onFolderNameChange("")
                viewModel.onProfileAction(ProfileAction.FolderCreatedUIAction(false))
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PortraitContent(
    state: ProfileUIState,
    navigateToAppRoute: (AppRoute) -> Unit,
    onProfileAction: (ProfileAction) -> Unit
) {
    val refreshState = rememberPullToRefreshState()
    Scaffold(
        topBar = {
            ProfileTopBar(navigateToAppRoute = navigateToAppRoute)
        }
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            isRefreshing = state.isRefreshing,
            state = refreshState,
            onRefresh = {
                onProfileAction(ProfileAction.RefreshAction)
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        state = rememberScrollState()
                    )
            ) {
                PortraitUserInfo(
                    following = state.following,
                    follower = state.follower,
                    dynamicCount = state.dynamicCount
                )

                ShortHistoryList(
                    histories = state.historyList,
                    navigateToAppRoute = navigateToAppRoute
                )
                Spacer(Modifier.height(12.dp))
                FolderList(
                    watchLaterList = state.watchlist,
                    watchLaterCount = state.watchLaterCount,
                    folderList = state.folderList,
                    navigateToAppRoute = navigateToAppRoute,
                    showCreatedFolder = {
                        onProfileAction(ProfileAction.FolderCreatedUIAction(true))
                    }
                )
                Spacer(Modifier.height(12.dp))
                OtherMenuList(navigateToAppRoute = navigateToAppRoute)

                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun PortraitUserInfo(
    following: Int,
    follower: Int,
    dynamicCount: Int
) {
    val context = LocalContext.current
    val avatar by remember { mutableStateOf(context.getValue(FACE_URL_KEY.name, "")) }
    val username by remember { mutableStateOf(context.getValue(USERNAME_KEY.name, "")) }
    val avatarPainter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(avatar)
            .crossfade(true)
            .placeholder(R.drawable.icon_loading_1_1)
            .error(R.drawable.icon_loading_1_1)
            .build()
    )
    ListItem(
        leadingContent = {
            Image(
                painter = avatarPainter,
                contentDescription = "avatar",
                modifier = Modifier
                    .size(72.dp)
                    .aspectRatio(1f)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        },
        headlineContent = {
            Text(
                text = username, style = MaterialTheme.typography.titleLarge
                    .copy(fontSize = 26.sp),
                fontWeight = FontWeight.Bold
            )
        },
        supportingContent = { VIPWidget() }
    )

    UserAssistWidget()

    ListItem(
        headlineContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                VerticalDataText(dynamicCount.toLong(), stringResource(R.string.str_dynamic))
                VerticalDataText(following.toLong(), stringResource(R.string.str_following))
                VerticalDataText(follower.toLong(), stringResource(R.string.str_follower))
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LandscapeContent(
    state: ProfileUIState,
    navigateToAppRoute: (AppRoute) -> Unit,
    onProfileAction: (ProfileAction) -> Unit
) {
    val refreshState = rememberPullToRefreshState()
    Scaffold{ innerPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            isRefreshing = state.isRefreshing,
            state = refreshState,
            onRefresh = {
                onProfileAction(ProfileAction.RefreshAction)
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        state = rememberScrollState()
                    )
            ) {
                LandscapeUserInfo(
                    following = state.following,
                    follower = state.follower,
                    dynamicCount = state.dynamicCount
                )

                ShortHistoryList(
                    histories = state.historyList,
                    navigateToAppRoute = navigateToAppRoute
                )
                Spacer(Modifier.height(12.dp))
                FolderList(
                    watchLaterList = state.watchlist,
                    watchLaterCount = state.watchLaterCount,
                    folderList = state.folderList,
                    navigateToAppRoute = navigateToAppRoute,
                    showCreatedFolder = {
                        onProfileAction(ProfileAction.FolderCreatedUIAction(true))
                    }
                )
                Spacer(Modifier.height(12.dp))
                OtherMenuList(navigateToAppRoute = navigateToAppRoute)

                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun LandscapeUserInfo(
    following: Int,
    follower: Int,
    dynamicCount: Int
) {
    val context = LocalContext.current
    val avatar by remember { mutableStateOf(context.getValue(FACE_URL_KEY.name, "")) }
    val username by remember { mutableStateOf(context.getValue(USERNAME_KEY.name, "")) }
    val avatarPainter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(avatar)
            .crossfade(true)
            .placeholder(R.drawable.icon_loading_1_1)
            .error(R.drawable.icon_loading_1_1)
            .build()
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        ListItem(
            modifier = Modifier.weight(1f),
            leadingContent = {
                Image(
                    painter = avatarPainter,
                    contentDescription = "avatar",
                    modifier = Modifier
                        .size(72.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            },
            headlineContent = {
                Text(
                    text = username, style = MaterialTheme.typography.titleLarge
                        .copy(fontSize = 26.sp),
                    fontWeight = FontWeight.Bold
                )
            },
            supportingContent = { VIPWidget() }
        )

        ListItem(
            modifier = Modifier.weight(1f),
            headlineContent = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    VerticalDataText(dynamicCount.toLong(), stringResource(R.string.str_dynamic))
                    VerticalDataText(following.toLong(), stringResource(R.string.str_following))
                    VerticalDataText(follower.toLong(), stringResource(R.string.str_follower))
                }
            },
        )
    }

    UserAssistWidget()
}
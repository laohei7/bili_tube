package com.laohei.bili_tube.presentation.download

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Downloading
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.PauseCircleOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.model.DownloadStatus
import com.laohei.bili_tube.model.DownloadTask
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private sealed interface DownloadAction {
    data object UpPressAction : DownloadAction
    data class NavigateAction(val route: Route) : DownloadAction
    data object SettingsAction : DownloadAction
    data class PauseDownloadAction(val task: DownloadTask) : DownloadAction
    data class StartDownloadAction(val task: DownloadTask) : DownloadAction
    data class DeleteTaskAction(val task: DownloadTask) : DownloadAction
    data class ShowSnackbarAction(val message: String) : DownloadAction
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun DownloadScreen(
    viewModel: DownloadViewModel = koinViewModel(),
    navigateToRoute: (Route) -> Unit,
    upPress: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val downloadQueue by viewModel.downloadQueue.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            DownloadTopAppBar {
                when (it) {
                    DownloadAction.SettingsAction -> {

                    }

                    DownloadAction.UpPressAction -> upPress.invoke()
                    else -> {}
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val fixedCount = when {
                maxWidth < 500.dp -> 1
                maxWidth >= 500.dp && maxWidth < 1280.dp -> 2
                else -> 4
            }
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize(),
                columns = GridCells.Fixed(fixedCount),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(downloadQueue) {
                    DownloadItem(it) { action ->
                        when (action) {
                            is DownloadAction.NavigateAction -> navigateToRoute.invoke(action.route)
                            is DownloadAction.PauseDownloadAction -> viewModel.pauseTask(action.task)
                            is DownloadAction.StartDownloadAction -> viewModel.startTask(action.task)
                            is DownloadAction.ShowSnackbarAction -> {
                                scope.launch { snackbarHostState.showSnackbar(action.message) }
                            }

                            is DownloadAction.DeleteTaskAction -> viewModel.deleteTask(action.task)
                            else -> {}
                        }
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DownloadTopAppBar(
    onClick: (DownloadAction) -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.str_download_management)) },
        navigationIcon = {
            IconButton(onClick = { onClick.invoke(DownloadAction.UpPressAction) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = Icons.AutoMirrored.Outlined.ArrowBack.name,
                )
            }
        },
        actions = {
            IconButton(onClick = { onClick.invoke(DownloadAction.SettingsAction) }) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = Icons.Outlined.Settings.name,
                )
            }
        }
    )
}

@Composable
private fun DownloadItem(task: DownloadTask, onClick: (DownloadAction) -> Unit) {
    val context = LocalContext.current
    val coverRequest = remember(task.id) {
        ImageRequest.Builder(context)
            .data(task.cover)
            .crossfade(true)
            .build()
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                val action = when (task.status) {
                    DownloadStatus.PAUSE,
                    DownloadStatus.FAILED,
                    DownloadStatus.PENDING -> {
                        DownloadAction.StartDownloadAction(task)
                    }

                    DownloadStatus.PROCESSING -> {
                        DownloadAction.ShowSnackbarAction(context.getString(R.string.str_processing_hint))
                    }

                    DownloadStatus.DOWNLOADING -> {
                        DownloadAction.PauseDownloadAction(task)
                    }

                    DownloadStatus.COMPLETED -> {
                        DownloadAction.NavigateAction(
                            Route.Play(
                                aid = task.aid,
                                bvid = task.id,
                                cid = task.cid,
                                isLocal = true
                            )
                        )
                    }
                }
                onClick.invoke(action)
            }
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = coverRequest,
            contentDescription = task.name,
            modifier = Modifier
                .weight(1.3f)
                .aspectRatio(16 / 9f)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.icon_loading),
            error = painterResource(R.drawable.icon_loading)
        )

        Column(
            modifier = Modifier.weight(2f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = task.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            when (task.status) {
                DownloadStatus.PENDING,
                DownloadStatus.PROCESSING -> {
                    LinearProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                DownloadStatus.PAUSE,
                DownloadStatus.DOWNLOADING,
                DownloadStatus.COMPLETED -> {
                    LinearProgressIndicator(
                        progress = { task.progress / 100f },
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                DownloadStatus.FAILED -> {
                    LinearProgressIndicator(
                        progress = { 0f },
                        trackColor = MaterialTheme.colorScheme.error,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val triple = when (task.status) {
                    DownloadStatus.DOWNLOADING -> {
                        Triple(
                            stringResource(R.string.str_download_progress, task.progress),
                            Icons.Outlined.Downloading,
                            MaterialTheme.colorScheme.primary
                        )
                    }

                    DownloadStatus.PROCESSING -> {
                        Triple(
                            stringResource(R.string.str_processing),
                            Icons.Outlined.Downloading,
                            MaterialTheme.colorScheme.primary
                        )
                    }

                    DownloadStatus.PENDING -> {
                        Triple(
                            stringResource(R.string.str_pending),
                            Icons.Outlined.Downloading,
                            MaterialTheme.colorScheme.primary
                        )
                    }

                    DownloadStatus.COMPLETED -> {
                        Triple(
                            stringResource(R.string.str_download_completed),
                            Icons.Outlined.Check,
                            MaterialTheme.colorScheme.secondary
                        )
                    }

                    DownloadStatus.FAILED -> {
                        Triple(
                            stringResource(R.string.str_download_failed),
                            Icons.Outlined.ErrorOutline,
                            MaterialTheme.colorScheme.error
                        )
                    }

                    DownloadStatus.PAUSE -> {
                        Triple(
                            stringResource(R.string.str_download_pause),
                            Icons.Outlined.PauseCircleOutline,
                            MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    modifier = Modifier.weight(1f),
                    text = triple.first,
                    style = MaterialTheme.typography.labelSmall,
                    color = triple.third
                )

                Icon(
                    imageVector = triple.second,
                    contentDescription = triple.second.name,
                    modifier = Modifier.size(16.dp),
                    tint = triple.third
                )
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = Icons.Outlined.DeleteOutline.name,
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .clickable { onClick.invoke(DownloadAction.DeleteTaskAction(task)) },
                    tint = Color.Red
                )
            }
        }
    }
}
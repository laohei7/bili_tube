package com.laohei.bili_tube.presentation.download

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.laohei.bili_tube.model.DownloadStatus
import com.laohei.bili_tube.model.DownloadTask
import org.koin.androidx.compose.koinViewModel

private sealed interface DownloadAction {
    data object UpPressAction : DownloadAction
    data object SettingsAction : DownloadAction
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun DownloadScreen(
    viewModel: DownloadViewModel = koinViewModel(),
    upPress: () -> Unit
) {
    val downloadQueue by viewModel.downloadQueue.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            DownloadTopAppBar {
                when (it) {
                    DownloadAction.SettingsAction -> {

                    }

                    DownloadAction.UpPressAction -> upPress.invoke()
                }
            }
        }
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
                    DownloadItem(it)
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
        title = { Text(text = stringResource(R.string.str_downlaod_management)) },
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
private fun DownloadItem(task: DownloadTask) {
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
                overflow = TextOverflow.Ellipsis
            )

            when (task.status) {
                DownloadStatus.PROCESSING -> {
                    LinearProgressIndicator()
                    Text(
                        text = stringResource(R.string.str_processing),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }

                DownloadStatus.PENDING -> {
                    LinearProgressIndicator()
                    Text(
                        text = stringResource(R.string.str_pending),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }

                DownloadStatus.COMPLETED -> {
                    LinearProgressIndicator(progress = { 1f })
                    Text(
                        text = stringResource(R.string.str_download_completed),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Red
                    )
                }

                DownloadStatus.DOWNLOADING -> {
                    LinearProgressIndicator(progress = { task.progress / 100f })
                    Text(
                        text = stringResource(R.string.str_download_progress, task.progress),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }

                DownloadStatus.FAILED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.str_download_failed),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Red
                        )

                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Outlined.Circle,
                                contentDescription = Icons.Outlined.Circle.name
                            )
                        }
                    }
                }
            }
        }
    }
}
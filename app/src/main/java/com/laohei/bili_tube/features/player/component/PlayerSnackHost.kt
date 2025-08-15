package com.laohei.bili_tube.features.player.component

import android.content.Context
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus

@Composable
fun PlayerSnackHost(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        EventBus.events.collect { event ->
            if ((event is Event.VideoPlayerEvent).not()) {
                return@collect
            }
            when (val playerEvent = event) {
                is Event.VideoPlayerEvent.SnackbarEvent -> {
                    snackbarHostState.showSnackbar(
                        message = playerEvent.message
                    )
                }

                is Event.VideoPlayerEvent.SnackbarEventById -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(playerEvent.messageId)
                    )
                }

                else -> {}
            }
        }
    }

    SnackbarHost(
        modifier = modifier,
        hostState = snackbarHostState,
    )
}
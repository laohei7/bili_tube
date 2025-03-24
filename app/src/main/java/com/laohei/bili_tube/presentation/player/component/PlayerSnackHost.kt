package com.laohei.bili_tube.presentation.player.component

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus

@Composable
fun PlayerSnackHost(
    modifier: Modifier=Modifier
){
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        EventBus.events.collect { event ->
            if((event is Event.PlayerEvent).not()){
                return@collect
            }
            when(val playerEvent = (event as Event.PlayerEvent)){
                is Event.PlayerEvent.SnackbarEvent -> {
                    snackbarHostState.showSnackbar(
                        message = playerEvent.message
                    )
                }
            }
        }
    }

    SnackbarHost(
        modifier = modifier,
        hostState = snackbarHostState,
    )
}
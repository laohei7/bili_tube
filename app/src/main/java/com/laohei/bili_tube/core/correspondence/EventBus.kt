package com.laohei.bili_tube.core.correspondence

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object EventBus {
    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    suspend fun send(event: Event) {
        _events.emit(event)
    }
}

interface Event {
    data object NotificationChildRefresh : Event

    sealed interface PlayerEvent : Event {
        data class SnackbarEvent(
            val message: String,
            val actionType: Int = NONE_ACTION
        ) : PlayerEvent {
            companion object {
                const val NONE_ACTION = 0
                const val FOLDER_ACTION = 1
            }
        }
    }

    sealed interface AppEvent : Event {
        data class ToastEvent(val message: String) : AppEvent
    }
}
package com.laohei.bili_tube.core.correspondence

import androidx.annotation.StringRes
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

    sealed interface VideoPlayerEvent : Event {
        companion object {
            const val NONE_ACTION = 0
            const val FOLDER_ACTION = 1
        }

        data class SnackbarEvent(
            val message: String,
            val actionType: Int = NONE_ACTION
        ) : VideoPlayerEvent

        data class SnackbarEventById(
            @StringRes val messageId: Int,
            val actionType: Int = NONE_ACTION
        ) : VideoPlayerEvent
    }

    sealed interface AppEvent : Event {
        data class ToastTextEvent(val message: String) : AppEvent
        data class ToastEvent(@StringRes val messageId: Int) : AppEvent
        data class PermissionRequestEvent(val permissions: List<String>) : AppEvent
    }
}
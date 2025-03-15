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

sealed interface Event {
    data object NotificationChildRefresh : Event
}
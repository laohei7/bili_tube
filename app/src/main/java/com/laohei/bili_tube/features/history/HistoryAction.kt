package com.laohei.bili_tube.features.history

sealed interface HistoryAction {
    data class DelHistory(val kid: String) : HistoryAction
    data object ClearHistory : HistoryAction
}
package com.laohei.bili_tube.features.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.data.repository.BiliHistoryRepository
import com.laohei.bili_tube.model.UIModel
import com.laohei.bili_tube.util.toFriendlyDateString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val biliHistoryRepository: BiliHistoryRepository
) : ViewModel() {

    companion object {
        private val TAG = HistoryViewModel::class.simpleName
        private const val DBG = false
    }

    private val refreshTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val histories = refreshTrigger.flatMapLatest {
        biliHistoryRepository.getHistoryList()
            .map { pagingData ->
                pagingData.map { UIModel.Item(it) }
                    .insertSeparators { before, after ->
                        val beforeDate = before?.item?.viewAt?.toFriendlyDateString(false)
                        val afterDate = after?.item?.viewAt?.toFriendlyDateString(false)

                        if (DBG) {
                            Log.d(TAG, "before: $before")
                            Log.d(TAG, "after: $after")
                        }

                        return@insertSeparators when {
                            beforeDate == null && afterDate != null -> UIModel.Header(afterDate)
                            beforeDate != afterDate -> UIModel.Header(afterDate)
                            else -> null
                        }
                    }
            }
    }.cachedIn(viewModelScope)

    fun onHistoryAction(action: HistoryAction) {
        when (action) {
            is HistoryAction.DelHistory -> delHistory(action)
            is HistoryAction.ClearHistory -> clearHistory()
        }
    }

    private fun clearHistory() {
        viewModelScope.launch {
            val response = biliHistoryRepository.clearHistory()
            if (response.code != 0) {
                EventBus.send(Event.AppEvent.ToastTextEvent(message = response.message))
                return@launch
            }
            EventBus.send(Event.AppEvent.ToastEvent(R.string.str_clear_success))
            refreshTrigger.value++
        }
    }

    private fun delHistory(action: HistoryAction.DelHistory) {
        viewModelScope.launch {
            val response = biliHistoryRepository.delHistory(action.kid)
            if (response.code != 0) {
                EventBus.send(Event.AppEvent.ToastTextEvent(message = response.message))
                return@launch
            }
            EventBus.send(Event.AppEvent.ToastEvent(R.string.str_delete_success))
            refreshTrigger.value++
        }
    }
}
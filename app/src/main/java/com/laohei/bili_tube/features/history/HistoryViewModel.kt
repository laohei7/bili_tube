package com.laohei.bili_tube.features.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.laohei.bili_tube.model.UIModel
import com.laohei.bili_tube.data.repository.BiliHistoryRepository
import com.laohei.bili_tube.util.toFriendlyDateString
import kotlinx.coroutines.flow.map

class HistoryViewModel(
    biliHistoryRepository: BiliHistoryRepository
) : ViewModel() {

    companion object {
        private val TAG = HistoryViewModel::class.simpleName
        private const val DBG = false
    }

    val histories = biliHistoryRepository.getHistoryList()
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
        .cachedIn(viewModelScope)

}
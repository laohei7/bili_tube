package com.laohei.bili_tube.presentation.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.laohei.bili_tube.model.UIModel
import com.laohei.bili_tube.repository.BiliHistoryRepository
import com.laohei.bili_tube.utill.toTimeAgoString2
import kotlinx.coroutines.flow.map

class HistoryViewModel(
    biliHistoryRepository: BiliHistoryRepository
) : ViewModel() {

    val histories = biliHistoryRepository.getHistoryList()
        .map { pagingData ->
            pagingData.map { UIModel.Item(it) }
                .insertSeparators { before, after ->

                    val beforeDate = before?.item?.viewAt?.toTimeAgoString2(false)
                    val afterDate = after?.item?.viewAt?.toTimeAgoString2(false)

                    Log.d("FilterA", ": $before $after")

                    return@insertSeparators when {
                        beforeDate == null && afterDate != null-> UIModel.Header(afterDate)
                        beforeDate != afterDate -> UIModel.Header(afterDate)
                        else -> null
                    }
                }
        }
        .cachedIn(viewModelScope)

}
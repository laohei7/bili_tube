package com.laohei.bili_tube.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.laohei.bili_sdk.apis.HistoryApi
import com.laohei.bili_sdk.module_v2.history.HistoryItem
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.dataStore
import com.laohei.bili_tube.presentation.history.HistoryPaging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow

class BiliHistoryRepository(
    private val context: Context,
    private val historyApi: HistoryApi
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getHistoryList(): Flow<PagingData<HistoryItem>> {
        return flow {
            val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
            emit(
                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = { HistoryPaging(historyApi, cookie) }
                ).flow
            )
        }.flattenConcat()
    }
}
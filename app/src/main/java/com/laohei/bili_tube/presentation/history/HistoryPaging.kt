package com.laohei.bili_tube.presentation.history

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.laohei.bili_sdk.history.History
import com.laohei.bili_sdk.module_v2.history.HistoryCursor
import com.laohei.bili_sdk.module_v2.history.HistoryItem

class HistoryPaging(
    private val history: History,
    private val cookie: String?
) : PagingSource<Int, HistoryItem>() {
    private val _mHistoryCursorList = mutableListOf<HistoryCursor?>()
    override fun getRefreshKey(state: PagingState<Int, HistoryItem>): Int? {
        val index = state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
        return if(index!=null && index<0) null else index
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HistoryItem> {
        return try {
            val key = params.key
            if(key == null) {
                _mHistoryCursorList.clear()
            }
            val cursor = key?.run { _mHistoryCursorList[this] }

            val response = history.historyList(
                cookie = cookie,
                ps = cursor?.ps ?: 20,
                max = cursor?.max,
                business = cursor?.business,
                viewAt = cursor?.viewAt
            )

            val data = response?.data?.list ?: emptyList()
            val newCursor = response?.data?.cursor
            _mHistoryCursorList.add(newCursor)

            LoadResult.Page(
                data = data,
                prevKey = if (key == null || key == 0) null else key - 1,
                nextKey = if (data.isEmpty()) null else if (newCursor != null && key ==null) 0 else (key?:0) + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
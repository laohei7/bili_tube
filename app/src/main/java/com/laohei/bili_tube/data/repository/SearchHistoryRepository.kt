package com.laohei.bili_tube.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.laohei.bili_tube.data.local.room.dao.SearchHistoryDao
import com.laohei.bili_tube.data.local.room.entity.SearchHistory
import kotlinx.coroutines.flow.Flow

class SearchHistoryRepository(
    private val searchHistoryDao: SearchHistoryDao
) {
    fun searchHistoryPager(): Flow<PagingData<SearchHistory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { searchHistoryDao.getHistoryPaging() }
        ).flow
    }

    suspend fun addSearchHistoryItem(item: SearchHistory) {
        val exist = searchHistoryDao.findByKeyword(item.keyword)
        exist?.let { searchHistoryDao.update(it.copy(timestamp = item.timestamp)) }
            ?: run { searchHistoryDao.insert(item) }
    }
}
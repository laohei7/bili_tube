package com.laohei.bili_tube.data.local.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.laohei.bili_tube.data.local.room.entity.SearchHistory

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    fun getHistoryPaging(): PagingSource<Int, SearchHistory>

    @Query("SELECT * FROM search_history WHERE keyword = :keyword LIMIT 1")
    suspend fun findByKeyword(keyword: String): SearchHistory?

    @Insert
    suspend fun insert(history: SearchHistory)

    @Update
    suspend fun update(history: SearchHistory)

    @Query("DELETE FROM search_history WHERE keyword = :keyword")
    suspend fun delete(keyword: String)

    @Query("DELETE FROM search_history")
    suspend fun clearAll()
}
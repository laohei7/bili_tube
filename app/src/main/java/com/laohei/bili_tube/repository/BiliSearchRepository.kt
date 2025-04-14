package com.laohei.bili_tube.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.laohei.bili_sdk.module_v2.search.SearchResultItemType
import com.laohei.bili_sdk.search.SearchRequest
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.dataStore
import com.laohei.bili_tube.presentation.search.SearchPaging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow

class BiliSearchRepository(
    private val context: Context,
    private val searchRequest: SearchRequest
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun onSearch(
        keyword: String,
        type: SearchRequest.Companion.SearchType
    ): Flow<PagingData<SearchResultItemType>> {
        return flow {
            val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
            emit(
                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = {
                        SearchPaging(
                            searchRequest = searchRequest,
                            cookie = cookie,
                            keyword = keyword,
                            type = type
                        )
                    }
                ).flow
            )
        }.flattenConcat()
    }

}
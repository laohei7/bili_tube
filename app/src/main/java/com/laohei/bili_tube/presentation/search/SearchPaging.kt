package com.laohei.bili_tube.presentation.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.laohei.bili_sdk.module_v2.search.SearchResultItemType
import com.laohei.bili_sdk.module_v2.search.SearchResultModel
import com.laohei.bili_sdk.module_v2.search.SearchResultModel2
import com.laohei.bili_sdk.search.SearchRequest

class SearchPaging(
    private val searchRequest: SearchRequest,
    private val cookie: String? = null,
    private val keyword: String,
    private val type: SearchRequest.Companion.SearchType = SearchRequest.Companion.SearchType.All
) : PagingSource<Int, SearchResultItemType>() {

    companion object {
        private val TAG = SearchPaging::class.simpleName
        private const val DBG = true
    }

    override fun getRefreshKey(state: PagingState<Int, SearchResultItemType>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchResultItemType> {
        if (type == SearchRequest.Companion.SearchType.None) {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }

        return try {
            val page = params.key ?: 1

            val adjustedType = when {
                type == SearchRequest.Companion.SearchType.All && page > 1 -> {
                    SearchRequest.Companion.SearchType.Video
                }

                else -> type
            }
            val response = searchRequest.search(
                cookie = cookie,
                type = adjustedType,
                keyword = keyword,
                page = page
            )

            val data = response?.run {
                when (this.data) {
                    is SearchResultModel -> {
                        (this.data as SearchResultModel).result.map { it.data }.flatten()
                    }

                    is SearchResultModel2 -> {
                        (this.data as SearchResultModel2).result
                    }

                    else -> emptyList()
                }
            } ?: emptyList()

            val (preKey, nextKey) = response?.run {
                when (this.data) {
                    is SearchResultModel -> {
                        val result = this.data as SearchResultModel
                        Pair(
                            if (page == 1) null else result.page.minus(1),
                            result.next
                        )
                    }

                    is SearchResultModel2 -> {
                        val result = this.data as SearchResultModel2
                        Pair(
                            if (page == 1) null else result.page.minus(1),
                            result.next
                        )
                    }

                    else -> Pair(if (page == 1) null else page.minus(1), null)
                }
            } ?: Pair(if (page == 1) null else page.minus(1), null)

            LoadResult.Page(
                data = data,
                prevKey = preKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}
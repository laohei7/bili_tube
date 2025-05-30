package com.laohei.bili_tube.presentation.home.hot

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.laohei.bili_sdk.apis.VideoApi
import com.laohei.bili_sdk.module_v2.hot.HotItem

class HotPaging(
    private val videoApi: VideoApi,
    private val cookie: String?
) : PagingSource<Int, HotItem>() {
    override fun getRefreshKey(state: PagingState<Int, HotItem>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HotItem> {
        return try {
            val page = params.key ?: 1
            val pageSize = 20

            val response = videoApi.getHots(
                cookie = cookie,
                pn = page,
                ps = pageSize
            )

            val data = response.data.list

            LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (data.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}
package com.laohei.bili_tube.presentation.home.hot

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.laohei.bili_sdk.hot.Hots
import com.laohei.bili_sdk.model.BiliHotVideoItem

class HotPaging(
    private val hots: Hots,
    private val cookie: String?
) : PagingSource<Int, BiliHotVideoItem>() {
    override fun getRefreshKey(state: PagingState<Int, BiliHotVideoItem>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BiliHotVideoItem> {
        return try {
            val page = params.key ?: 1
            val pageSize = 20

            val response = hots.hotVideos(
                cookie = cookie,
                pn = page,
                ps = pageSize
            )

            val data = response?.data?.list ?: emptyList()

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
package com.laohei.bili_tube.presentation.home.recommend

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.laohei.bili_sdk.model.BiliRandomVideoItem
import com.laohei.bili_sdk.recommend.Recommend

class RecommendPaging(
    private val recommend: Recommend,
    private val cookie: String?
) : PagingSource<Int, BiliRandomVideoItem>() {
    override fun getRefreshKey(state: PagingState<Int, BiliRandomVideoItem>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BiliRandomVideoItem> {
        return try {
            val page = params.key ?: 1
//            val pageSize = 30

            val response = recommend.recommendVideos(cookie)

            val data = response?.data?.item ?: emptyList()

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
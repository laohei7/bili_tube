package com.laohei.bili_tube.presentation.home.anime

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.laohei.bili_sdk.apis.BangumiApi
import com.laohei.bili_sdk.module_v2.bangumi.AnimeScheduleModel

class TimelinePaging(
    private val bangumiApi: BangumiApi,
    private val cookie: String?
) : PagingSource<Int, AnimeScheduleModel>() {
    override fun getRefreshKey(state: PagingState<Int, AnimeScheduleModel>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AnimeScheduleModel> {
        return try {
            val page = params.key ?: 1
//            val pageSize = 30

            val response = bangumiApi.getTimeline(cookie = cookie)

            val data = response.result

            LoadResult.Page(
                data = data,
                prevKey = null,
                nextKey = null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}
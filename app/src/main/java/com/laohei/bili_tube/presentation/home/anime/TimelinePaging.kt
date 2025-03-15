package com.laohei.bili_tube.presentation.home.anime

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.laohei.bili_sdk.model.BiliAnimeSchedule
import com.laohei.bili_sdk.anime.Timeline

class TimelinePaging(
    private val timeline: Timeline,
    private val cookie: String?
) : PagingSource<Int, BiliAnimeSchedule>() {
    override fun getRefreshKey(state: PagingState<Int, BiliAnimeSchedule>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BiliAnimeSchedule> {
        return try {
            val page = params.key ?: 1
//            val pageSize = 30

            val response = timeline.timelineEpisodes(cookie = cookie)

            val data = response?.result ?: emptyList()

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
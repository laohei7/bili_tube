package com.laohei.bili_tube.presentation.home.anime

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.laohei.bili_sdk.anime.GetBangumi
import com.laohei.bili_sdk.module_v2.bangumi.BangumiItem
import com.laohei.bili_tube.model.BangumiFilterModel


class BangumiPaging(
    private val getBangumi: GetBangumi,
    private val bangumiFilterModel: BangumiFilterModel,
    private val cookie: String?
) : PagingSource<Int, BangumiItem>() {
    override fun getRefreshKey(state: PagingState<Int, BangumiItem>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BangumiItem> {
        return try {
            val page = params.key ?: 1

            val response = getBangumi.bangumis(
                page = page,
                seasonVersion = bangumiFilterModel.seasonVersion,
                spokenLanguageType = bangumiFilterModel.spokenLanguageType,
                area = bangumiFilterModel.area,
                isFinish = bangumiFilterModel.isFinish,
                copyright = bangumiFilterModel.copyright,
                seasonStatus = bangumiFilterModel.seasonStatus,
                seasonMonth = bangumiFilterModel.seasonMonth,
                styleId = bangumiFilterModel.styleId,
                cookie = cookie
            )

            val data = response?.data?.list ?: emptyList()

            LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response?.data?.hasNext == 1) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}
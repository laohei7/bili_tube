package com.laohei.bili_tube.presentation.subscription

import android.util.Log
import androidx.compose.ui.util.fastFilter
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.laohei.bili_sdk.apis.VideoApi
import com.laohei.bili_sdk.module_v2.dynamic.DynamicItem

class SubscriptionPaging(
    private val videoApi: VideoApi,
    private val cookie: String?
) : PagingSource<Pair<Int, Long?>, DynamicItem>() {

    companion object {
        private val TAG = SubscriptionPaging::class.simpleName
    }

    private val filterTypes = listOf(
        DynamicItem.DYNAMIC_TYPE_AV,
        DynamicItem.DYNAMIC_TYPE_DRAW,
        DynamicItem.DYNAMIC_TYPE_ARTICLE
    )

    override fun getRefreshKey(state: PagingState<Pair<Int, Long?>, DynamicItem>): Pair<Int, Long?>? {
        return state.anchorPosition?.let { anchor ->
            val prevKey = state.closestPageToPosition(anchor)?.prevKey
            val nextKey = state.closestPageToPosition(anchor)?.nextKey

            val refreshKey = prevKey?.copy(first = prevKey.first.plus(1))
                ?: nextKey?.copy(first = nextKey.first.minus(1))

            refreshKey?.run {
                if (this.first == 1) {
                    this.copy(
                        second = null
                    )
                } else {
                    this
                }
            }
        }
    }

    override suspend fun load(params: LoadParams<Pair<Int, Long?>>): LoadResult<Pair<Int, Long?>, DynamicItem> {
        return try {
            val (page, offset) = params.key ?: Pair(1, null)

            Log.d(TAG, "load: $page $offset")

            val response = videoApi.getDynamics(
                cookie = cookie,
                page = page,
                offset = offset
            )


            val data = response.data.items
                ?.fastFilter { it.type in filterTypes }
                ?: emptyList()

            val hasMore = response.data.hasMore == true
            val nextOffset = response.data.offset

            LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else Pair(page - 1, null),
                nextKey = if (hasMore.not()) null else Pair(page + 1, nextOffset)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}
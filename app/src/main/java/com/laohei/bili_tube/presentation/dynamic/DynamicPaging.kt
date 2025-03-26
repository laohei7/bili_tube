package com.laohei.bili_tube.presentation.dynamic

import android.util.Log
import androidx.compose.ui.util.fastForEach
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.laohei.bili_sdk.dynamic.GetWebDynamic
import com.laohei.bili_sdk.module_v2.dynamic.DynamicItem

class DynamicPaging(
    private val webDynamic: GetWebDynamic,
    private val cookie: String?
) : PagingSource<Pair<Int, Long?>, DynamicItem>() {

    companion object {
        private val TAG = DynamicPaging::class.simpleName
    }

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

            val response = webDynamic.dynamicList(
                cookie = cookie,
                page = page,
                offset = offset
            )


            val data = response?.data?.items ?: emptyList()

            val hasMore = response?.data?.hasMore == true
            val nextOffset = response?.data?.offset

            data.fastForEach {
                Log.d(TAG, "load: ${it.type}")
            }


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
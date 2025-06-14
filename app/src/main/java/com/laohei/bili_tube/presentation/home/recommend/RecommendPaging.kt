package com.laohei.bili_tube.presentation.home.recommend

import android.content.Context
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastForEachIndexed
import androidx.datastore.preferences.core.edit
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.laohei.bili_sdk.apis.VideoApi
import com.laohei.bili_sdk.module_v2.recomment.RecommendItem
import com.laohei.bili_tube.core.LAST_SHOW_LIST_KEY
import com.laohei.bili_tube.dataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random

private data class RecommendParams(
    val webLocation: Int = 1430650,
    val refreshType: Int = 4,
    val lastShowList: String? = null,
    val feedVersion: String = "V8",
    val freshIdx: Int = 1,
    val freshIdx1h: Int = 1,
    val brush: Int = 1,
    val homepageVer: Int = 1,
    val fetchRow: Int = 1,
    val ps: Int = 12,
    val yNum: Int = 3
)

class RecommendPaging(
    private val videoApi: VideoApi,
    private val context: Context,
    private val cookie: String?
) : PagingSource<Int, RecommendItem>() {

    companion object {
        private val TAG = RecommendPaging::class.simpleName
    }

    private var _mParams = RecommendParams()

    override fun getRefreshKey(state: PagingState<Int, RecommendItem>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecommendItem> {
        return try {
            val page = params.key ?: 1
            if (params.key == null) {
                _mParams = RecommendParams(
                    lastShowList = context.dataStore.data.firstOrNull()?.get(LAST_SHOW_LIST_KEY)
                        ?.run { ifBlank { null } }
                )
            }

            val response = videoApi.getRecommends(
                cookie,
                refreshType = Random.nextInt(3, 12),
                webLocation = _mParams.webLocation,
                feedVersion = _mParams.feedVersion,
                brush = _mParams.brush,
                freshIdx = _mParams.freshIdx,
                freshIdx1h = _mParams.freshIdx1h,
                lastShowList = _mParams.lastShowList,
                homepageVer = _mParams.homepageVer,
                fetchRow = _mParams.fetchRow,
                ps = _mParams.ps
            )

            val data = response.data.item.fastFilter { it.owner != null && it.stat != null }

            response.data.let {
                _mParams = _mParams.copy(
                    freshIdx = page + 1,
                    freshIdx1h = page + 1,
                    brush = page + 1,
                    fetchRow = _mParams.fetchRow + _mParams.yNum,
                    lastShowList = buildString {
                        it.item.fastForEachIndexed { index, item ->
                            append(item.goto)
                            append("_")
                            if (item.isFollowed == 1) {
                                append("n")
                                append("_")
                            }
                            append(item.id)
                            if (index < it.item.size - 1) {
                                append(",")
                            }
                        }
                    }
                )
                context.dataStore.edit { settings ->
                    settings[LAST_SHOW_LIST_KEY] = _mParams.lastShowList ?: ""
                }
            }

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
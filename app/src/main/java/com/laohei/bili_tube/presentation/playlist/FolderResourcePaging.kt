package com.laohei.bili_tube.presentation.playlist

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.laohei.bili_sdk.apis.FolderApi
import com.laohei.bili_sdk.module_v2.folder.FolderMediaItem

class FolderResourcePaging(
    private val folderApi: FolderApi,
    private val cookie: String?,
    private val mlid: Long
) : PagingSource<Int, FolderMediaItem>() {

    companion object {
        private val TAG = FolderResourcePaging::class.simpleName
        private const val DBG = true
    }

    override fun getRefreshKey(state: PagingState<Int, FolderMediaItem>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FolderMediaItem> {
        return runCatching {
            val page = params.key ?: 1
            val res = folderApi.getFolderResources(
                mlid = mlid,
                pn = page
            )
            val data = res.data.medias
            val hasMore = res.data.hasMore
            LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (hasMore) page + 1 else null
            )
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    Log.d(TAG, "load: ${it.message}")
                }
                LoadResult.Error(it)
            }
        )
    }
}
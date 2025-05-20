package com.laohei.bili_tube.presentation.user

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.laohei.bili_sdk.module_v2.user.UploadedVideoItem
import com.laohei.bili_sdk.user.GetUploadedVideo

class UserUploadedVideoPaging(
    private val getUploadedVideo: GetUploadedVideo,
    private val cookie: String?,
    private val mid: Long,
) : PagingSource<Int, UploadedVideoItem>() {

    companion object {
        private val TAG = UserUploadedVideoPaging::class.simpleName
    }

    private var aid: Long? = null

    override fun getRefreshKey(state: PagingState<Int, UploadedVideoItem>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UploadedVideoItem> {
        return try {
            val page = params.key ?: 1
            val data = getUploadedVideo.uploadedVideos(cookie = cookie, vmid = mid, aid = aid)?.data
            data?.run {
                val videos = this.items
                val hasNext = this.hasNext
                if (hasNext && videos.isNotEmpty()) {
                    aid = videos.last().aid.toLong()
                }
                LoadResult.Page(
                    data = items,
                    prevKey = if (hasPrev) page - 1 else null,
                    nextKey = if (hasNext) page + 1 else null
                )
            } ?: run {
                LoadResult.Invalid()
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
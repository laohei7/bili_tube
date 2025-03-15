package com.laohei.bili_tube.presentation.player.component.reply

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.laohei.bili_sdk.model.VideoReplyItem
import com.laohei.bili_sdk.video.VideoReply

class VideoReplyPaging(
    private val videoReply: VideoReply,
    private val cookie: String?,
    private val type: Int = 1,
    private val oid: String
) : PagingSource<Int, VideoReplyItem>() {
    override fun getRefreshKey(state: PagingState<Int, VideoReplyItem>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoReplyItem> {
        return try {
            val page = params.key ?: 1
            val pageSize = 20

            val response = videoReply.getVideoReplies(
                cookie = cookie,
                type = type,
                oid = oid,
                pn = page,
                ps = pageSize
            )

            val data = response?.data?.replies ?: emptyList()

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
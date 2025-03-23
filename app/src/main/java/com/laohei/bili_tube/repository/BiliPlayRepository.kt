package com.laohei.bili_tube.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.laohei.bili_sdk.model.VideoReplyItem
import com.laohei.bili_sdk.video.PlayURL
import com.laohei.bili_sdk.video.VideoArchive
import com.laohei.bili_sdk.video.VideoHeartBeat
import com.laohei.bili_sdk.video.VideoInfo
import com.laohei.bili_sdk.video.VideoReply
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.dataStore
import com.laohei.bili_tube.presentation.player.component.reply.VideoReplyPaging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow

class BiliPlayRepository(
    private val context: Context,
    private val playURL: PlayURL,
    private val videoInfo: VideoInfo,
    private val videoReply: VideoReply,
    private val videoHeartBeat: VideoHeartBeat,
    private val archive: VideoArchive
) {

    suspend fun getPlayURL(
        aid: Long,
        bvid: String,
        cid: Long,
        qn: Int = 116,
        fnval: Int = 4048,
    ) = playURL.videoUrl(
        aid, bvid, cid, qn, fnval,
        context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
    )

    suspend fun getVideoInfo(
        aid: Long,
        bvid: String,
    ) = videoInfo.videoInfo(
        aid, bvid,
        context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
    )

    suspend fun getVideoDetail(
        aid: Long,
        bvid: String,
    ) = videoInfo.getVideoDetail(
        aid, bvid,
        context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getVideoReplyPager(
        type: Int = 1,
        oid: String
    ): Flow<PagingData<VideoReplyItem>> {
        return flow {
            val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
            emit(
                Pager(
                    config = PagingConfig(
                        pageSize = 30,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = {
                        VideoReplyPaging(
                            videoReply,
                            cookie, type, oid
                        )
                    }
                ).flow
            )
        }.flattenConcat()
    }

    suspend fun uploadVideoHeartBeat(
        aid: String,
        bvid: String,
        cid: String,
        playedTime: Long = 0,
    ) = videoHeartBeat.uploadVideoHeartBeat(
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
        aid, bvid, cid, playedTime
    )

    suspend fun uploadVideoHistory(
        aid: String,
        cid: String,
        progress: Long = 0,
    ) = videoHeartBeat.uploadVideoHistory(
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
        aid, cid, progress
    )

    suspend fun getArchives(
        mid: Long,
        seasonId: Long,
        pageNum: Int = 1,
        pageSize: Int = 30,
        sortReverse: Boolean = true,
    ) = archive.videoArchive(
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
        mid = mid,
        seasonId = seasonId,
        pageNum = pageNum,
        pageSize = pageSize,
        sortReverse = sortReverse
    )

    suspend fun hasLike(
        aid: Long,
        bvid: String,
    ) = videoInfo.hasLike(
        aid = aid, bvid = bvid,
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
    )

    suspend fun hasCoin(
        aid: Long,
        bvid: String,
    ) = videoInfo.hasCoin(
        aid = aid, bvid = bvid,
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
    )

    suspend fun hasFavoured(
        aid: Long,
    ) = videoInfo.hasFavoured(
        aid = aid,
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
    )

    suspend fun videoLike(
        aid: Long,
        bvid: String,
        like: Int
    ) = videoInfo.videoLike(
        aid = aid, bvid = bvid, like = like,
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
    )

    suspend fun videoCoin(
        aid: Long,
        bvid: String,
        multiply: Int
    ) = videoInfo.videoCoin(
        aid = aid, bvid = bvid, multiply = multiply,
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
    )
}
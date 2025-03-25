package com.laohei.bili_tube.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.laohei.bili_sdk.anime.Timeline
import com.laohei.bili_sdk.hot.Hots
import com.laohei.bili_sdk.model.BiliAnimeSchedule
import com.laohei.bili_sdk.model.BiliHotVideoItem
import com.laohei.bili_sdk.module_v2.recomment.RecommendItem
import com.laohei.bili_sdk.recommend.Recommend
import com.laohei.bili_sdk.video.VideoInfo
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.dataStore
import com.laohei.bili_tube.presentation.home.anime.TimelinePaging
import com.laohei.bili_tube.presentation.home.hot.HotPaging
import com.laohei.bili_tube.presentation.home.recommend.RecommendPaging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow

class BiliHomeRepository(
    private val context: Context,
    private val recommend: Recommend,
    private val hots: Hots,
    private val timeline: Timeline,
    private val videoInfo: VideoInfo
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getPagedRecommendVideo(): Flow<PagingData<RecommendItem>> {
        return flow {
            val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
            emit(
                Pager(
                    config = PagingConfig(
                        pageSize = 12,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = { RecommendPaging(recommend, cookie) }
                ).flow
            )
        }.flattenConcat()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getPagedHotVideo(): Flow<PagingData<BiliHotVideoItem>> {
        return flow {
            val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
            emit(
                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = { HotPaging(hots, cookie) }
                ).flow
            )
        }.flattenConcat()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTimelineEpisode(): Flow<PagingData<BiliAnimeSchedule>> {
        return flow {
            val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
            emit(
                Pager(
                    config = PagingConfig(
                        pageSize = 13,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = { TimelinePaging(timeline, cookie) }
                ).flow
            )
        }.flattenConcat()
    }

    suspend fun videoFolderDeal(
        aid: Long,
        addMediaIds: Set<Long>,
        delMediaIds: Set<Long>,
    ) = videoInfo.videoFolderDeal(
        rid = aid,
        addMediaIds = addMediaIds,
        delMediaIds = delMediaIds,
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
    )
}
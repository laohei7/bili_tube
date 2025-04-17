package com.laohei.bili_tube.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.laohei.bili_sdk.anime.GetBangumi
import com.laohei.bili_sdk.anime.GetTimeline
import com.laohei.bili_sdk.folder.PostFolder
import com.laohei.bili_sdk.history.PostToView
import com.laohei.bili_sdk.hot.GetHots
import com.laohei.bili_sdk.model.BiliAnimeSchedule
import com.laohei.bili_sdk.model.BiliHotVideoItem
import com.laohei.bili_sdk.module_v2.bangumi.BangumiItem
import com.laohei.bili_sdk.module_v2.recomment.RecommendItem
import com.laohei.bili_sdk.recommend.GetRecommend
import com.laohei.bili_sdk.video.PostInfo
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.dataStore
import com.laohei.bili_tube.model.BangumiFilterModel
import com.laohei.bili_tube.presentation.home.anime.BangumiPaging
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
    private val getRecommend: GetRecommend,
    private val getHots: GetHots,
    private val getTimeline: GetTimeline,
    private val getBangumi: GetBangumi,
    private val postInfo: PostInfo,
    private val postToView: PostToView,
    private val postFolder: PostFolder
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
                    pagingSourceFactory = { RecommendPaging(getRecommend, context, cookie) }
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
                    pagingSourceFactory = { HotPaging(getHots, cookie) }
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
                    pagingSourceFactory = { TimelinePaging(getTimeline, cookie) }
                ).flow
            )
        }.flattenConcat()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getBangumis(bangumiFilterModel: BangumiFilterModel): Flow<PagingData<BangumiItem>> {
        return flow {
            val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
            emit(
                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = { BangumiPaging(getBangumi, bangumiFilterModel, cookie) }
                ).flow
            )
        }.flattenConcat()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAnimations(bangumiFilterModel: BangumiFilterModel): Flow<PagingData<BangumiItem>> {
        return flow {
            val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
            emit(
                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = {
                        BangumiPaging(
                            getBangumi,
                            bangumiFilterModel,
                            cookie,
                            4
                        )
                    }
                ).flow
            )
        }.flattenConcat()
    }

    suspend fun folderDeal(
        aid: Long,
        addMediaIds: Set<Long>,
        delMediaIds: Set<Long>,
    ) = postFolder.folderDeal(
        rid = aid,
        addMediaIds = addMediaIds,
        delMediaIds = delMediaIds,
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
    )

    suspend fun addToView(
        aid: Long,
        bvid: String
    ) = postToView.addToView(
        aid = aid, bvid = bvid,
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
    )
}
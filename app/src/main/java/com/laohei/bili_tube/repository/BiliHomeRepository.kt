package com.laohei.bili_tube.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.laohei.bili_sdk.apis.BangumiApi
import com.laohei.bili_sdk.apis.FolderApi
import com.laohei.bili_sdk.apis.HistoryApi
import com.laohei.bili_sdk.apis.VideoApi
import com.laohei.bili_sdk.module_v2.bangumi.AnimeScheduleModel
import com.laohei.bili_sdk.module_v2.bangumi.BangumiItem
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.common.BiliResponseNoData
import com.laohei.bili_sdk.module_v2.folder.FolderDealModel
import com.laohei.bili_sdk.module_v2.hot.HotItem
import com.laohei.bili_sdk.module_v2.recomment.RecommendItem
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.dataStore
import com.laohei.bili_tube.model.BangumiFilterModel
import com.laohei.bili_tube.presentation.home.anime.BangumiPaging
import com.laohei.bili_tube.presentation.home.anime.TimelinePaging
import com.laohei.bili_tube.presentation.home.hot.HotPaging
import com.laohei.bili_tube.presentation.home.recommend.RecommendPaging
import com.laohei.bili_tube.utill.getBiliJct
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow

class BiliHomeRepository(
    private val context: Context,
    private val videoApi: VideoApi,
    private val folderApi: FolderApi,
    private val historyApi: HistoryApi,
    private val bangumiApi: BangumiApi
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getRecommendPager(): Flow<PagingData<RecommendItem>> {
        return flow {
            val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
            emit(
                Pager(
                    config = PagingConfig(
                        pageSize = 12,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = { RecommendPaging(videoApi, context, cookie) }
                ).flow
            )
        }.flattenConcat()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getHotPager(): Flow<PagingData<HotItem>> {
        return flow {
            val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
            emit(
                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = { HotPaging(videoApi, cookie) }
                ).flow
            )
        }.flattenConcat()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTimelineEpisode(): Flow<PagingData<AnimeScheduleModel>> {
        return flow {
            val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
            emit(
                Pager(
                    config = PagingConfig(
                        pageSize = 13,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = { TimelinePaging(bangumiApi, cookie) }
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
                    pagingSourceFactory = { BangumiPaging(bangumiApi, bangumiFilterModel, cookie) }
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
                            bangumiApi,
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
    ): BiliResponse<FolderDealModel>? {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return folderApi.dealFolder(
            aid = aid,
            addMediaIds = addMediaIds,
            delMediaIds = delMediaIds,
            cookie = cookie,
            biliJct = cookie.getBiliJct()
        )
    }

    suspend fun addToView(
        aid: Long,
        bvid: String
    ): BiliResponseNoData {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return historyApi.addToView(
            aid = aid, bvid = bvid,
            cookie = cookie,
            csrf = cookie.getBiliJct()
        )
    }
}
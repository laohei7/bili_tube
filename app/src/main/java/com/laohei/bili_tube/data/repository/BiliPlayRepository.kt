package com.laohei.bili_tube.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.laohei.bili_sdk.apis.BangumiApi
import com.laohei.bili_sdk.apis.FolderApi
import com.laohei.bili_sdk.apis.HistoryApi
import com.laohei.bili_sdk.apis.PlayApi
import com.laohei.bili_sdk.apis.UserApi
import com.laohei.bili_sdk.apis.UserRelationAction
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.common.BiliResponseNoData
import com.laohei.bili_sdk.module_v2.folder.FolderDealModel
import com.laohei.bili_sdk.module_v2.reply.ReplyItem
import com.laohei.bili_sdk.module_v2.user.UploadedVideoItem
import com.laohei.bili_sdk.module_v2.video.AddCoinModel
import com.laohei.bili_sdk.module_v2.video.VideoURLModel
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.core.VideoSuperQualities
import com.laohei.bili_tube.data.local.datastore.dataStore
import com.laohei.bili_tube.data.local.room.BiliTubeDB
import com.laohei.bili_tube.data.local.room.entity.BiliAudioUrl
import com.laohei.bili_tube.data.local.room.entity.BiliVideoUrl
import com.laohei.bili_tube.data.paging.UserUploadedVideoPaging
import com.laohei.bili_tube.data.paging.VideoReplyPaging
import com.laohei.bili_tube.util.extractBiliJct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class BiliPlayRepository(
    private val context: Context,
    private val historyApi: HistoryApi,
    private val playApi: PlayApi,
    private val folderApi: FolderApi,
    private val bangumiApi: BangumiApi,
    private val userApi: UserApi,
    private val biliTubeDB: BiliTubeDB,
) {

    suspend fun getVideoPlayURLByLocal(bvid: String) =
        biliTubeDB.downloadTaskDao().getTaskById(bvid)

    suspend fun getVideoPlayURL(
        aid: Long,
        bvid: String,
        cid: Long,
        qn: Int = 116,
        fnval: Int = 4048,
    ) = playApi.getVideoURL(
        aid, bvid, cid, qn, fnval,
        context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
    )

    suspend fun getMediaPlayURL(
        avid: Long? = null,
        bvid: String? = null,
        cid: Long? = null,
        epId: Long? = null,
        qn: Int = 127,
        fnval: Int = 4048,
    ) = playApi.getMediaURL(
        avid, bvid, cid, epId, qn, fnval,
        context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
    )

    suspend fun getVideoDetail(
        aid: Long,
        bvid: String,
    ) = playApi.getVideoDetail(
        aid, bvid,
        context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
    )

    suspend fun getBangumiDetail(
        seasonId: Long? = null,
        epId: Long? = null,
    ) = playApi.getBangumiDetail(
        seasonId, epId,
        context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getVideoReplyPager(
        type: Int = 1,
        oid: String
    ): Flow<PagingData<ReplyItem>> {
        return flow {
            val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
            emit(
                Pager(
                    config = PagingConfig(
                        pageSize = 30,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = {
                        VideoReplyPaging(playApi, cookie, type, oid)
                    }
                ).flow
            )
        }.flattenConcat()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun userUploadedVideos(mid: Long): Flow<PagingData<UploadedVideoItem>> {
        return flow {
            val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
            emit(
                Pager(
                    config = PagingConfig(pageSize = 20),
                    pagingSourceFactory = {
                        UserUploadedVideoPaging(
                            userApi = userApi,
                            cookie = cookie,
                            mid = mid
                        )
                    }
                ).flow
            )
        }.flattenConcat()
    }

    suspend fun postHistory(
        aid: String,
        cid: String,
        progress: Long = 0,
    ): BiliResponseNoData {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return historyApi.postHistory(
            cookie = cookie,
            aid = aid, cid = cid, progress = progress,
            biliJct = cookie.extractBiliJct()
        )
    }

    suspend fun getArchives(
        mid: Long,
        seasonId: Long,
        pageNum: Int = 1,
        pageSize: Int = 30,
        sortReverse: Boolean = false,
    ) = playApi.getArchives(
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
        mid = mid,
        seasonId = seasonId,
        pageNum = pageNum,
        pageSize = pageSize,
        sortReverse = sortReverse
    )

    suspend fun getPageList(bvid: String) = playApi.getMediaSeries(
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
        bvid = bvid
    )

    suspend fun hasLike(
        aid: Long,
        bvid: String,
    ) = playApi.hasLike(
        aid = aid, bvid = bvid,
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
    )

    suspend fun hasCoin(
        aid: Long,
        bvid: String,
    ) = playApi.hasCoin(
        aid = aid, bvid = bvid,
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
    )

    suspend fun hasFavoured(
        aid: Long,
    ) = playApi.hasFavoured(
        aid = aid,
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
    )

    suspend fun videoLike(
        aid: Long,
        bvid: String,
        like: Int
    ) = playApi.postLike(
        aid = aid, bvid = bvid, like = like,
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
    )

    suspend fun videoCoin(
        aid: Long,
        bvid: String,
        multiply: Int
    ): BiliResponse<AddCoinModel>? {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
//        val cookie = "buvid3=2625B771-4491-E26F-4637-988E556FB55856487infoc; b_nut=1740791956; enable_web_push=DISABLE; buvid4=E843E1D2-7504-26B3-7F38-DF054B70392A63486-025030101-Vkcl9zUd0a7v7dsB27q10A%3D%3D; enable_feed_channel=ENABLE; rpdid=|(J~luR|Yu~)0J'u~R|~mRm~~; buvid_fp_plain=undefined; PVID=1; hit-dyn-v2=1; BILI-MALL-MERCHANT-IS-COMPUTER=false; header_theme_version=OPEN; theme_style=light; CURRENT_QUALITY=116; theme-tip-show=SHOWED; theme-avatar-tip-show=SHOWED; theme-switch-show=SHOWED; fingerprint=2e9d5d6760ad067fd3bd046fa4cdce18; buvid_fp=f9aa9950bdf038726c1d1fe5277fbe8c; balh_server_inner=__custom__; balh_is_closed=; DedeUserID=291325546; DedeUserID__ckMd5=14675df63f611236; home_feed_column=5; SESSDATA=c1c0aab4%2C1767848702%2C292ee%2A71CjDmBE7rWi_Az88UouilBkVCyd_cHtqc_W2jfG_IsIue7fXKFnLwXB20O3NkoPp1GdQSVkZfY2czb0FMREhXX3oxWGx5WnV4RzVBUlM1SG5zcURKd3J3bTl5M3JUUGR1REN3c0szcHhPZUlXZFVZTFdOa2dsQmw1V1Rfa0hLZzRPalJmZTdDY0xBIIEC; bili_jct=8ca1435c04e0faa2c1a6b24d4160064c; bili_ticket=eyJhbGciOiJIUzI1NiIsImtpZCI6InMwMyIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3NTI1Nzc2MDEsImlhdCI6MTc1MjMxODM0MSwicGx0IjotMX0.ZflVcUlYSdjtCrH4d9x-dmaN4irpmzycqT7eDxmgu80; bili_ticket_expires=1752577541; b_lsid=1CA4947D_197FE7DC7A7; browser_resolution=1920-991; bp_t_offset_291325546=1088701774912552960; sid=87dlikhr; CURRENT_FNVAL=4048"
        return playApi.postCoins(
            aid = aid, bvid = bvid, multiply = multiply,
            cookie = cookie,
            biliJct = cookie.extractBiliJct()
        )
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
            biliJct = cookie.extractBiliJct()
        )
    }

    suspend fun getRelatedBangumis(seasonId: Long) = bangumiApi.relatedBangumis(seasonId)

    suspend fun getUserInfoCard(mid: Long) = userApi.getUserInfoCard(
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
        mid = mid
    )

    suspend fun userRelationModify(
        mid: Long,
        act: UserRelationAction,
    ): BiliResponseNoData {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return userApi.postRelationModify(
            cookie = cookie,
            mid = mid,
            act = act,
            csrf = cookie.extractBiliJct()
        )
    }

    suspend fun saveSharedSource(
        bvid: String,
        aid: Long,
        cid: Long,
        data: VideoURLModel
    ) = withContext(Dispatchers.IO) {
        data.dash?.let { dash ->
            val videoList = dash.video
                .filter { it -> VideoSuperQualities.any { quality -> quality.first == it.id } }
                .map {
                    BiliVideoUrl(
                        bvid = bvid,
                        aid = aid,
                        cid = cid,
                        quality = it.id,
                        url = it.baseUrl
                    )
                }

            val audioList = dash.audio
                .filter { it.id in arrayOf(30250, 30251) }
                .map {
                    BiliAudioUrl(
                        bvid = bvid,
                        aid = aid,
                        cid = cid,
                        quality = it.id,
                        url = it.baseUrl
                    )
                }.toMutableList()
            dash.dolby?.audio?.let { dolby ->
                audioList.add(
                    BiliAudioUrl(
                        bvid = bvid,
                        aid = aid,
                        cid = cid,
                        quality = dolby.first().id,
                        url = dolby.first().baseUrl
                    )
                )
            }
            biliTubeDB.biliSharedSourceDao().addVideos(videoList)
            biliTubeDB.biliSharedSourceDao().addAudios(audioList)
        }
    }
}
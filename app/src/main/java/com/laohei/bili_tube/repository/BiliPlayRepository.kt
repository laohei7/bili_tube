package com.laohei.bili_tube.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.laohei.bili_sdk.anime.GetBangumi
import com.laohei.bili_sdk.apis.UserApi
import com.laohei.bili_sdk.apis.UserRelationAction
import com.laohei.bili_sdk.folder.PostFolder
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.common.BiliResponseNoData
import com.laohei.bili_sdk.module_v2.folder.FolderDealModel
import com.laohei.bili_sdk.module_v2.reply.ReplyItem
import com.laohei.bili_sdk.module_v2.user.UploadedVideoItem
import com.laohei.bili_sdk.module_v2.video.AddCoinModel
import com.laohei.bili_sdk.user.GetUploadedVideo
import com.laohei.bili_sdk.user.GetUserInfo
import com.laohei.bili_sdk.video.GetArchive
import com.laohei.bili_sdk.video.GetInfo
import com.laohei.bili_sdk.video.GetReply
import com.laohei.bili_sdk.video.GetURL
import com.laohei.bili_sdk.video.PostHeartBeat
import com.laohei.bili_sdk.video.PostInfo
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.dataStore
import com.laohei.bili_tube.db.BiliTubeDB
import com.laohei.bili_tube.presentation.player.component.reply.VideoReplyPaging
import com.laohei.bili_tube.presentation.user.UserUploadedVideoPaging
import com.laohei.bili_tube.utill.getBiliJct
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow

class BiliPlayRepository(
    private val context: Context,
    private val getURL: GetURL,
    private val getBangumi: GetBangumi,
    private val getInfo: GetInfo,
    private val getReply: GetReply,
    private val getArchive: GetArchive,
    private val postInfo: PostInfo,
    private val postHeartBeat: PostHeartBeat,
    private val postFolder: PostFolder,
    private val getUserInfo: GetUserInfo,
    private val biliTubeDB: BiliTubeDB,
    private val getUploadedVideo: GetUploadedVideo,
    private val userApi: UserApi
) {

    suspend fun getVideoPlayURLByLocal(bvid: String) =
        biliTubeDB.downloadTaskDao().getTaskById(bvid)

    suspend fun getVideoPlayURL(
        aid: Long,
        bvid: String,
        cid: Long,
        qn: Int = 116,
        fnval: Int = 4048,
    ) = getURL.videoUrl(
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
    ) = getURL.mediaUrl(
        avid, bvid, cid, epId, qn, fnval,
        context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
    )

    suspend fun getVideoInfo(
        aid: Long,
        bvid: String,
    ) = getInfo.videoInfo(
        aid, bvid,
        context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
    )

    suspend fun getVideoDetail(
        aid: Long,
        bvid: String,
    ) = getInfo.getVideoDetail(
        aid, bvid,
        context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
    )

    suspend fun getBangumiDetail(
        seasonId: Long? = null,
        epId: Long? = null,
    ) = getInfo.bangumiDetail(
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
                        VideoReplyPaging(
                            getReply,
                            cookie, type, oid
                        )
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
                            getUploadedVideo = getUploadedVideo,
                            cookie = cookie,
                            mid = mid
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
    ) = postHeartBeat.uploadVideoHeartBeat(
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
        aid, bvid, cid, playedTime
    )

    suspend fun uploadVideoHistory(
        aid: String,
        cid: String,
        progress: Long = 0,
    ): HttpResponse? {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return postHeartBeat.uploadVideoHistory(
            cookie = cookie,
            aid = aid, cid = cid, progress = progress,
            biliJct = cookie.getBiliJct()
        )
    }

    suspend fun getArchives(
        mid: Long,
        seasonId: Long,
        pageNum: Int = 1,
        pageSize: Int = 30,
        sortReverse: Boolean = false,
    ) = getArchive.videoArchive(
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
        mid = mid,
        seasonId = seasonId,
        pageNum = pageNum,
        pageSize = pageSize,
        sortReverse = sortReverse
    )

    suspend fun getPageList(bvid: String) = getArchive.videoPageList(
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
        bvid = bvid
    )

    suspend fun hasLike(
        aid: Long,
        bvid: String,
    ) = getInfo.hasLike(
        aid = aid, bvid = bvid,
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
    )

    suspend fun hasCoin(
        aid: Long,
        bvid: String,
    ) = getInfo.hasCoin(
        aid = aid, bvid = bvid,
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
    )

    suspend fun hasFavoured(
        aid: Long,
    ) = getInfo.hasFavoured(
        aid = aid,
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
    )

    suspend fun videoLike(
        aid: Long,
        bvid: String,
        like: Int
    ) = getInfo.videoLike(
        aid = aid, bvid = bvid, like = like,
        cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY),
    )

    suspend fun videoCoin(
        aid: Long,
        bvid: String,
        multiply: Int
    ): BiliResponse<AddCoinModel>? {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return postInfo.videoCoin(
            aid = aid, bvid = bvid, multiply = multiply,
            cookie = cookie,
            biliJct = cookie.getBiliJct()
        )
    }

    suspend fun folderDeal(
        aid: Long,
        addMediaIds: Set<Long>,
        delMediaIds: Set<Long>,
    ): BiliResponse<FolderDealModel>? {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return postFolder.folderDeal(
            rid = aid,
            addMediaIds = addMediaIds,
            delMediaIds = delMediaIds,
            cookie = cookie,
            biliJct = cookie.getBiliJct()
        )
    }

    suspend fun getRelatedBangumis(seasonId: Long) = getBangumi.relatedBangumis(seasonId)

    suspend fun getUserInfoCard(mid: Long) = getUserInfo.getUserInfoCard(
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
            csrf = cookie.getBiliJct()
        )
    }
}
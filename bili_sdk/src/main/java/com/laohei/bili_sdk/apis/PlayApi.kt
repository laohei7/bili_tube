package com.laohei.bili_sdk.apis

import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.common.BiliResponse2
import com.laohei.bili_sdk.module_v2.common.BiliResponseNoData
import com.laohei.bili_sdk.module_v2.reply.ReplyModel
import com.laohei.bili_sdk.module_v2.video.AddCoinModel
import com.laohei.bili_sdk.module_v2.video.BangumiDetailModel
import com.laohei.bili_sdk.module_v2.video.CoinModel
import com.laohei.bili_sdk.module_v2.video.FavouredModel
import com.laohei.bili_sdk.module_v2.video.VideoArchiveModel
import com.laohei.bili_sdk.module_v2.video.VideoDetailModel
import com.laohei.bili_sdk.module_v2.video.VideoPageListModel
import com.laohei.bili_sdk.module_v2.video.VideoURLModel

interface PlayApi {
    suspend fun getVideoURL(
        aid: Long,
        bvid: String,
        cid: Long,
        qn: Int = 127,
        fnval: Int = 4048,
        cookie: String? = null
    ): BiliResponse<VideoURLModel>


    suspend fun getMediaURL(
        avid: Long? = null,
        bvid: String? = null,
        cid: Long? = null,
        epId: Long? = null,
        qn: Int = 127,
        fnval: Int = 4048,
        cookie: String? = null
    ): BiliResponse2<VideoURLModel>

    suspend fun getVideoDetail(
        aid: Long,
        bvid: String,
        cookie: String? = null
    ): BiliResponse<VideoDetailModel>

    suspend fun getBangumiDetail(
        seasonId: Long? = null,
        epId: Long? = null,
        cookie: String? = null
    ): BiliResponse2<BangumiDetailModel>

    suspend fun hasLike(
        aid: Long,
        bvid: String,
        cookie: String? = null
    ): BiliResponse<Int>

    suspend fun hasCoin(
        aid: Long,
        bvid: String,
        cookie: String? = null
    ): BiliResponse<CoinModel>

    suspend fun hasFavoured(
        aid: Long,
        cookie: String? = null
    ): BiliResponse<FavouredModel>

    suspend fun postLike(
        aid: Long,
        bvid: String,
        like: Int,
        cookie: String? = null,
    ): BiliResponseNoData

    suspend fun getVideoReplies(
        cookie: String? = null, type: Int = 1, oid: String, ps: Int = 20, pn: Int = 1
    ): BiliResponse<ReplyModel>


    suspend fun getArchives(
        mid: Long,
        seasonId: Long,
        pageNum: Int = 1,
        pageSize: Int = 30,
        sortReverse: Boolean = true,
        cookie: String? = null
    ): BiliResponse<VideoArchiveModel>

    suspend fun getMediaSeries(
        bvid: String,
        cookie: String? = null
    ): BiliResponse<List<VideoPageListModel>>

    suspend fun postCoins(
        aid: Long,
        bvid: String,
        multiply: Int,
        cookie: String? = null,
        biliJct: String
    ):BiliResponse<AddCoinModel>
}
package com.laohei.bili_sdk.apis.impl

import com.laohei.bili_sdk.apis.URL_BANGUMI_DETAIL
import com.laohei.bili_sdk.apis.URL_BANGUMI_PLAY
import com.laohei.bili_sdk.apis.BILIBILI
import com.laohei.bili_sdk.apis.PlayApi
import com.laohei.bili_sdk.apis.URL_VIDEO_DETAIL
import com.laohei.bili_sdk.apis.URL_HAS_COIN
import com.laohei.bili_sdk.apis.URL_HAS_FAVORED
import com.laohei.bili_sdk.apis.URL_HAS_LIKE
import com.laohei.bili_sdk.apis.URL_DEAL_LIKE
import com.laohei.bili_sdk.apis.URL_VIDEO_PLAY
import com.laohei.bili_sdk.apis.URL_VIDEO_REPLY
import com.laohei.bili_sdk.exception.globalSDKExceptionHandle
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.common.BiliResponse2
import com.laohei.bili_sdk.module_v2.common.BiliResponseNoData
import com.laohei.bili_sdk.module_v2.reply.ReplyModel
import com.laohei.bili_sdk.module_v2.video.BangumiDetailModel
import com.laohei.bili_sdk.module_v2.video.CoinModel
import com.laohei.bili_sdk.module_v2.video.FavouredModel
import com.laohei.bili_sdk.module_v2.video.VideoCard
import com.laohei.bili_sdk.module_v2.video.VideoCardContent
import com.laohei.bili_sdk.module_v2.video.VideoDetailModel
import com.laohei.bili_sdk.module_v2.video.VideoDimension
import com.laohei.bili_sdk.module_v2.video.VideoOwner
import com.laohei.bili_sdk.module_v2.video.VideoStat
import com.laohei.bili_sdk.module_v2.video.VideoURLModel
import com.laohei.bili_sdk.module_v2.video.VideoView
import com.laohei.bili_sdk.wbi.GetWbi
import com.laohei.bili_sdk.wbi.WbiParams
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class PlayApiImpl(
    private val client: HttpClient
) : PlayApi {

    companion object {
        private val TAG = PlayApiImpl::class.simpleName
        private const val DBG = true
    }

    override suspend fun getVideoURL(
        aid: Long,
        bvid: String,
        cid: Long,
        qn: Int,
        fnval: Int,
        cookie: String?
    ): BiliResponse<VideoURLModel> = withContext(Dispatchers.IO) {
        runCatching {
            if (WbiParams.wbi == null) {
                GetWbi.getWbiRequest(client).wbi(cookie)
            }
            val param = WbiParams.wbi!!.enc(
                mapOf("aid" to aid, "bvid" to bvid, "cid" to cid, "qn" to qn, "fnval" to fnval)
            )
            val response = client.get("${URL_VIDEO_PLAY}?$param") {
                cookie?.apply {
                    header(HttpHeaders.Cookie, this)
                }
            }
            Json.decodeFromString<BiliResponse<VideoURLModel>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse(
                    code = 400,
                    message = "ERROR",
                    data = VideoURLModel(
                        quality = 0,
                        timeLength = 0,
                        acceptDescription = emptyList(),
                        acceptQuality = emptyList(),
                        videoCodecId = 0,
                        supportFormats = emptyList(),
                    )
                )
            }
        )
    }

    override suspend fun getMediaURL(
        avid: Long?,
        bvid: String?,
        cid: Long?,
        epId: Long?,
        qn: Int,
        fnval: Int,
        cookie: String?
    ): BiliResponse2<VideoURLModel> = withContext(Dispatchers.IO) {
        runCatching {
            if (cid == null && epId == null) {
                throw IllegalArgumentException("Both cid and ep_id can not be Null at the same time")
            }
            if (WbiParams.wbi == null) {
                GetWbi.getWbiRequest(client).wbi(cookie)
            }
            val param = WbiParams.wbi!!.enc(
                buildMap {
                    avid?.let { put("avid", it) }
                    bvid?.let { put("bvid", it) }
                    cid?.let { put("cid", it) }
                    epId?.let { put("ep_id", it) }
                    put("qn", qn)
                    put("fnval", fnval)
                }
            )
            val response = client.get("${URL_BANGUMI_PLAY}?$param") {
                cookie?.apply {
                    header(HttpHeaders.Cookie, this)
                }
                header(HttpHeaders.Referrer, BILIBILI)
            }
            Json.decodeFromString<BiliResponse2<VideoURLModel>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse2(
                    code = 400,
                    message = "ERROR",
                    result = VideoURLModel(
                        quality = 0,
                        timeLength = 0,
                        acceptDescription = emptyList(),
                        acceptQuality = emptyList(),
                        videoCodecId = 0,
                        supportFormats = emptyList(),
                    )
                )
            }
        )
    }

    override suspend fun getVideoDetail(
        aid: Long,
        bvid: String,
        cookie: String?
    ): BiliResponse<VideoDetailModel> = withContext(Dispatchers.IO) {
        runCatching {
            if (WbiParams.wbi == null) {
                GetWbi.getWbiRequest(client).wbi(cookie)
            }
            val param = WbiParams.wbi!!.enc(
                mapOf("aid" to aid, "bvid" to bvid)
            )
            val response = client.get("$URL_VIDEO_DETAIL?$param") {
                cookie?.apply {
                    header(HttpHeaders.Cookie, this)
                }
            }
            Json.decodeFromString<BiliResponse<VideoDetailModel>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse(
                    code = 400,
                    message = "ERROR",
                    data = VideoDetailModel(
                        view = VideoView(
                            "", 0, 0, "", "", 0, 0, "", 0, VideoOwner(0, "", ""), VideoStat(),
                            VideoDimension(0, 0, 0)
                        ),
                        card = VideoCard(VideoCardContent("", "", "", "", "", 0, 0, 0), 0),
                        tags = emptyList(),
                        related = emptyList()
                    )
                )
            }
        )
    }

    override suspend fun getBangumiDetail(
        seasonId: Long?,
        epId: Long?,
        cookie: String?
    ): BiliResponse2<BangumiDetailModel> = withContext(Dispatchers.IO) {
        runCatching {
            when {
                seasonId == null && epId == null -> {
                    throw IllegalArgumentException("Both season_id and ep_id can not be Null at the same time")
                }

                else -> {}
            }
            val response = client.get(URL_BANGUMI_DETAIL) {
                cookie?.apply {
                    header(HttpHeaders.Cookie, this)
                }
                seasonId?.let {
                    parameter("season_id", it.toString())
                }
                epId?.let {
                    parameter("ep_id", it.toString())
                }
            }
            Json.decodeFromString<BiliResponse2<BangumiDetailModel>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse2(
                    code = 400,
                    message = "ERROR",
                    result = BangumiDetailModel.ERROR
                )
            }
        )
    }

    override suspend fun hasLike(
        aid: Long,
        bvid: String,
        cookie: String?
    ): BiliResponse<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get(URL_HAS_LIKE) {
                cookie?.apply {
                    header(HttpHeaders.Cookie, this)
                }
                parameter("aid", aid.toString())
                parameter("bvid", bvid)
            }
            Json.decodeFromString<BiliResponse<Int>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse(
                    code = 400,
                    message = "ERROR",
                    data = 0
                )
            }
        )
    }

    override suspend fun hasCoin(
        aid: Long,
        bvid: String,
        cookie: String?
    ): BiliResponse<CoinModel> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get(URL_HAS_COIN) {
                cookie?.apply {
                    header(HttpHeaders.Cookie, this)
                }
                parameter("aid", aid.toString())
                parameter("bvid", bvid)
            }
            Json.decodeFromString<BiliResponse<CoinModel>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse(
                    code = 400,
                    message = "ERROR",
                    data = CoinModel(0)
                )
            }
        )
    }

    override suspend fun hasFavoured(
        aid: Long,
        cookie: String?
    ): BiliResponse<FavouredModel> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get(URL_HAS_FAVORED) {
                cookie?.apply {
                    header(HttpHeaders.Cookie, this)
                }
                parameter("aid", aid.toString())
            }
            Json.decodeFromString<BiliResponse<FavouredModel>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse(
                    code = 400,
                    message = "ERROR",
                    data = FavouredModel(0, false)
                )
            }
        )
    }

    override suspend fun postLike(
        aid: Long,
        bvid: String,
        like: Int,
        cookie: String?
    ): BiliResponseNoData = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.post(URL_DEAL_LIKE) {
                cookie?.apply {
                    header(HttpHeaders.Cookie, this)
                }
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("aid", aid.toString())
                            append("bvid", bvid)
                            append("like", like.toString())
                            append(
                                "csrf",
                                cookie?.substringAfter("bili_jct=")?.substringBefore(";") ?: ""
                            )
                        }
                    ))
            }
            Json.decodeFromString<BiliResponseNoData>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponseNoData(400, "ERROR")
            }
        )
    }

    override suspend fun getVideoReplies(
        cookie: String?,
        type: Int,
        oid: String,
        ps: Int,
        pn: Int
    ): BiliResponse<ReplyModel> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get(URL_VIDEO_REPLY) {
                cookie?.apply {
                    header(HttpHeaders.Cookie, this)
                }
                parameter("type", type.toString())
                parameter("oid", oid)
                parameter("pn", pn.toString())
                parameter("ps", ps.toString())
            }
            Json.decodeFromString<BiliResponse<ReplyModel>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse(
                    code = 400,
                    message = "ERROR",
                    data = ReplyModel.ERROR
                )
            }
        )
    }
}
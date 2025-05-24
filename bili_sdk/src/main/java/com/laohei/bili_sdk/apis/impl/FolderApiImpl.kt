package com.laohei.bili_sdk.apis.impl

import com.laohei.bili_sdk.apis.BILIBILI
import com.laohei.bili_sdk.apis.FolderApi
import com.laohei.bili_sdk.apis.URL_FOLDER
import com.laohei.bili_sdk.apis.URL_FOLDER_DEAL
import com.laohei.bili_sdk.apis.URL_FOLDER_RESOURCE_LIST
import com.laohei.bili_sdk.apis.URL_SIMPLE_FOLDER
import com.laohei.bili_sdk.exception.globalSDKExceptionHandle
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.folder.FolderDealModel
import com.laohei.bili_sdk.module_v2.folder.FolderModel
import com.laohei.bili_sdk.module_v2.folder.FolderResourceModel
import com.laohei.bili_sdk.module_v2.folder.SimpleFolderModel
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

class FolderApiImpl(
    private val client: HttpClient
) : FolderApi {
    companion object {
        private val TAG = FolderApiImpl::class.simpleName
        private const val DBG = true
    }

    override suspend fun getFolders(cookie: String?): BiliResponse<List<FolderModel>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = client.get(URL_FOLDER) {
                    cookie?.apply {
                        header(HttpHeaders.Cookie, this)
                    }
                }
                Json.decodeFromString<BiliResponse<List<FolderModel>>>(response.bodyAsText())
            }.fold(
                onSuccess = { it },
                onFailure = {
                    if (DBG) {
                        globalSDKExceptionHandle(TAG.toString(), it)
                    }
                    BiliResponse(
                        code = 400,
                        message = "ERROR",
                        data = emptyList()
                    )
                }
            )
        }

    override suspend fun getSimpleFolders(
        cookie: String?,
        aid: Long,
        mid: Long
    ): BiliResponse<SimpleFolderModel> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get(URL_SIMPLE_FOLDER) {
                cookie?.let {
                    header(HttpHeaders.Cookie, it)
                }
                parameter("rid", aid.toString())
                parameter("up_mid", mid.toString())
            }
            Json.decodeFromString<BiliResponse<SimpleFolderModel>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse(
                    code = 400,
                    message = "ERROR",
                    data = SimpleFolderModel(count = 0, list = emptyList())
                )
            }
        )
    }

    override suspend fun dealFolder(
        aid: Long,
        type: FolderApi.FolderAction,
        addMediaIds: Set<Long>,
        delMediaIds: Set<Long>,
        cookie: String?,
        biliJct: String?
    ): BiliResponse<FolderDealModel> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.post(URL_FOLDER_DEAL) {
                cookie?.apply {
                    header(HttpHeaders.Cookie, cookie)
                }
                header(HttpHeaders.Referrer, BILIBILI)
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("rid", aid.toString())
                            append("type", type.id.toString())
                            append("add_media_ids", addMediaIds.joinToString(","))
                            append("del_media_ids", delMediaIds.joinToString(","))
                            biliJct?.let { append("csrf", it) }
                        }
                    )
                )
            }
            Json.decodeFromString<BiliResponse<FolderDealModel>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse(
                    code = 400,
                    message = "ERROR",
                    data = FolderDealModel(false)
                )
            }
        )
    }

    override suspend fun getFolderResources(
        cookie: String?,
        mlid: Long,
        ps: Int,
        pn: Int
    ): BiliResponse<FolderResourceModel> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get(URL_FOLDER_RESOURCE_LIST) {
                cookie?.let { header(HttpHeaders.Cookie, it) }
                parameter("media_id", mlid.toString())
                parameter("ps", ps.toString())
                parameter("pn", pn.toString())
            }
            Json.decodeFromString<BiliResponse<FolderResourceModel>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse(
                    code = 400,
                    message = "ERROR",
                    data = FolderResourceModel.ERROR
                )
            }
        )
    }
}
package com.laohei.bili_sdk.apis

import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.folder.FolderDealModel
import com.laohei.bili_sdk.module_v2.folder.FolderModel
import com.laohei.bili_sdk.module_v2.folder.FolderResourceModel
import com.laohei.bili_sdk.module_v2.folder.SimpleFolderModel

interface FolderApi {

    enum class FolderAction(val id: Int) {
        ADD(2)
    }

    suspend fun getFolders(cookie: String? = null): BiliResponse<List<FolderModel>>

    suspend fun getSimpleFolders(
        cookie: String? = null,
        aid: Long,
        mid: Long
    ): BiliResponse<SimpleFolderModel>

    suspend fun dealFolder(
        aid: Long,
        type: FolderAction = FolderAction.ADD,
        addMediaIds: Set<Long>,
        delMediaIds: Set<Long>,
        cookie: String? = null,
        biliJct: String? = null
    ): BiliResponse<FolderDealModel>

    suspend fun getFolderResources(
        cookie: String? = null,
        mlid: Long,
        ps: Int = 20,
        pn: Int = 1
    ): BiliResponse<FolderResourceModel>
}
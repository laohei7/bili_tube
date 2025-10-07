package com.laohei.bili_sdk.apis

import com.laohei.bili_sdk.model_v2.common.BiliResponse
import com.laohei.bili_sdk.model_v2.folder.ModifyFavoriteModel
import com.laohei.bili_sdk.model_v2.folder.FolderItem
import com.laohei.bili_sdk.model_v2.folder.FolderModel
import com.laohei.bili_sdk.model_v2.folder.FolderContent
import com.laohei.bili_sdk.model_v2.folder.SimpleFolderModel

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

    suspend fun modifyFavorite(
        aid: Long,
        type: FolderAction = FolderAction.ADD,
        addMediaIds: Set<Long>,
        delMediaIds: Set<Long>,
        cookie: String? = null,
        biliJct: String? = null
    ): BiliResponse<ModifyFavoriteModel>

    suspend fun getFolderResources(
        cookie: String? = null,
        mlid: Long,
        ps: Int = 20,
        pn: Int = 1
    ): BiliResponse<FolderContent>

    suspend fun addNewFolder(
        cookie: String? = null,
        title: String, privacy: Boolean = false, csrf: String? = null
    ): BiliResponse<FolderItem?>

    suspend fun editFolder(
        mediaId: Long,
        title: String,
        intro: String? = null,
        privacy: Boolean = false,
        csrf: String? = null
    )
}
package com.laohei.bili_tube.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.laohei.bili_sdk.apis.FolderApi
import com.laohei.bili_sdk.apis.HistoryApi
import com.laohei.bili_sdk.model_v2.common.BiliResponse
import com.laohei.bili_sdk.model_v2.common.BiliResponseNoData
import com.laohei.bili_sdk.model_v2.folder.ModifyFavoriteModel
import com.laohei.bili_sdk.model_v2.folder.MediaItem
import com.laohei.bili_sdk.model_v2.folder.FolderModel
import com.laohei.bili_sdk.model_v2.folder.SimpleFolderModel
import com.laohei.bili_sdk.model_v2.history.ToViewModel
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.core.UP_MID_KEY
import com.laohei.bili_tube.core.extension.getValue
import com.laohei.bili_tube.data.local.datastore.dataStore
import com.laohei.bili_tube.data.paging.FolderResourcePaging
import com.laohei.bili_tube.util.extractBiliJct
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow

class BiliPlaylistRepository(
    private val context: Context,
    private val historyApi: HistoryApi,
    private val folderApi: FolderApi
) {
    suspend fun getFolderList(): List<FolderModel> {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return folderApi.getFolders(cookie).data
    }

    suspend fun getFolderSimpleList(aid: Long): SimpleFolderModel {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return folderApi.getSimpleFolders(cookie, aid, context.getValue(UP_MID_KEY.name, 0L)).data
    }

    suspend fun getWatchLaterList(
        pn: Int = 1,
        ps: Int = 20
    ): BiliResponse<ToViewModel> {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return historyApi.getToView(
            cookie = cookie,
            pn = pn,
            ps = ps
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun folderMediaPagingFlow(
        mlid: Long
    ): Flow<PagingData<MediaItem>> {
        return flow {
            val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
            emit(
                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = {
                        FolderResourcePaging(
                            folderApi = folderApi,
                            cookie = cookie,
                            mlid = mlid
                        )
                    }
                ).flow
            )
        }.flattenConcat()
    }

    suspend fun addNewFolder(title: String, privacy: Boolean): Boolean {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return folderApi.addNewFolder(
            cookie = cookie,
            title = title,
            privacy = privacy,
            csrf = cookie.extractBiliJct()
        ).data != null
    }

    suspend fun addToView(
        aid: Long,
        bvid: String
    ): BiliResponseNoData {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return historyApi.addToView(
            aid = aid, bvid = bvid,
            cookie = cookie,
            csrf = cookie.extractBiliJct()
        )
    }

    suspend fun delToView(
        viewed: Boolean,
        aid: Long?,
    ): BiliResponseNoData {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return historyApi.delToView(
            aid = aid, viewed = viewed,
            cookie = cookie,
            csrf = cookie.extractBiliJct()
        )
    }

    suspend fun clearToView(): BiliResponseNoData {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return historyApi.clearToView(
            cookie = cookie,
            csrf = cookie.extractBiliJct()
        )
    }

    suspend fun folderDeal(
        aid: Long,
        addMediaIds: Set<Long>,
        delMediaIds: Set<Long>,
    ): BiliResponse<ModifyFavoriteModel>? {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return folderApi.modifyFavorite(
            aid = aid,
            addMediaIds = addMediaIds,
            delMediaIds = delMediaIds,
            cookie = cookie,
            biliJct = cookie.extractBiliJct()
        )
    }
}
package com.laohei.bili_tube.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.laohei.bili_sdk.apis.FolderApi
import com.laohei.bili_sdk.apis.HistoryApi
import com.laohei.bili_sdk.folder.GetFolder
import com.laohei.bili_sdk.history.GetWatchLater
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.folder.FolderMediaItem
import com.laohei.bili_sdk.module_v2.folder.FolderModel
import com.laohei.bili_sdk.module_v2.folder.SimpleFolderModel
import com.laohei.bili_sdk.module_v2.history.ToViewModel
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.core.UP_MID_KEY
import com.laohei.bili_tube.core.util.getValue
import com.laohei.bili_tube.dataStore
import com.laohei.bili_tube.presentation.playlist.FolderResourcePaging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow

class BiliPlaylistRepository(
    private val context: Context,
    private val getFolder: GetFolder,
    private val getWatchLater: GetWatchLater,
    private val historyApi: HistoryApi,
    private val folderApi: FolderApi
) {
    suspend fun getFolderList(): List<FolderModel>? {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return getFolder.folderList(cookie)?.data
    }

    suspend fun getFolderSimpleList(aid: Long): SimpleFolderModel? {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return getFolder.folderSimpleList(cookie, aid, context.getValue(UP_MID_KEY.name, 0L))?.data
    }

    suspend fun getWatchLaterList(ps: Int = 20): ToViewModel? {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return getWatchLater.watchLaterList(cookie = cookie, ps = ps)?.data
    }

    suspend fun getToViewList(
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
    fun getFolderResourcePager(
        mlid: Long
    ): Flow<PagingData<FolderMediaItem>> {
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
}
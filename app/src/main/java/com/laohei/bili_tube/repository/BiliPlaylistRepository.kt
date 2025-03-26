package com.laohei.bili_tube.repository

import android.content.Context
import com.laohei.bili_sdk.folder.GetFolder
import com.laohei.bili_sdk.history.GetWatchLater
import com.laohei.bili_sdk.module_v2.folder.FolderModel
import com.laohei.bili_sdk.module_v2.folder.FolderSimpleModel
import com.laohei.bili_sdk.module_v2.history.WatchLaterModel
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.core.UP_MID_KEY
import com.laohei.bili_tube.core.util.getValue
import com.laohei.bili_tube.dataStore
import kotlinx.coroutines.flow.firstOrNull

class BiliPlaylistRepository(
    private val context: Context,
    private val getFolder: GetFolder,
    private val getWatchLater: GetWatchLater
) {
    suspend fun getFolderList(): List<FolderModel>? {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return getFolder.folderList(cookie)?.data
    }

    suspend fun getFolderSimpleList(aid: Long): FolderSimpleModel? {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return getFolder.folderSimpleList(cookie, aid, context.getValue(UP_MID_KEY.name, 0L))?.data
    }

    suspend fun getWatchLaterList(ps: Int = 20): WatchLaterModel? {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return getWatchLater.watchLaterList(cookie = cookie, ps = ps)?.data
    }
}
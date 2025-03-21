package com.laohei.bili_tube.repository

import android.content.Context
import com.laohei.bili_sdk.folder.Folder
import com.laohei.bili_sdk.history.WatchLater
import com.laohei.bili_sdk.module_v2.folder.FolderModel
import com.laohei.bili_sdk.module_v2.history.WatchLaterModel
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.dataStore
import kotlinx.coroutines.flow.firstOrNull

class BiliPlaylistRepository(
    private val context: Context,
    private val folder: Folder,
    private val watchLater: WatchLater
) {
    suspend fun getFolderList(): List<FolderModel>? {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return folder.folderList(cookie)?.data
    }

    suspend fun getWatchLaterList(ps: Int = 20): WatchLaterModel? {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return watchLater.watchLaterList(cookie = cookie, ps = ps)?.data
    }
}
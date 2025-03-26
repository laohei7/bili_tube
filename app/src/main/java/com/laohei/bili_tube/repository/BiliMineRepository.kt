package com.laohei.bili_tube.repository

import android.content.Context
import com.laohei.bili_sdk.folder.GetFolder
import com.laohei.bili_sdk.history.GetHistory
import com.laohei.bili_sdk.history.GetWatchLater
import com.laohei.bili_sdk.module_v2.folder.FolderModel
import com.laohei.bili_sdk.module_v2.history.HistoryModel
import com.laohei.bili_sdk.module_v2.history.WatchLaterModel
import com.laohei.bili_sdk.module_v2.user.UserStatModel
import com.laohei.bili_sdk.user.GetUserInfo
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.dataStore
import kotlinx.coroutines.flow.firstOrNull

class BiliMineRepository(
    private val context: Context,
    private val getWatchLater: GetWatchLater,
    private val getHistory: GetHistory,
    private val getFolder: GetFolder,
    private val getUserInfo: GetUserInfo
) {
    suspend fun getWatchLaterList(ps: Int = 20): WatchLaterModel? {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return getWatchLater.watchLaterList(cookie = cookie, ps = ps)?.data
    }

    suspend fun getHistoryList(ps: Int = 20): HistoryModel? {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return getHistory.historyList(cookie = cookie, ps = ps)?.data
    }

    suspend fun getFolderList(): List<FolderModel>? {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return getFolder.folderList(cookie)?.data
    }

    suspend fun getUserStat(): UserStatModel? {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return getUserInfo.getUserStat(cookie)?.data
    }
}
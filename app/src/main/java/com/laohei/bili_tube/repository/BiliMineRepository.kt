package com.laohei.bili_tube.repository

import android.content.Context
import com.laohei.bili_sdk.apis.FolderApi
import com.laohei.bili_sdk.apis.HistoryApi
import com.laohei.bili_sdk.apis.UserApi
import com.laohei.bili_sdk.module_v2.folder.FolderModel
import com.laohei.bili_sdk.module_v2.history.HistoryModel
import com.laohei.bili_sdk.module_v2.history.ToViewModel
import com.laohei.bili_sdk.module_v2.user.UserStatModel
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.dataStore
import kotlinx.coroutines.flow.firstOrNull

class BiliMineRepository(
    private val context: Context,
    private val historyApi: HistoryApi,
    private val folderApi: FolderApi,
    private val userApi: UserApi,
) {
    suspend fun getWatchLaterList(ps: Int = 20): ToViewModel {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return historyApi.getToView(cookie = cookie, ps = ps).data
    }

    suspend fun getHistoryList(ps: Int = 20): HistoryModel {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return historyApi.getHistories(cookie = cookie, ps = ps).data
    }

    suspend fun getFolderList(): List<FolderModel> {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return folderApi.getFolders(cookie).data
    }

    suspend fun getUserStat(): UserStatModel {
        val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
        return userApi.getUserStat(cookie).data
    }
}
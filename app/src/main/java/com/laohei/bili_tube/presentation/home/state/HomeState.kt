package com.laohei.bili_tube.presentation.home.state

import androidx.paging.PagingData
import com.laohei.bili_sdk.module_v2.bangumi.BangumiItem
import com.laohei.bili_sdk.module_v2.folder.SimpleFolderItem
import com.laohei.bili_tube.model.BangumiFilterModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class HomeState(
    val folders: List<SimpleFolderItem> = emptyList(),
    val currentAid: Long? = null,
    val currentBvid: String? = null,
    val isShowMenuSheet: Boolean = false,
    val isShowFolderSheet: Boolean = false,
    val bangumiFilterModel: BangumiFilterModel = BangumiFilterModel(),
    val animationFilterModel: BangumiFilterModel = BangumiFilterModel(),
    val bangumis: Flow<PagingData<BangumiItem>> = flow { PagingData.empty<BangumiItem>() },
    val animations: Flow<PagingData<BangumiItem>> = flow { PagingData.empty<BangumiItem>() },
    val isShowAddFolder: Boolean = false,
    val folderName: String = "",
    val isPrivate: Boolean = false
)

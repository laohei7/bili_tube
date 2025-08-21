package com.laohei.bili_tube.features.main.home

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.pager.PagerState
import androidx.paging.PagingData
import com.laohei.bili_sdk.module_v2.bangumi.BangumiItem
import com.laohei.bili_sdk.module_v2.folder.SimpleFolderItem
import com.laohei.bili_sdk.module_v2.hot.HotItem
import com.laohei.bili_sdk.module_v2.recommend.RecommendItem
import com.laohei.bili_tube.R
import com.laohei.bili_tube.model.BangumiFilterModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class HomeState(
    val pager: PagerState = PagerState { HomeTabs.size },
    val tabGridStates: List<LazyGridState> = List(HomeTabs.size) { LazyGridState() },

    // video and anime net data by paging
    val recommends: Flow<PagingData<RecommendItem>> = flow { PagingData.empty<RecommendItem>() },
    val hots: Flow<PagingData<HotItem>> = flow { PagingData.empty<HotItem>() },
    val bangumis: Flow<PagingData<BangumiItem>> = flow { PagingData.empty<BangumiItem>() },
    val animeList: Flow<PagingData<BangumiItem>> = flow { PagingData.empty<BangumiItem>() },

    // show dialog or sheet
    val showMenuSheet: Boolean = false,
    val showFolderSheet: Boolean = false,
    val showAddFolder: Boolean = false,

    // selected item info
    val selectedAid: Long ?= null,
    val selectedBvid: String? = null,

    // folder
    val newFolderName: String = "",
    val folders: List<SimpleFolderItem> = emptyList(),
    val isPrivateFolder: Boolean = false,

    // anime or bangumi filter
    val bangumiFilter: BangumiFilterModel = BangumiFilterModel(),
    val animeFilter: BangumiFilterModel = BangumiFilterModel(),
)

internal val HomeTabs =
    listOf(R.string.str_recommend, R.string.str_hots, R.string.str_bangumi, R.string.str_anime)

package com.laohei.bili_tube.features.search

import androidx.paging.PagingData
import com.laohei.bili_tube.R
import com.laohei.bili_tube.model.UIModel
import com.laohei.bili_tube.room.entity.SearchHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class SearchUIState(
    // category tab ids
    val tabs: List<Int> = SearchTabs,
    val expanded: Boolean = true,
    val keyword: String = "",
    val isSearching: Boolean = false,
    val results: Flow<PagingData<UIModel<out Any?>>> = flowOf(PagingData.empty()),
    val videos: Flow<PagingData<UIModel<out Any?>>> = flowOf(PagingData.empty()),
    val bangumis: Flow<PagingData<UIModel<out Any?>>> = flowOf(PagingData.empty()),
    val fts: Flow<PagingData<UIModel<out Any?>>> = flowOf(PagingData.empty()),
    val searchHistories: Flow<PagingData<SearchHistory>> = flowOf(PagingData.empty())
)

internal val SearchTabs = listOf(
    R.string.str_integrated,
    R.string.str_video,
    R.string.str_bangumi,
    R.string.str_moive,
)
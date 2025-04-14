package com.laohei.bili_tube.presentation.search

import androidx.paging.PagingData
import com.laohei.bili_tube.model.UIModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class SearchState(
    val expanded: Boolean = false,
    val keyword: String = "",
    val isSearching: Boolean = false,
    val results: Flow<PagingData<UIModel<out Any?>>> = flowOf(PagingData.empty()),
    val videos: Flow<PagingData<UIModel<out Any?>>> = flowOf(PagingData.empty()),
    val bangumis: Flow<PagingData<UIModel<out Any?>>> = flowOf(PagingData.empty()),
    val fts: Flow<PagingData<UIModel<out Any?>>> = flowOf(PagingData.empty()),
)
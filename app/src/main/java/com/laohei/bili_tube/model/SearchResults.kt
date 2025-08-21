package com.laohei.bili_tube.model

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

data class SearchResults(
    val all: Flow<PagingData<UIModel<out Any?>>>,
    val videos: Flow<PagingData<UIModel<out Any?>>>,
    val bangumis: Flow<PagingData<UIModel<out Any?>>>,
    val movies: Flow<PagingData<UIModel<out Any?>>>
)

package com.laohei.bili_tube.presentation.home.recommend

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.laohei.bili_tube.repository.BiliHomeRepository

class RecommendViewModel(
    biliHomeRepository: BiliHomeRepository,
) : ViewModel() {
    val gridState = LazyGridState()

    val randomVideos = biliHomeRepository.getPagedRecommendVideo()
        .cachedIn(viewModelScope)
}
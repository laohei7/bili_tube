package com.laohei.bili_tube.features.main.home.recommend

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.laohei.bili_tube.data.repository.BiliHomeRepository

class RecommendViewModel(
    biliHomeRepository: BiliHomeRepository,
) : ViewModel() {
    val gridState = LazyGridState()

    val randomVideos = biliHomeRepository.getRecommendPager()
        .cachedIn(viewModelScope)
}
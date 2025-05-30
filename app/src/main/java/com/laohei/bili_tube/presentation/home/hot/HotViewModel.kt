package com.laohei.bili_tube.presentation.home.hot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.laohei.bili_tube.repository.BiliHomeRepository

class HotViewModel(
    biliHomeRepository: BiliHomeRepository,
) : ViewModel() {
    val hotVideos = biliHomeRepository.getHotPager()
        .cachedIn(viewModelScope)
}
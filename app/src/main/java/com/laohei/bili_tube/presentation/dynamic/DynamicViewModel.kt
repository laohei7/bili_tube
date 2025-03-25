package com.laohei.bili_tube.presentation.dynamic

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.laohei.bili_tube.repository.BiliDynamicRepository

class DynamicViewModel(
    dynamicRepository: BiliDynamicRepository
) : ViewModel() {
    val dynamicList = dynamicRepository.getDynamicList().cachedIn(viewModelScope)
//    val gridState = LazyGridState()
    val gridState = LazyStaggeredGridState()
}
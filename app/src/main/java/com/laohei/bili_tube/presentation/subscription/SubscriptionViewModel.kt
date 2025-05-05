package com.laohei.bili_tube.presentation.subscription

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.laohei.bili_tube.repository.BiliSubscriptionRepository

class SubscriptionViewModel(
    dynamicRepository: BiliSubscriptionRepository
) : ViewModel() {
    val dynamicList = dynamicRepository.getDynamicList().cachedIn(viewModelScope)
//    val gridState = LazyGridState()
    val gridState = LazyStaggeredGridState()
}
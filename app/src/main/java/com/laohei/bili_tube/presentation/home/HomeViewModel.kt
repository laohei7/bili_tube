package com.laohei.bili_tube.presentation.home

import android.content.Context
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.pager.PagerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.laohei.bili_tube.R
import com.laohei.bili_tube.repository.BiliHomeRepository

class HomeViewModel(
    context: Context,
    biliHomeRepository: BiliHomeRepository,
) : ViewModel() {
    val tabs = listOf(
        context.getString(R.string.str_recommend),
        context.getString(R.string.str_hots),
        context.getString(R.string.str_anime),
    )
    val pagerState = PagerState { tabs.size }

    val gridStates = List(tabs.size) { LazyGridState() }

    val hotVideos = biliHomeRepository.getPagedHotVideo()
        .cachedIn(viewModelScope)

    val randomVideos = biliHomeRepository.getPagedRecommendVideo()
        .cachedIn(viewModelScope)

    val timeline = biliHomeRepository.getTimelineEpisode()
        .cachedIn(viewModelScope)
}
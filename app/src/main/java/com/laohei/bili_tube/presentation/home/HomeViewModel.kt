package com.laohei.bili_tube.presentation.home

import android.content.Context
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.pager.PagerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.laohei.bili_tube.R
import com.laohei.bili_tube.component.video.VideoAction
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.repository.BiliHomeRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    context: Context,
    private val biliHomeRepository: BiliHomeRepository,
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

    fun videoMenuActionHandle(action: VideoAction.VideoMenuAction) {
        when (action) {
            is VideoAction.VideoMenuAction.CollectActionByAid -> {
                videoFolderDeal(action.aid, action.addAids, action.delAids)
            }

            else -> {}
        }
    }

    private fun videoFolderDeal(
        aid: Long,
        addMediaIds: Set<Long>,
        delMediaIds: Set<Long>,
    ) {
        viewModelScope.launch {
            biliHomeRepository.videoFolderDeal(
                aid = aid,
                addMediaIds = addMediaIds,
                delMediaIds = delMediaIds
            )?.apply {
                val message = if (code == 0) "添加成功" else "添加失败"
                EventBus.send(Event.AppEvent.ToastEvent(message = message))
            }
        }
    }
}
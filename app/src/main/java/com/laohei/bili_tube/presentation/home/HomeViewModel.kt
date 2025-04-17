package com.laohei.bili_tube.presentation.home

import android.content.Context
import android.util.Log
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.pager.PagerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.laohei.bili_tube.R
import com.laohei.bili_tube.component.video.VideoAction
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.presentation.home.state.DefaultHomePageManager
import com.laohei.bili_tube.presentation.home.state.HomePageAction
import com.laohei.bili_tube.presentation.home.state.HomePageManager
import com.laohei.bili_tube.repository.BiliHomeRepository
import com.laohei.bili_tube.repository.BiliPlaylistRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class HomeViewModel(
    context: Context,
    private val biliHomeRepository: BiliHomeRepository,
    private val biliPlaylistRepository: BiliPlaylistRepository,
    private val defaultHomePageManager: DefaultHomePageManager
) : ViewModel(), HomePageManager by defaultHomePageManager {
    companion object {
        private val TAG = HomeViewModel::class.simpleName
    }

    val tabs = listOf(
        context.getString(R.string.str_recommend),
        context.getString(R.string.str_hots),
        context.getString(R.string.str_bangumi),
        context.getString(R.string.str_anime),
    )
    val pagerState = PagerState { tabs.size }

    val gridStates = List(tabs.size) { LazyGridState() }

    val hotVideos = biliHomeRepository.getPagedHotVideo()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
        .cachedIn(viewModelScope)

    val randomVideos = biliHomeRepository.getPagedRecommendVideo()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
        .cachedIn(viewModelScope)

    val timeline = biliHomeRepository.getTimelineEpisode()
        .cachedIn(viewModelScope)

    init {
        loadBangumis()
        loadAnimations()
    }

    fun onVideoMenuActionHandle(action: VideoAction.VideoMenuAction) {
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
            biliHomeRepository.folderDeal(
                aid = aid,
                addMediaIds = addMediaIds,
                delMediaIds = delMediaIds
            )?.apply {
                val message = if (code == 0) "添加成功" else "添加失败"
                EventBus.send(Event.AppEvent.ToastEvent(message = message))
            }
        }
    }

    fun onVideoSheetActionHandle(action: VideoAction.VideoSheetAction) {
        when (action) {
            is VideoAction.VideoSheetAction.PlaylistAction -> {
                viewModelScope.launch {
                    getFolderSimpleList(action.aid)
                }
            }

            is VideoAction.VideoSheetAction.AddToViewAction -> {
                viewModelScope.launch {
                    addToView(action.aid, action.bvid)
                }
            }
        }
    }

    private suspend fun getFolderSimpleList(aid: Long) {
        biliPlaylistRepository.getFolderSimpleList(aid)?.let { data ->
            updateState(
                homeState.value.copy(
                    folders = data.list,
                    isShowMenuSheet = false,
                    isShowFolderSheet = true
                )
            )
        }
    }

    private suspend fun addToView(aid: Long, bvid: String) {
        biliHomeRepository.addToView(aid, bvid)?.apply {
            Log.d(TAG, "addToView: $this")
            val message = if (code == 0) "添加成功" else "添加失败"
            EventBus.send(Event.AppEvent.ToastEvent(message = message))
            updateState(homeState.value.copy(isShowMenuSheet = false))
        }
    }

    override fun homeActionHandle(action: HomePageAction) {
        defaultHomePageManager.homeActionHandle(action)
        when (action) {
            is HomePageAction.AnimeFilterAction -> {
                if (action.isAnime) {
                    loadAnimations()
                } else {
                    loadBangumis()
                }
            }

            else -> {}
        }
    }

    private fun loadBangumis() {
        viewModelScope.launch {
            updateState(
                homeState.value.copy(
                    bangumis = biliHomeRepository.getBangumis(homeState.value.bangumiFilterModel)
                        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
                        .cachedIn(viewModelScope)
                )
            )
        }
    }

    private fun loadAnimations() {
        viewModelScope.launch {
            updateState(
                homeState.value.copy(
                    animations = biliHomeRepository.getAnimations(homeState.value.bangumiFilterModel)
                        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
                        .cachedIn(viewModelScope)
                )
            )
        }
    }
}
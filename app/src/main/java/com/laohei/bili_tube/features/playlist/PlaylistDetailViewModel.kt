package com.laohei.bili_tube.features.playlist

import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.history.ToViewModel
import com.laohei.bili_tube.features.playlist.data.repository.BiliPlaylistRepository
import com.laohei.bili_tube.nav.AppRoute
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    private val biliPlaylistRepository: BiliPlaylistRepository,
    private val param: AppRoute.PlaylistDetail
) : ViewModel() {
    private val _mState = MutableStateFlow(PlaylistDetailState())
    val state = _mState.onStart {
        if (param.isToView) {
            initToViews()
        } else {
            initFolderResources()
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _mState.value
    )

    private fun initFolderResources() {
        viewModelScope.launch {
            val pager = biliPlaylistRepository.getFolderResourcePager(mlid = param.fid!!)
                .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
                .cachedIn(viewModelScope)
            _mState.update { it.copy(folderResources = pager) }
        }
    }

    private fun initToViews() {
        viewModelScope.launch {
            val deferredList = mutableListOf<Deferred<BiliResponse<ToViewModel>>>()
            for (pn in 1..5) {
                val deferred = async {
                    biliPlaylistRepository.getToViewList(pn = pn)
                }
                deferredList.add(deferred)
            }
            val results = deferredList.awaitAll()
            _mState.update {
                it.copy(
                    toViewList = results.fastMap { item -> item.data.list }.flatten()
                )
            }
        }
    }

    fun reorderItem(from: Int, to: Int) {
        _mState.update {
            it.copy(
                toViewList = it.toViewList.toMutableList().apply {
                    add(to, removeAt(from))
                }
            )
        }
    }
}
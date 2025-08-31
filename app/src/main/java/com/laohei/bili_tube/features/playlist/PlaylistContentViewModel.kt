package com.laohei.bili_tube.features.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.laohei.bili_tube.data.repository.BiliPlaylistRepository
import com.laohei.bili_tube.nav.AppRoute
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class PlaylistContentViewModel(
    private val biliPlaylistRepository: BiliPlaylistRepository,
    private val param: AppRoute.PlaylistContent
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlaylistContentUIState())
    val uiState = _uiState.onStart {
        if (param.isToView) {
            initWatchLater()
        } else {
            initFolderMedia()
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _uiState.value
    )

    private fun initFolderMedia() {
        val folderFlow = biliPlaylistRepository.folderMediaPagingFlow(mlid = param.fid!!)
            .cachedIn(viewModelScope)
        _uiState.update { it.copy(folderMediaFlow = folderFlow) }
    }

    private suspend fun initWatchLater() {
        val responses = coroutineScope {
            (1..5).map { pn ->
                async { biliPlaylistRepository.getWatchLaterList(pn) }
            }.awaitAll()
        }

        val watchLaterItems = responses
            .map { it.data.list }
            .flatten()

        _uiState.update { state ->
            state.copy(watchLaterList = watchLaterItems)
        }
    }

    fun reorderItem(from: Int, to: Int) {
        _uiState.update {
            it.copy(
                watchLaterList = it.watchLaterList.toMutableList().apply {
                    add(to, removeAt(from))
                }
            )
        }
    }
}
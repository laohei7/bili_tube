package com.laohei.bili_tube.features.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
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
import kotlinx.coroutines.launch

class PlaylistContentViewModel(
    private val playlistRepository: BiliPlaylistRepository,
    param: AppRoute.PlaylistContent
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlaylistContentUIState(param = param))
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
        val param = _uiState.value.param
        val folderFlow = playlistRepository.folderMediaPagingFlow(mlid = param.fid!!)
            .cachedIn(viewModelScope)
        _uiState.update { it.copy(folderMediaFlow = folderFlow) }
    }

    private suspend fun initWatchLater() {
        val responses = coroutineScope {
            (1..5).map { pn ->
                async { playlistRepository.getWatchLaterList(pn) }
            }.awaitAll()
        }

        val watchLaterItems = responses
            .map { it.data.list }
            .flatten()

        _uiState.update { state ->
            state.copy(watchLaterList = watchLaterItems)
        }
    }

    fun onPlaylistContentAction(action: PlaylistContentAction) {
        when (action) {
            is PlaylistContentAction.DelToView -> delToView(action)
            PlaylistContentAction.ClearToView -> clearToView()
            is PlaylistContentAction.ShareLink -> shareLink(action)
        }
    }

    private fun shareLink(action: PlaylistContentAction.ShareLink) {
        _uiState.update { state ->
            state.copy(isShareLinkVisible = action.flag, shareLink = action.link)
        }
    }

    private fun clearToView() {
        viewModelScope.launch {
            val response = playlistRepository.clearToView()
            if (response.code != 0) {
                EventBus.send(Event.AppEvent.ToastTextEvent(message = response.message))
            } else {
                initWatchLater()
                EventBus.send(Event.AppEvent.ToastEvent(R.string.str_clear_success))
            }
        }
    }

    private fun delToView(action: PlaylistContentAction.DelToView) {
        viewModelScope.launch {
            val response = playlistRepository.delToView(
                aid = action.aid,
                viewed = action.viewed
            )
            if (response.code != 0) {
                EventBus.send(Event.AppEvent.ToastTextEvent(message = response.message))
                return@launch
            }
            coroutineScope {
                launch { refreshFolderMeta() }
                launch {
                    if (action.viewed) {
                        initWatchLater()
                    } else {
                        _uiState.update { state ->
                            state.copy(
                                watchLaterList = state.watchLaterList.filter {
                                    it.aid != action.aid
                                }
                            )
                        }
                    }
                }
            }

            EventBus.send(Event.AppEvent.ToastEvent(R.string.str_delete_success))
        }
    }

    private suspend fun refreshFolderMeta() {
        val param = _uiState.value.param
        if (param.isToView) {
            val data = playlistRepository.getWatchLaterList().data
            val newCount = data.count
            val newCover = data.list.firstOrNull()?.pic ?: param.cover
            _uiState.update { state ->
                state.copy(
                    param = param.copy(count = newCount, cover = newCover)
                )
            }
        } else {
            val folders = playlistRepository.getFolderList()
            val folder = folders.find { it.id == 1 }?.mediaListResponse?.list
                ?.find { it.id == param.fid }
            val newCount = folder?.mediaCount ?: param.count
            val newCover = folder?.cover ?: param.cover
            _uiState.update { state ->
                state.copy(
                    param = param.copy(count = newCount, cover = newCover)
                )
            }
        }
    }

}
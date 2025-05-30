package com.laohei.bili_tube.presentation.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laohei.bili_tube.repository.BiliPlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistViewModel(
    private val playlistRepository: BiliPlaylistRepository
) : ViewModel() {

    private val _mState = MutableStateFlow(PlaylistState())
    val state = _mState
        .onStart {
            refresh()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _mState.value
        )

    suspend fun refresh() = withContext(Dispatchers.IO) {
        _mState.update { it.copy(isLoading = true) }
        launch { getFolderList() }
        launch { getWatchLaterCover() }
    }

    private suspend fun getFolderList() {
        playlistRepository.getFolderList().apply {
            _mState.update {
                it.copy(
                    isLoading = false,
                    folderList = this.sortedByDescending { it.id }
                )
            }
        }
    }

    private suspend fun getWatchLaterCover() {
        playlistRepository.getToViewList(ps = 3).apply {
            if (this.data.list.isEmpty()) {
                return@apply
            }
            _mState.update { it.copy(watchLaterCover = this.data.list.first().pic) }
        }
    }
}
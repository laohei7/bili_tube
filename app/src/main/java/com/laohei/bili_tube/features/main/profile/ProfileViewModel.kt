package com.laohei.bili_tube.features.main.profile

import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.core.extension.withRefreshing
import com.laohei.bili_tube.data.repository.BiliPlaylistRepository
import com.laohei.bili_tube.data.repository.BiliProfileRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userProfileRepository: BiliProfileRepository,
    private val userPlaylistRepository: BiliPlaylistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState = _uiState
        .onStart {
            initData()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _uiState.value
        )

    private suspend fun initData() = coroutineScope {
        launch { getUserStat() }
        launch { getShortHistoryList() }
        launch { getShortWatchlist() }
        launch { getFolderList() }
    }

    private suspend fun getUserStat() {
        val userStat = userProfileRepository.getUserStat()
        _uiState.update {
            it.copy(
                following = userStat.following,
                follower = userStat.follower,
                dynamicCount = userStat.dynamicCount
            )
        }
    }

    private suspend fun getShortHistoryList() {
        val historyList = userProfileRepository.getHistoryList()
        _uiState.update { it.copy(historyList = historyList.list) }
    }

    private suspend fun getShortWatchlist() {
        val watchlist = userProfileRepository.getWatchLaterList(ps = 3)
        _uiState.update {
            it.copy(watchlist = watchlist.list, watchLaterCount = watchlist.count)
        }
    }

    private suspend fun getFolderList() {
        val folders = userProfileRepository.getFolderList()
            .fastFilter { it.id == 1 }
            .firstOrNull()
            ?.mediaListResponse
            ?.list
        folders?.let { list -> _uiState.update { it.copy(folderList = list) } }
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            withRefreshing { initData() }
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun onProfileAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.FolderCreatedUIAction -> displayCreateFolder(action)

            ProfileAction.RefreshAction -> refresh()

            is ProfileAction.HistoryOptionsVisible -> displayHistoryOptions(action)
            is ProfileAction.FolderOptionsVisible -> displayFolderOptions(action)
        }
    }

    private fun displayHistoryOptions(action: ProfileAction.HistoryOptionsVisible) {
        _uiState.update { it.copy(isHistoryOptionsVisible = action.flag, selectedKid = action.kid) }
    }

    private fun displayFolderOptions(action: ProfileAction.FolderOptionsVisible) {
        _uiState.update { it.copy(isFolderOptionsVisible = action.flag, selectedFid = action.fid) }
    }

    private fun displayCreateFolder(action: ProfileAction.FolderCreatedUIAction) {
        _uiState.update { it.copy(isShowAddFolder = action.flag) }
    }

    fun onFolderNameChange(value: String) {
        _uiState.update { it.copy(folderName = value) }
    }

    fun onPrivateChange(value: Boolean) {
        _uiState.update { it.copy(isPrivateFolder = value) }
    }

    fun addNewFolder() {
        viewModelScope.launch {
            val folderName = _uiState.value.folderName
            val privacy = _uiState.value.isPrivateFolder
            if (folderName.isBlank()) {
                EventBus.send(
                    Event.AppEvent.ToastEvent(R.string.str_folder_name_error)
                )
                return@launch
            }
            val success = userPlaylistRepository.addNewFolder(
                title = folderName,
                privacy = privacy
            )
            if (success) {
                refresh()
                _uiState.update { it.copy(isShowAddFolder = false) }
                EventBus.send(
                    Event.AppEvent.ToastEvent(R.string.str_folder_created_success)
                )
            } else {
                EventBus.send(
                    Event.AppEvent.ToastEvent(R.string.str_folder_created_failed)
                )
            }
        }
    }

    fun delHistory() {
        val selectedKid = _uiState.value.selectedKid
        if (selectedKid == null) return
        viewModelScope.launch {
            val response = userProfileRepository.delHistory(selectedKid)
            if (response.code != 0) {
                EventBus.send(Event.AppEvent.ToastTextEvent(message = response.message))
                return@launch
            }
            EventBus.send(Event.AppEvent.ToastEvent(R.string.str_delete_success))
            getShortHistoryList()
            onProfileAction(ProfileAction.HistoryOptionsVisible(false))
        }
    }

}
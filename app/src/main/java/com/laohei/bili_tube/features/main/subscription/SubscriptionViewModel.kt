package com.laohei.bili_tube.features.main.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.features.main.subscription.data.repository.BiliSubscriptionRepository
import com.laohei.bili_tube.features.playlist.data.repository.BiliPlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SubscriptionViewModel(
    private val subscriptionRepository: BiliSubscriptionRepository,
    private val playlistRepository: BiliPlaylistRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SubscriptionUIState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _uiState.value
    )

    val subscriptions = subscriptionRepository.getDynamicList()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
        .cachedIn(viewModelScope)

    private var _selectedAid: Long? = null
    private var _selectedBvid: String? = null


    fun onSubscriptionAction(action: SubscriptionAction) {
        when (action) {
            is SubscriptionAction.MenuUIAction -> displayMenu(action)

            SubscriptionAction.AddToViewAction -> addToView()

            is SubscriptionAction.FolderUIAction -> displayFolder(action)

            SubscriptionAction.NoneAction -> {}

            is SubscriptionAction.FolderCreatedUIAction -> displayCreateFolder(action)

            is SubscriptionAction.AddToFoldersAction -> addToFolder(action)
        }
    }

    private fun displayMenu(action: SubscriptionAction.MenuUIAction) {
        _selectedBvid = action.bvid
        _selectedAid = action.aid
        _uiState.update { it.copy(showMenuSheet = action.flag) }
    }

    private fun displayFolder(action: SubscriptionAction.FolderUIAction) {
        displayMenu(
            SubscriptionAction.MenuUIAction(
                false,
                if (action.flag) _selectedAid else null,
                if (action.flag) _selectedBvid else null
            )
        )
        if (action.flag.not()) {
            _uiState.update { it.copy(showFolderSheet = action.flag) }
            return
        }
        _selectedAid?.let { getFolderSimpleList(it) } ?: run {
            displayMenu(SubscriptionAction.MenuUIAction(false))
        }
    }

    private fun displayCreateFolder(action: SubscriptionAction.FolderCreatedUIAction) {
        _uiState.update { it.copy(showAddFolder = action.flag) }
    }

    private fun addToView() {
        viewModelScope.launch {
            if (_selectedAid == null && _selectedBvid == null) {
                return@launch
            }
            playlistRepository.addToView(_selectedAid!!, _selectedBvid!!).apply {
                val messageId = when {
                    code == 0 -> R.string.str_add_to_vew_success
                    else -> R.string.str_add_to_view_failed
                }
                EventBus.send(Event.AppEvent.ToastEvent(messageId = messageId))
                onSubscriptionAction(SubscriptionAction.MenuUIAction(false))
            }
        }
    }

    private fun addToFolder(action: SubscriptionAction.AddToFoldersAction) {
        _selectedAid?.let {
            videoFolderDeal(
                aid = it,
                addMediaIds = action.addAids,
                delMediaIds = action.delAids
            )
        } ?: run {
            displayFolder(SubscriptionAction.FolderUIAction(false))
        }
    }

    fun onFolderNameChange(value: String) {
        _uiState.update { it.copy(newFolderName = value) }
    }

    fun onPrivateChange(value: Boolean) {
        _uiState.update { it.copy(isPrivateFolder = value) }
    }


    fun addNewFolder() {
        viewModelScope.launch {
            val folderName = uiState.value.newFolderName
            val privacy = uiState.value.isPrivateFolder
            if (folderName.isBlank()) {
                EventBus.send(
                    Event.AppEvent.ToastEvent(R.string.str_folder_name_error)
                )
                return@launch
            }
            val success = playlistRepository.addNewFolder(
                title = folderName,
                privacy = privacy
            )
            if (success) {
                displayCreateFolder(SubscriptionAction.FolderCreatedUIAction(false))
                EventBus.send(
                    Event.AppEvent.ToastEvent(R.string.str_folder_created_success)
                )
            } else {
                EventBus.send(
                    Event.AppEvent.ToastEvent(R.string.str_foler_created_failed)
                )
            }
        }
    }

    private fun getFolderSimpleList(aid: Long) {
        viewModelScope.launch {
            playlistRepository.getFolderSimpleList(aid).let { data ->
                _uiState.update {
                    it.copy(
                        folders = data.list,
                        showMenuSheet = false,
                        showFolderSheet = true
                    )
                }
            }
        }
    }

    private fun videoFolderDeal(
        aid: Long,
        addMediaIds: Set<Long>,
        delMediaIds: Set<Long>,
    ) {
        viewModelScope.launch {
            playlistRepository.folderDeal(
                aid = aid,
                addMediaIds = addMediaIds,
                delMediaIds = delMediaIds
            )?.apply {
                val messageId =
                    if (code == 0) R.string.str_add_to_folder_success else R.string.str_add_to_folder_failed
                EventBus.send(Event.AppEvent.ToastEvent(messageId = messageId))
            }
            displayFolder(SubscriptionAction.FolderUIAction(false))
        }
    }
}
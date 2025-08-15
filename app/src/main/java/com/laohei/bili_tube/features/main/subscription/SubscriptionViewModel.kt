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
    private val mSubscriptionState = MutableStateFlow(SubscriptionState())
    val subscriptionState = mSubscriptionState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        mSubscriptionState.value
    )

    val subscriptions = subscriptionRepository.getDynamicList()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
        .cachedIn(viewModelScope)

    private var selectedAid: Long? = null
    private var selectedBvid: String? = null


    fun onSubscriptionAction(action: SubscriptionAction) {
        when (action) {
            is SubscriptionAction.MenuUIAction -> {
                selectedBvid = action.bvid
                selectedAid = action.aid
                mSubscriptionState.update { it.copy(showMenuSheet = action.flag) }
            }

            SubscriptionAction.AddToViewAction -> {
                if (selectedAid == null && selectedBvid == null) {
                    return
                }
                addToView(selectedAid!!, selectedBvid!!)
            }

            is SubscriptionAction.FolderUIAction -> {
                onSubscriptionAction(
                    SubscriptionAction.MenuUIAction(
                        false,
                        if (action.flag) selectedAid else null,
                        if (action.flag) selectedBvid else null
                    )
                )
                if (action.flag.not()) {
                    mSubscriptionState.update { it.copy(showFolderSheet = action.flag) }
                    return
                }
                selectedAid?.let { getFolderSimpleList(it) } ?: run {
                    onSubscriptionAction(SubscriptionAction.MenuUIAction(false))
                }
            }

            SubscriptionAction.NoneAction -> {}

            is SubscriptionAction.FolderCreatedUIAction -> {
                mSubscriptionState.update { it.copy(showAddFolder = action.flag) }
            }

            is SubscriptionAction.AddToFoldersAction -> {
                selectedAid?.let {
                    videoFolderDeal(
                        aid = it,
                        addMediaIds = action.addAids,
                        delMediaIds = action.delAids
                    )
                } ?: run {
                    onSubscriptionAction(SubscriptionAction.FolderUIAction(false))
                }
            }
        }
    }

    private fun addToView(aid: Long, bvid: String) {
        viewModelScope.launch {
            playlistRepository.addToView(aid, bvid).apply {
                val messageId = when {
                    code == 0 -> R.string.str_add_to_vew_success
                    else -> R.string.str_add_to_view_failed
                }
                EventBus.send(Event.AppEvent.ToastEvent(messageId = messageId))
                onSubscriptionAction(SubscriptionAction.MenuUIAction(false))
            }
        }
    }

    fun onFolderNameChanged(value: String) {
        mSubscriptionState.update { it.copy(newFolderName = value) }
    }

    fun onPrivateChanged(value: Boolean) {
        mSubscriptionState.update { it.copy(isPrivateFolder = value) }
    }


    fun addNewFolder() {
        viewModelScope.launch {
            val folderName = subscriptionState.value.newFolderName
            val privacy = subscriptionState.value.isPrivateFolder
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
                onSubscriptionAction(SubscriptionAction.FolderCreatedUIAction(false))
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
                mSubscriptionState.update {
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
            onSubscriptionAction(SubscriptionAction.FolderUIAction(false))
        }
    }
}
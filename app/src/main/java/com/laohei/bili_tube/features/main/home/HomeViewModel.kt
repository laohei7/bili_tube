package com.laohei.bili_tube.features.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.features.main.home.data.repository.BiliHomeRepository
import com.laohei.bili_tube.features.playlist.data.repository.BiliPlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val biliHomeRepository: BiliHomeRepository,
    private val biliPlaylistRepository: BiliPlaylistRepository,
) : ViewModel() {

    companion object {
        private const val DBG = true
        private val TAG = HomeViewModel::class.simpleName
    }

    private val mHomeState = MutableStateFlow(HomeState())
    val homeState = mHomeState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        mHomeState.value
    )

    val recommends = biliHomeRepository.getRecommendPager()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
        .cachedIn(viewModelScope)

    val hots = biliHomeRepository.getHotPager()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
        .cachedIn(viewModelScope)

    var bangumis = biliHomeRepository.getBangumis(homeState.value.bangumiFilter)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
        .cachedIn(viewModelScope)
        private set

    var animations = biliHomeRepository.getAnimations(homeState.value.animeFilter)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
        .cachedIn(viewModelScope)
        private set


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
                EventBus.send(Event.AppEvent.ToastTextEvent(message = message))
            }
        }
    }

    private suspend fun getFolderSimpleList(aid: Long) {
        biliPlaylistRepository.getFolderSimpleList(aid).let { data ->
            mHomeState.update {
                it.copy(
                    folders = data.list,
                    showMenuSheet = false,
                    showFolderSheet = true
                )
            }
        }
    }

    private suspend fun addToView(aid: Long, bvid: String) {
        biliHomeRepository.addToView(aid, bvid).apply {
            val messageId = when {
                code == 0 -> R.string.str_add_to_vew_success
                else -> R.string.str_add_to_view_failed
            }
            EventBus.send(Event.AppEvent.ToastEvent(messageId = messageId))
            onHomeAction(HomeAction.MenuSheetUIAction(false))
        }
    }

    fun onHomeAction(action: HomeAction) {
        when (action) {
            is HomeAction.AnimeFilterAction -> {
                when {
                    action.isAnime -> {
                        mHomeState.update {
                            it.copy(
                                animeFilter = it.animeFilter.update(
                                    action.key,
                                    action.value
                                )
                            )
                        }
                        loadAnimations()
                    }

                    else -> {
                        mHomeState.update {
                            it.copy(
                                bangumiFilter = it.bangumiFilter.update(
                                    action.key,
                                    action.value
                                )
                            )
                        }
                        loadBangumis()
                    }
                }
            }

            is HomeAction.FolderCreatedUIAction -> {
                mHomeState.update {
                    it.copy(
                        showFolderSheet = action.flag.not(),
                        showAddFolder = action.flag
                    )
                }
            }

            is HomeAction.FolderSheetUIAction -> {
                action.aid?.let {
                    viewModelScope.launch {
                        getFolderSimpleList(action.aid)
                    }
                } ?: run {
                    mHomeState.update {
                        it.copy(showFolderSheet = action.flag)
                    }
                }
            }

            is HomeAction.MenuSheetUIAction -> {
                mHomeState.update {
                    it.copy(
                        showMenuSheet = action.flag,
                        selectedAid = action.aid,
                        selectedBvid = action.bvid
                    )
                }
            }

            is HomeAction.AddToViewAction -> {
                viewModelScope.launch {
                    addToView(action.aid, action.bvid)
                }
            }

            is HomeAction.AddToFoldersAction -> {
                videoFolderDeal(action.aid, action.addAids, action.delAids)
            }

            HomeAction.NoneAction -> {}
        }
    }

    private fun loadBangumis() {
        bangumis = biliHomeRepository.getBangumis(homeState.value.bangumiFilter)
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
            .cachedIn(viewModelScope)
    }

    private fun loadAnimations() {
        animations = biliHomeRepository.getAnimations(homeState.value.animeFilter)
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
            .cachedIn(viewModelScope)
    }

    fun onFolderNameChanged(value: String) {
        mHomeState.update { it.copy(newFolderName = value) }
    }

    fun onPrivateChanged(value: Boolean) {
        mHomeState.update { it.copy(isPrivateFolder = value) }
    }

    fun addNewFolder() {
        viewModelScope.launch {
            val folderName = homeState.value.newFolderName
            val privacy = homeState.value.isPrivateFolder
            if (folderName.isBlank()) {
                EventBus.send(
                    Event.AppEvent.ToastEvent(R.string.str_folder_name_error)
                )
                return@launch
            }
            val success = biliPlaylistRepository.addNewFolder(
                title = folderName,
                privacy = privacy
            )
            if (success) {
                val aid = homeState.value.selectedAid
                aid?.run { getFolderSimpleList(this) }
                onHomeAction(HomeAction.FolderCreatedUIAction(false))
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
}
package com.laohei.bili_tube.features.main.profile

import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.features.main.profile.data.repository.BiliProfileRepository
import com.laohei.bili_tube.features.playlist.data.repository.BiliPlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val biliMineRepository: BiliProfileRepository,
    private val biliPlaylistRepository: BiliPlaylistRepository
) : ViewModel() {

    private val mProfileState = MutableStateFlow(ProfileState())
    val profileState = mProfileState
        .onStart {
            initData()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            mProfileState.value
        )

    private suspend fun initData() = withContext(Dispatchers.IO) {
        launch {
            getUserStat()
        }
        launch {
            getShortHistoryList()
        }
        launch {
            getShortWatchLater()
        }
        launch {
            getFolderList()
        }
    }

    private suspend fun getUserStat() {
        biliMineRepository.getUserStat().run {
            mProfileState.update {
                it.copy(
                    following = this.following,
                    follower = this.follower,
                    dynamicCount = this.dynamicCount
                )
            }
        }
    }

    private suspend fun getShortHistoryList() {
        biliMineRepository.getHistoryList().run {
            mProfileState.update {
                it.copy(
                    historyList = this.list
                )
            }
        }
    }

    private suspend fun getShortWatchLater() {
        biliMineRepository.getWatchLaterList(ps = 3).run {
            mProfileState.update {
                it.copy(
                    watchLaterList = this.list,
                    watchLaterCount = this.count
                )
            }
        }
    }

    private suspend fun getFolderList() {
        biliMineRepository.getFolderList().run {
            val folders = this.fastFilter { item -> item.id == 1 }
                .firstOrNull()?.mediaListResponse?.list
            folders?.apply {
                mProfileState.update {
                    it.copy(folderList = this)
                }
            }
        }
    }

    private fun refresh() {
        mProfileState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            initData()
            delay(500)
            mProfileState.update { it.copy(isRefreshing = false) }
        }
    }

    fun onProfileAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.AddFolderUIAction -> {
                mProfileState.update { it.copy(isShowAddFolder = action.flag) }
            }

            ProfileAction.RefreshAction -> {
                refresh()
            }
        }
    }

    fun showCreatedFolder() {
        mProfileState.update { it.copy(isShowAddFolder = true) }
    }

    fun hideCreatedFolder() {
        mProfileState.update { it.copy(isShowAddFolder = false) }
    }

    fun onFolderNameChanged(value: String) {
        mProfileState.update { it.copy(folderName = value) }
    }

    fun onPrivateChanged(value: Boolean) {
        mProfileState.update { it.copy(isPrivate = value) }
    }

    fun addNewFolder() {
        viewModelScope.launch {
            val folderName = mProfileState.value.folderName
            val privacy = mProfileState.value.isPrivate
            if (folderName.isBlank()) {
                EventBus.send(
                    Event.AppEvent.ToastTextEvent("收藏夹名称不允许为空")
                )
                return@launch
            }
            val success = biliPlaylistRepository.addNewFolder(
                title = folderName,
                privacy = privacy
            )
            if (success) {
                refresh()
                mProfileState.update { it.copy(isShowAddFolder = false) }
                EventBus.send(
                    Event.AppEvent.ToastTextEvent("收藏夹创建成功")
                )
            } else {
                EventBus.send(
                    Event.AppEvent.ToastTextEvent("收藏夹创建失败")
                )
            }
        }
    }

}
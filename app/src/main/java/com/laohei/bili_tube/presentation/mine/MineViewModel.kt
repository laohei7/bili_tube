package com.laohei.bili_tube.presentation.mine

import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.repository.BiliMineRepository
import com.laohei.bili_tube.repository.BiliPlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MineViewModel(
    private val biliMineRepository: BiliMineRepository,
    private val biliPlaylistRepository: BiliPlaylistRepository
) : ViewModel() {

    private val _mMineState = MutableStateFlow(MineState())
    val mineState = _mMineState
        .onStart {
            initData()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _mMineState.value
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
            _mMineState.update {
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
            _mMineState.update {
                it.copy(
                    historyList = this.list
                )
            }
        }
    }

    private suspend fun getShortWatchLater() {
        biliMineRepository.getWatchLaterList(ps = 3).run {
            _mMineState.update {
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
                _mMineState.update {
                    it.copy(folderList = this)
                }
            }
        }
    }

    fun refresh() {
        _mMineState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            initData()
            delay(500)
            _mMineState.update { it.copy(isRefreshing = false) }
        }
    }

    fun showCreatedFolder(){
        _mMineState.update { it.copy(isShowAddFolder = true) }
    }

    fun hideCreatedFolder(){
        _mMineState.update { it.copy(isShowAddFolder = false) }
    }

    fun onFolderNameChanged(value: String) {
        _mMineState.update { it.copy(folderName = value) }
    }

    fun onPrivateChanged(value: Boolean) {
        _mMineState.update { it.copy(isPrivate = value) }
    }

    fun addNewFolder() {
        viewModelScope.launch {
            val folderName = _mMineState.value.folderName
            val privacy = _mMineState.value.isPrivate
            if (folderName.isBlank()) {
                EventBus.send(
                    Event.AppEvent.ToastEvent("收藏夹名称不允许为空")
                )
                return@launch
            }
            val success = biliPlaylistRepository.addNewFolder(
                title = folderName,
                privacy = privacy
            )
            if (success) {
                refresh()
                _mMineState.update { it.copy(isShowAddFolder = false) }
                EventBus.send(
                    Event.AppEvent.ToastEvent("收藏夹创建成功")
                )
            } else {
                EventBus.send(
                    Event.AppEvent.ToastEvent("收藏夹创建失败")
                )
            }
        }
    }

}
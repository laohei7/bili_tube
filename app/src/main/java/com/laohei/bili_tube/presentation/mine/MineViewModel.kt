package com.laohei.bili_tube.presentation.mine

import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laohei.bili_tube.repository.BiliMineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MineViewModel(
    private val biliMineRepository: BiliMineRepository
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
        biliMineRepository.getUserStat()?.run {
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
        biliMineRepository.getHistoryList()?.run {
            _mMineState.update {
                it.copy(
                    historyList = this.list
                )
            }
        }
    }

    private suspend fun getShortWatchLater() {
        biliMineRepository.getWatchLaterList(ps = 3)?.run {
            _mMineState.update {
                it.copy(
                    watchLaterList = this.list,
                    watchLaterCount = this.count
                )
            }
        }
    }

    private suspend fun getFolderList() {
        biliMineRepository.getFolderList()?.run {
            val folders = this.fastFilter { item -> item.id == 1 }
                .firstOrNull()?.mediaListResponse?.list
            folders?.apply {
                _mMineState.update {
                    it.copy(folderList = this)
                }
            }
        }
    }

}
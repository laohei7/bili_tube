package com.laohei.bili_tube.presentation.playlist

import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.history.ToViewModel
import com.laohei.bili_tube.repository.BiliPlaylistRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    private val biliPlaylistRepository: BiliPlaylistRepository
) : ViewModel() {
    private val _mState = MutableStateFlow(PlaylistDetailState())
    val state = _mState.onStart {
        initToViews()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _mState.value
    )

    private fun initToViews() {
        viewModelScope.launch {
            val deferredList = mutableListOf<Deferred<BiliResponse<ToViewModel>>>()
            for (pn in 1..5) {
                val deferred = async {
                    biliPlaylistRepository.getToViewList(pn = pn)
                }
                deferredList.add(deferred)
            }
            val results = deferredList.awaitAll()
            _mState.update {
                it.copy(
                    toViewList = results.fastMap { it.data.list }.flatten()
                )
            }
        }
    }

    fun reorderItem(from: Int, to: Int) {
        _mState.update {
            it.copy(
                toViewList = it.toViewList.toMutableList().apply {
                    add(to, removeAt(from))
                }
            )
        }
    }
}
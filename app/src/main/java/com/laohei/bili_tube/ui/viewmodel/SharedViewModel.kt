package com.laohei.bili_tube.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laohei.bili_tube.AppState
import com.laohei.bili_tube.model.play.MediaPlayConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class DRAWItemParam(
    val initialIndex: Int = 0,
    val face: String,
    val ownerName: String,
    val date: String,
    val desc: String,
    val images: List<String>,
)

class SharedViewModel : ViewModel() {
    private val mAppState = MutableStateFlow(AppState())
    val appState = mAppState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        mAppState.value
    )

    var mPlayParam: MediaPlayConfig = MediaPlayConfig.NONE
        private set

    lateinit var mDRAWItemParam: DRAWItemParam
        private set

    fun setPlayParam(playParam: MediaPlayConfig) {
        mPlayParam = playParam
    }

    fun setDRAWItemParam(drawItemParam: DRAWItemParam) {
        mDRAWItemParam = drawItemParam
    }

    fun updateAppState(other: AppState) {
        mAppState.update { other }
    }
}
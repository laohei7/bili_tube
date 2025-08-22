package com.laohei.bili_tube

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

sealed class PlayParam(
    open val bvid: String = "",
    open val aid: Long = -1L,
    open val cid: Long = -1L,
    open val width: Int = 1920,
    open val height: Int = 1080,
    open val isLocal: Boolean = false,
) {
    data class VideoParam(
        override val bvid: String,
        override val aid: Long = -1L,
        override val cid: Long = -1L,
        override val width: Int = 1920,
        override val height: Int = 1080,
        override val isLocal: Boolean = false,
    ) : PlayParam(bvid, aid, cid, width, height, isLocal)

    data class BangumiParam(
        override val bvid: String,
        override val aid: Long = -1L,
        override val cid: Long = -1L,
        val mediaId: Long? = null,
        val seasonId: Long? = null,
        val epId: Long? = null,
        override val width: Int = 1920,
        override val height: Int = 1080,
        override val isLocal: Boolean = false,
    ) : PlayParam(bvid, aid, cid, width, height, isLocal)

    data class MediaList(
        override val bvid: String,
        override val aid: Long,
        override val cid: Long,
        val mediaKeys: List<Triple<Long, String, Long>>,
        val isToView: Boolean = true,
        val fid: Long? = null,
        val title: String,
        val count: Int
    ) : PlayParam(bvid, aid, cid, 1920, 1080, false)

    object NONE : PlayParam()
}

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

    var mPlayParam: PlayParam = PlayParam.NONE
        private set

    lateinit var mDRAWItemParam: DRAWItemParam
        private set

    fun setPlayParam(playParam: PlayParam) {
        mPlayParam = playParam
    }

    fun setDRAWItemParam(drawItemParam: DRAWItemParam) {
        mDRAWItemParam = drawItemParam
    }

    fun updateAppState(other: AppState) {
        mAppState.update { other }
    }
}
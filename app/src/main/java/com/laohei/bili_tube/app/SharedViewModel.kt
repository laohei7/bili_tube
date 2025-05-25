package com.laohei.bili_tube.app

import androidx.lifecycle.ViewModel

sealed class PlayParam(
    open val bvid: String,
    open val aid: Long,
    open val cid: Long,
    open val width: Int = 1920,
    open val height: Int = 1080,
    open val isLocal: Boolean = false,
) {
    data class Video(
        override val bvid: String,
        override val aid: Long,
        override val cid: Long,
        override val width: Int = 1920,
        override val height: Int = 1080,
        override val isLocal: Boolean = false,
    ) : PlayParam(bvid, aid, cid, width, height, isLocal)

    data class Bangumi(
        override val bvid: String,
        override val aid: Long,
        override val cid: Long,
        val mediaId: Long? = null,
        val seasonId: Long? = null,
        val epId: Long? = null,
        override val width: Int = 1920,
        override val height: Int = 1080,
        override val isLocal: Boolean = false,
    ) : PlayParam(bvid, aid, cid, width, height, isLocal)

    data class MediaList(
        val mediaKeys: List<Triple<Long, String, Long>>,
    ) : PlayParam("", -1, -1, 1920, 1080, false)

    data object NONE : PlayParam("", -1, -1, 1920, 1080, false)
}

class SharedViewModel : ViewModel() {
    var mPlayParam: PlayParam = PlayParam.NONE
        private set

    fun setPlayParam(playParam: PlayParam) {
        mPlayParam = playParam
    }
}
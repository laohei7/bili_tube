package com.laohei.bili_tube.model.play

import com.laohei.bili_tube.model.FolderMedia


sealed class MediaPlayConfig(
    open val bvid: String = "",
    open val aid: Long = -1L,
    open val cid: Long = -1L,
    open val width: Int = 1920,
    open val height: Int = 1080,
    open val isLocal: Boolean = false,
) {
    data class BasicVideoConfig(
        override val bvid: String,
        override val aid: Long = -1L,
        override val cid: Long = -1L,
        override val width: Int = 1920,
        override val height: Int = 1080,
        override val isLocal: Boolean = false,
    ) : MediaPlayConfig(bvid, aid, cid, width, height, isLocal)

    data class BangumiPlayConfig(
        override val bvid: String,
        override val aid: Long = -1L,
        override val cid: Long = -1L,
        val mediaId: Long? = null,
        val seasonId: Long? = null,
        val epId: Long? = null,
        override val width: Int = 1920,
        override val height: Int = 1080,
        override val isLocal: Boolean = false,
    ) : MediaPlayConfig(bvid, aid, cid, width, height, isLocal)

    data class MediaFolderConfig(
        override val bvid: String,
        override val aid: Long,
        override val cid: Long,
        val medias: List<FolderMedia>,
        val isToView: Boolean = true,
        val fid: Long? = null,
        val title: String,
        val count: Int
    ) : MediaPlayConfig(bvid, aid, cid, 1920, 1080, false)

    object NONE : MediaPlayConfig()
}
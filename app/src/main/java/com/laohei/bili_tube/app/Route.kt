package com.laohei.bili_tube.app

import kotlinx.serialization.Serializable

@Serializable
sealed class Route {
    @Serializable
    data object Login : Route()

    @Serializable
    data object HomeGraph : Route() {
        @Serializable
        data object Home : Route()

        @Serializable
        data object Mine : Route()

        @Serializable
        data object Dynamic : Route()

        @Serializable
        data object Subscription : Route()
    }

    @Serializable
    data object Splash : Route()

    @Serializable
    data object History : Route()

    @Serializable
    data object Playlist : Route()

    @Serializable
    data class PlaylistDetail(
        val cover: String,
        val title: String,
        val count: Int,
        val isPrivate: Boolean,
        val isToView: Boolean = true,
        val fid: Long? = null
    ) : Route()

    @Serializable
    data object DownloadManagement : Route()

    @Serializable
    data object Search : Route()

    @Serializable
    data object Settings : Route()

    @Serializable
    data class Play(
        val aid: Long = -1,
        val bvid: String = "",
        val cid: Long = -1,
        val width: Int = 1920,
        val height: Int = 1080,
        val isLocal: Boolean = false,
        val isVideo: Boolean = true,
        val mediaId: Long? = null,
        val seasonId: Long? = null,
        val epId: Long? = null
    ) : Route()
}
package com.laohei.bili_tube.nav

import kotlinx.serialization.Serializable

@Serializable
sealed class AppRoute {
    @Serializable
    data object Splash : AppRoute()

    @Serializable
    data object LoginNav : AppRoute()

    @Serializable
    data object MainNav : AppRoute()

    @Serializable
    data object SettingNav : AppRoute()

    @Serializable
    data object Download : AppRoute()

    @Serializable
    data object Play : AppRoute()

    @Serializable
    data object ImagesBrowser : AppRoute()

    @Serializable
    data object Playlist : AppRoute()

    @Serializable
    data object History : AppRoute()

    @Serializable
    data object Search : AppRoute()

    @Serializable
    data class PlaylistDetail(
        val cover: String,
        val title: String,
        val count: Int,
        val isPrivate: Boolean,
        val isToView: Boolean = true,
        val fid: Long? = null
    ) : AppRoute()
}
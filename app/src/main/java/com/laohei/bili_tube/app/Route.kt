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
    data object DownloadManagement : Route()


    @Serializable
    data class Play(
        val aid: Long,
        val bvid: String,
        val cid: Long,
        val width: Int = 1920,
        val height: Int = 1080
    ) : Route()
}
package com.laohei.bili_tube.features.playlist

sealed interface PlaylistContentAction {
    data class DelToView(
        val aid: Long? = null,
        val viewed: Boolean = false
    ) : PlaylistContentAction

    data object ClearToView : PlaylistContentAction

    data class ShareLink(val flag: Boolean, val link: String = "") : PlaylistContentAction
}
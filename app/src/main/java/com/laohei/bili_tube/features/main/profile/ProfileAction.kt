package com.laohei.bili_tube.features.main.profile

sealed interface ProfileAction {
    data object RefreshAction : ProfileAction

    data class FolderCreatedUIAction(val flag: Boolean) : ProfileAction

    data class HistoryOptionsVisible(val flag: Boolean, val kid: String? = null) : ProfileAction
    data class FolderOptionsVisible(val flag: Boolean, val fid: Long? = null) : ProfileAction
}
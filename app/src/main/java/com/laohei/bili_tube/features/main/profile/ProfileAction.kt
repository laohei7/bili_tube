package com.laohei.bili_tube.features.main.profile

sealed interface ProfileAction {
    data object RefreshAction : ProfileAction

    data class AddFolderUIAction(val flag: Boolean) : ProfileAction
}
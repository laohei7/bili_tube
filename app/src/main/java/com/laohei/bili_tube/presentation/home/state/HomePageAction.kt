package com.laohei.bili_tube.presentation.home.state

sealed interface HomePageAction {
    data class ShowVideoMenuSheetAction(
        val flag: Boolean, val aid: Long? = null,
        val bvid: String? = null
    ) : HomePageAction

    data class ShowFolderSheetAction(val flag: Boolean) : HomePageAction
}
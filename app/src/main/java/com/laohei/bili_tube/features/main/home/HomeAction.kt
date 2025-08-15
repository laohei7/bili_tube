package com.laohei.bili_tube.features.main.home


sealed interface HomeAction {
    data object NoneAction : HomeAction

    data class MenuSheetUIAction(
        val flag: Boolean,
        val aid: Long? = null,
        val bvid: String? = null
    ) : HomeAction

    data class FolderSheetUIAction(
        val flag: Boolean,
        val aid: Long? = null
    ) : HomeAction

    data class AnimeFilterAction(
        val isAnime: Boolean,
        val key: String,
        val value: String
    ) : HomeAction

    data class FolderCreatedUIAction(val flag: Boolean) : HomeAction

    data class AddToViewAction(
        val aid: Long = 0,
        val bvid: String = ""
    ) : HomeAction

    data class AddToFoldersAction(
        val addAids: Set<Long>,
        val delAids: Set<Long>,
        val aid: Long
    ) : HomeAction
}
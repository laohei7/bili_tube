package com.laohei.bili_tube.features.main.subscription

sealed interface SubscriptionAction {
    data object NoneAction : SubscriptionAction
    data class MenuUIAction(val flag: Boolean, val aid: Long? = null, val bvid: String? = null) :
        SubscriptionAction

    data object AddToViewAction : SubscriptionAction

    data class FolderUIAction(val flag: Boolean) : SubscriptionAction

    data class FolderCreatedUIAction(val flag: Boolean) : SubscriptionAction

    data class AddToFoldersAction(
        val addAids: Set<Long>,
        val delAids: Set<Long>,
    ) : SubscriptionAction
}
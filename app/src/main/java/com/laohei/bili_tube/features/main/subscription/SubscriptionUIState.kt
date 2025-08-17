package com.laohei.bili_tube.features.main.subscription

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import com.laohei.bili_sdk.module_v2.folder.SimpleFolderItem

data class SubscriptionUIState(
    val gridState: LazyStaggeredGridState = LazyStaggeredGridState(),

    // ui control
    val showMenuSheet: Boolean = false,
    val showFolderSheet: Boolean = false,
    val showAddFolder: Boolean = false,

    // folder
    val newFolderName: String = "",
    val folders: List<SimpleFolderItem> = emptyList(),
    val isPrivateFolder: Boolean = false,
)

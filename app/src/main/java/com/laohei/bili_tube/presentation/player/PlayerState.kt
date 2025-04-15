package com.laohei.bili_tube.presentation.player

import com.laohei.bili_sdk.module_v2.folder.FolderSimpleItem
import com.laohei.bili_sdk.module_v2.video.ArchiveItem
import com.laohei.bili_sdk.module_v2.video.ArchiveMeta
import com.laohei.bili_sdk.module_v2.video.VideoDetailModel
import com.laohei.bili_sdk.module_v2.video.VideoPageListModel

data class PlayerState(
    val videoDetail: VideoDetailModel? = null,
    val videoArchives: List<ArchiveItem>? = null,
    val videoArchiveMeta: ArchiveMeta? = null,
    val currentArchiveIndex: Int = 0,
    val videoPageList: List<VideoPageListModel>? = null,
    val currentPageListIndex: Int = 0,
    val hasLike: Boolean = false,
    val hasCoin: Boolean = false,
    val hasFavoured: Boolean = false,
    val folders: List<FolderSimpleItem> = emptyList(),
    val isDownloaded: Boolean = false,
    val isVideo:Boolean = true
)
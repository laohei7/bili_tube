package com.laohei.bili_tube.presentation.player

import com.laohei.bili_sdk.module_v2.video.ArchiveItem
import com.laohei.bili_sdk.module_v2.video.ArchiveMeta
import com.laohei.bili_sdk.module_v2.video.VideoDetailModel

data class PlayerState(
    val videoDetail: VideoDetailModel? = null,
    val videoArchives: List<ArchiveItem>? = null,
    val videoArchiveMeta:ArchiveMeta? = null,
    val currentArchiveIndex: Int = 0
)
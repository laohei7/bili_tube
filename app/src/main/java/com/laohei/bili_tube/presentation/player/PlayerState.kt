package com.laohei.bili_tube.presentation.player

import androidx.paging.PagingData
import com.laohei.bili_sdk.module_v2.bangumi.RelatedBangumiItem
import com.laohei.bili_sdk.module_v2.folder.FolderMediaItem
import com.laohei.bili_sdk.module_v2.folder.SimpleFolderItem
import com.laohei.bili_sdk.module_v2.reply.ReplyItem
import com.laohei.bili_sdk.module_v2.user.InfoCardModel
import com.laohei.bili_sdk.module_v2.user.UploadedVideoItem
import com.laohei.bili_sdk.module_v2.video.ArchiveItem
import com.laohei.bili_sdk.module_v2.video.ArchiveMeta
import com.laohei.bili_sdk.module_v2.video.BangumiDetailModel
import com.laohei.bili_sdk.module_v2.video.VideoDetailModel
import com.laohei.bili_sdk.module_v2.video.VideoPageListModel
import com.laohei.bili_sdk.module_v2.video.VideoView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class PlayerState(
    val title: String = "",
    val videoDetail: VideoDetailModel? = null,
    val videoArchives: List<ArchiveItem>? = null,
    val videoArchiveMeta: ArchiveMeta? = null,
    val currentArchiveIndex: Int = 0,
    val videoPageList: List<VideoPageListModel>? = null,
    val currentPageListIndex: Int = 0,
    val hasLike: Boolean = false,
    val hasCoin: Boolean = false,
    val hasFavoured: Boolean = false,
    val folders: List<SimpleFolderItem> = emptyList(),
    val isDownloaded: Boolean = false,
    val isVideo: Boolean = true,
    val bangumiDetail: BangumiDetailModel? = null,
    val currentEpId: Long = -1,
    val initialSeasonIndex: Int = 0,
    val initialEpisodeIndex: Int = 0,
    val replies: Flow<PagingData<ReplyItem>> = flow { PagingData.empty<ReplyItem>() },
    val relatedBangumis: List<RelatedBangumiItem>? = null,
    val autoSkip: Boolean = false,
    val infoCardModel: InfoCardModel? = null,
    val uploadedVideos: Flow<PagingData<UploadedVideoItem>> = flow { PagingData.empty<UploadedVideoItem>() },
    val toViewList: List<VideoView> = emptyList(),
    val playlistIndex: Int = 0,
    val playlistCount: Int = 0,
    val playlistTitle: String = "",
    val nextVideoTitle: String = "",
    val folderResources: Flow<PagingData<FolderMediaItem>> = flow { PagingData.empty<FolderMediaItem>() },
)
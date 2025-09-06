package com.laohei.bili_tube.features.player

import androidx.paging.PagingData
import com.laohei.bili_sdk.model_v2.bangumi.RelatedBangumiItem
import com.laohei.bili_sdk.model_v2.folder.FolderMediaItem
import com.laohei.bili_sdk.model_v2.folder.SimpleFolderItem
import com.laohei.bili_sdk.model_v2.reply.ReplyItem
import com.laohei.bili_sdk.model_v2.user.InfoCardModel
import com.laohei.bili_sdk.model_v2.user.UploadedVideoItem
import com.laohei.bili_sdk.model_v2.video.ArchiveItem
import com.laohei.bili_sdk.model_v2.video.ArchiveMeta
import com.laohei.bili_sdk.model_v2.video.BangumiDetailModel
import com.laohei.bili_sdk.model_v2.video.VideoDetailModel
import com.laohei.bili_sdk.model_v2.video.VideoPageModel
import com.laohei.bili_sdk.model_v2.video.VideoView
import com.laohei.bili_tube.model.play.PlayParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class MediaPlayerUIState(
    val playParam: PlayParam,
    val title: String = "",
    val isVideo: Boolean = true,

    // video properties
    val videoDetail: VideoDetailModel? = null,
    val videoArchives: List<ArchiveItem>? = null,
    val videoArchiveMeta: ArchiveMeta? = null,
    val currentArchiveIndex: Int = 0,

    // divide P adaptation
    val videoPageList: List<VideoPageModel>? = null,
    val currentPageListIndex: Int = 0,

    // user interaction status
    val hasLike: Boolean = false,
    val hasCoin: Boolean = false,
    val hasFavoured: Boolean = false,
    val isPrivate: Boolean = false,
    val isDownloaded: Boolean = false,

    // favorite folder
    val folders: List<SimpleFolderItem> = emptyList(),
    val folderName: String = "",

    // bangumi properties
    val bangumiDetail: BangumiDetailModel? = null,
    val currentEpId: Long = -1,
    val initialSeasonIndex: Int = 0,
    val initialEpisodeIndex: Int = 0,
    val relatedBangumis: List<RelatedBangumiItem>? = null,

    // comment interaction
    val repliesFlow: Flow<PagingData<ReplyItem>> = flow { PagingData.empty<ReplyItem>() },

    // video author related info
    val uploadedVideosFlow: Flow<PagingData<UploadedVideoItem>> = flow { PagingData.empty<UploadedVideoItem>() },
    val infoCardModel: InfoCardModel? = null,

    // watch later list or favorites
    val watchLaterList: List<VideoView> = emptyList(),
    val folderMediaFlow: Flow<PagingData<FolderMediaItem>> = flow { PagingData.empty<FolderMediaItem>() },
    val playlistIndex: Int = 0,
    val playlistCount: Int = 0,
    val playlistTitle: String = "",
    val nextVideoTitle: String = "",

    // other
    val autoSkip: Boolean = false,
)
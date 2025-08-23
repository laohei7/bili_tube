package com.laohei.bili_tube.features.player

import android.util.Log
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.laohei.bili_sdk.apis.UserRelationAction
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.common.BiliResponseNoData
import com.laohei.bili_sdk.module_v2.history.ToViewModel
import com.laohei.bili_sdk.module_v2.video.VideoURLModel
import com.laohei.bili_tube.PlayParam
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.AUTO_SKIP_KEY
import com.laohei.bili_tube.core.EXPORT_SHARED_SOURCE
import com.laohei.bili_tube.core.MERGE_SOURCE_KEY
import com.laohei.bili_tube.core.MOBILE_NET_AUDIO_QUALITY
import com.laohei.bili_tube.core.MOBILE_NET_VIDEO_QUALITY
import com.laohei.bili_tube.core.WLAN_AUDIO_QUALITY
import com.laohei.bili_tube.core.WLAN_VIDEO_QUALITY
import com.laohei.bili_tube.core.action.VideoSettingAction
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.features.player.data.repository.BiliPlayRepository
import com.laohei.bili_tube.features.player.state.media.DefaultMediaController
import com.laohei.bili_tube.features.player.state.media.MediaController
import com.laohei.bili_tube.features.player.state.screen.DefaultScreenController
import com.laohei.bili_tube.features.player.state.screen.ScreenAction
import com.laohei.bili_tube.features.player.state.screen.ScreenController
import com.laohei.bili_tube.features.playlist.data.repository.BiliPlaylistRepository
import com.laohei.bili_tube.utill.NetworkType
import com.laohei.bili_tube.utill.NetworkUtil
import com.laohei.bili_tube.utill.PreferencesUtil
import com.laohei.bili_tube.utill.displayTitle
import com.laohei.bili_tube.utill.download.DownloadManager
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.ceil

@UnstableApi
internal class MediaViewModel(
    private val downloadManager: DownloadManager,
    private val biliPlayRepository: BiliPlayRepository,
    private val biliPlaylistRepository: BiliPlaylistRepository,
    private val preferenceUtil: PreferencesUtil,
    private val networkUtil: NetworkUtil,
    playParam: PlayParam,
    private val defaultMediaManager: DefaultMediaController,
    private val screenManager: DefaultScreenController,
) : ViewModel(), MediaController by defaultMediaManager, ScreenController by screenManager {

    companion object {
        private val TAG = MediaViewModel::class.simpleName
        private const val DBG = true
    }

    private val _mediaPlayerUIState = MutableStateFlow(
        MediaPlayerUIState(
            playParam = playParam,
            autoSkip = preferenceUtil.getValue(AUTO_SKIP_KEY, false)
        )
    )
    val mediaPlayerUIState = _mediaPlayerUIState.onStart {
        setPlayParam(playParam)
        refreshFolderList()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _mediaPlayerUIState.value
    )

    private var mPlaylist: List<Triple<Long, String, Long>> = emptyList()// aid,bvid,cid
    private var mPlaylistIndex = 0

    private var mSelectedAid: Long? = null
    private var mSelectedBvid: String? = null

    init {
        defaultMediaManager.playError = { playErrorCallback() }
        defaultMediaManager.playEnd = { playEndCallback() }
    }

    private fun playErrorCallback() {

    }

    private fun playEndCallback() {
        reportPlaybackProgress(exoPlayer.duration / 1000)
        autoSwitchToNextVideo()
    }

    private fun autoSwitchToNextVideo() {
        val playerUIState = _mediaPlayerUIState.value
        val currentPlayParam = playerUIState.playParam
        var newPlayParam: PlayParam? = null
//        if (mPlaylist.isNotEmpty()) {
//            if (mPlaylistIndex >= mPlaylist.size) {
//                return
//            }
//            val item = mPlaylist[mPlaylistIndex + 1]
//            newPlayParam =
//                (playParam as PlayParam.MediaList).copy(
//                    bvid = item.second,
//                    aid = item.first,
//                    cid = item.third
//                )
//        } else {
//
//        }
        when (currentPlayParam) {
            is PlayParam.VideoParam -> {
                // next playlist
                playerUIState.videoPageList?.let {
                    val next = playerUIState.currentPageListIndex + 1
                    if (next < it.size) {
                        // only cid difference
                        newPlayParam = currentPlayParam.copy(cid = it[next].cid)
                    }
                }
                // next archive
                playerUIState.videoArchives?.let {
                    if (newPlayParam != null) {
                        return@let
                    }
                    val next = playerUIState.currentArchiveIndex + 1
                    if (next < it.size) {
                        val nextVideo = it[next]
                        newPlayParam =
                            currentPlayParam.copy(aid = nextVideo.aid, bvid = nextVideo.bvid)
                    }
                }
            }

            is PlayParam.BangumiParam -> {
                playerUIState.bangumiDetail?.episodes?.let { episodes ->
                    val next =
                        episodes.indexOfFirst { it.epId == playerUIState.currentEpId } + 1
                    if (next < episodes.size) {
                        val nextEpisode = episodes[next]
                        newPlayParam =
                            currentPlayParam.copy(
                                aid = nextEpisode.aid,
                                bvid = nextEpisode.bvid,
                                cid = nextEpisode.cid,
                                epId = nextEpisode.epId
                            )
                    }
                }
            }

            else -> {}
        }
        newPlayParam?.let { setPlayParam(it) }
    }

    fun setPlayParam(newPlayParam: PlayParam) {
        viewModelScope.launch {
            _mediaPlayerUIState.update {
                it.copy(
                    playParam = newPlayParam,
                    isVideo = newPlayParam !is PlayParam.BangumiParam
                )
            }
            withContext(Dispatchers.Main) { setBuffering(true) }
            val playerUIState = _mediaPlayerUIState.value
            val currentPlayParam = playerUIState.playParam
            when (currentPlayParam) {
                is PlayParam.BangumiParam -> loadBangumi(currentPlayParam)

                is PlayParam.VideoParam -> loadVideo(currentPlayParam)

                is PlayParam.MediaList -> {
//                    val mediaList = playParam as PlayParam.MediaList
//                    _mediaPlayerUIState.update {
//                        it.copy(
//                            isVideo = true,
//                            playlistCount = mediaList.count,
//                            playlistTitle = mediaList.title
//                        )
//                    }
//                    mPlaylist = mediaList.mediaKeys
//                    launch { loadPlaylist(mediaList.bvid) }
//                    if ((playParam as PlayParam.MediaList).isToView) {
//                        getToViews()
//                    } else {
//                        val resources =
//                            biliPlaylistRepository.getFolderResourcePager(mediaList.fid!!)
//                        _mediaPlayerUIState.update {
//                            it.copy(folderMediaFlow = resources)
//                        }
//                    }
                }

                PlayParam.NONE -> {}
            }
        }
    }

    private fun getToViews() {
        viewModelScope.launch {
            val deferredList = mutableListOf<Deferred<BiliResponse<ToViewModel>>>()
            for (pn in 1..5) {
                val deferred = async {
                    biliPlaylistRepository.getToViewList(pn = pn)
                }
                deferredList.add(deferred)
            }
            val results = deferredList.awaitAll()
            val idIndexMap = mPlaylist.withIndex().associate { it.value.first to it.index }
            val toViews = results.fastMap { it.data.list }.flatten()
                .sortedWith(compareBy { idIndexMap[it.aid] })
            _mediaPlayerUIState.update {
                it.copy(
                    watchLaterList = toViews,
                    nextVideoTitle = if (mPlaylistIndex + 1 < it.watchLaterList.size - 1) {
                        toViews[mPlaylistIndex + 1].title
                    } else {
                        "已最后一个视频"
                    }
                )
            }
        }
    }

    private suspend fun loadPlaylist(bvid: String) = withContext(Dispatchers.IO) {
        mPlaylistIndex = mPlaylist.indexOfFirst { it.second == bvid }.coerceAtLeast(0)
        _mediaPlayerUIState.update {
            it.copy(
                playlistIndex = mPlaylistIndex,
                nextVideoTitle = when {
                    it.watchLaterList.isNotEmpty() -> {
                        if (mPlaylistIndex + 1 < it.watchLaterList.size - 1) {
                            it.watchLaterList[mPlaylistIndex + 1].title
                        } else {
                            "已最后一个视频"
                        }
                    }

                    else -> ""
                }
            )
        }
        val item = mPlaylist[mPlaylistIndex]
        loadVideo(PlayParam.VideoParam(bvid = item.second, aid = item.first, cid = item.third))
    }

    private suspend fun loadBangumi(bangumiParam: PlayParam.BangumiParam) {
        coroutineScope {
            launch {
                getURL(
                    bvid = "", aid = Long.MIN_VALUE, cid = Long.MIN_VALUE,
                    epId = bangumiParam.epId, isVideo = false
                )
            }
            launch { getBangumiDetail(seasonId = bangumiParam.seasonId, epId = bangumiParam.epId) }
        }
    }

    private suspend fun loadVideo(videoParam: PlayParam.VideoParam) {
        withContext(Dispatchers.IO) {
            launch {
                getURL(
                    bvid = videoParam.bvid,
                    aid = videoParam.aid,
                    cid = videoParam.cid,
                    epId = Long.MIN_VALUE,
                    isVideo = true
                )
            }
            launch {
                getVideoDetail(aid = videoParam.aid, bvid = videoParam.bvid, cid = videoParam.cid)
                getReplies(aid = videoParam.aid)
            }
            launch {
                refreshLikeStatus()
                refreshCoinStatus()
                refreshFavouredStatus()
            }
        }
    }

    private fun getReplies(aid: Long) {
        val replies = biliPlayRepository.getVideoReplyPager(type = 1, oid = aid.toString())
        _mediaPlayerUIState.update {
            it.copy(repliesFlow = replies)
        }
    }

    private fun getUserUploadedVideos(mid: Long) {
        val uploadedVideos = biliPlayRepository.userUploadedVideos(mid)
        _mediaPlayerUIState.update { it.copy(uploadedVideosFlow = uploadedVideos) }
    }

    private suspend fun refreshLikeStatus() {
        val playParam = _mediaPlayerUIState.value.playParam
        val result = biliPlayRepository.hasLike(
            aid = playParam.aid,
            bvid = playParam.bvid
        )

        val isLiked = result.data == 1
        _mediaPlayerUIState.update { it.copy(hasLike = isLiked) }
    }

    private suspend fun refreshCoinStatus() {
        val playParam = _mediaPlayerUIState.value.playParam
        val result = biliPlayRepository.hasCoin(
            aid = playParam.aid,
            bvid = playParam.bvid
        )

        val hasCoin = result.data.multiply != 0
        _mediaPlayerUIState.update { it.copy(hasCoin = hasCoin) }
    }

    private suspend fun refreshFavouredStatus() {
        val playParam = _mediaPlayerUIState.value.playParam
        val result = biliPlayRepository.hasFavoured(aid = playParam.aid)

        val hasFavoured = result.data.favoured
        _mediaPlayerUIState.update { it.copy(hasFavoured = hasFavoured) }
    }

    private suspend fun getURL(
        bvid: String,
        aid: Long,
        cid: Long,
        epId: Long?,
        isVideo: Boolean
    ) {
        if (tryPlayLocal(bvid)) {
            return
        }

        val data = fetchPlayData(bvid, aid, cid, epId, isVideo) ?: return

        updateMediaStateWithQuality(data)

        withContext(Dispatchers.Main) { play(data) }
        saveSharedSourceIfNeeded(data)
    }

    private suspend fun tryPlayLocal(bvid: String): Boolean {
        val task = biliPlayRepository.getVideoPlayURLByLocal(bvid)
        val mergeSource = preferenceUtil.getValue(MERGE_SOURCE_KEY, false)
        return if (mergeSource) {
            task?.mergedFile?.takeIf { File(it).exists() }?.let { localUrl ->
                if (DBG) {
                    Log.d(TAG, "tryPlayLocal: play by local")
                }
                _mediaPlayerUIState.update { it.copy(isDownloaded = true) }
                withContext(Dispatchers.Main) { play(localUrl, null) }
                true
            } ?: false
        } else {
            task?.takeIf { it.videoFile != null && it.audioFile != null }?.let {
                _mediaPlayerUIState.update { state -> state.copy(isDownloaded = true) }
                withContext(Dispatchers.Main) { play(it.videoFile!!, it.audioFile!!) }
                true
            } ?: false
        }
    }

    private suspend fun fetchPlayData(
        bvid: String,
        aid: Long,
        cid: Long,
        epId: Long?,
        isVideo: Boolean
    ) = runCatching {
        when {
            isVideo -> {
                if (cid == -1L) return null
                biliPlayRepository.getVideoPlayURL(aid = aid, bvid = bvid, cid = cid).data
            }

            else -> {
                val response = epId?.let {
                    biliPlayRepository.getMediaPlayURL(aid, bvid, cid, it)
                } ?: return null
                response.takeIf { it.code != 400 }?.result
            }
        }
    }.getOrNull()

    private fun updateMediaStateWithQuality(data: VideoURLModel) {
        // keep the current quality when user select
        if (mediaState.value.isUserSelectedQuality) {
            return
        }
        val qualityList = data.supportFormats.map { it.quality to it.newDescription }
        val (videoDefault, audioDefault) = when (networkUtil.getNetworkType()) {
            NetworkType.NETWORK_TYPE_WIFI ->
                preferenceUtil.getValue(WLAN_VIDEO_QUALITY, Int.MAX_VALUE) to
                        preferenceUtil.getValue(WLAN_AUDIO_QUALITY, 30251)

            NetworkType.NETWORK_TYPE_CELLULAR ->
                preferenceUtil.getValue(MOBILE_NET_VIDEO_QUALITY, 80) to
                        preferenceUtil.getValue(MOBILE_NET_AUDIO_QUALITY, 30280)

            else -> return
        }

        val defaultQuality = qualityList.find { it.first == videoDefault } ?: qualityList.first()
        updateMediaState(
            mediaState.value.copy(
                quality = qualityList,
                videoQuality = defaultQuality,
                audioQuality = audioDefault
            )
        )
    }

    private suspend fun saveSharedSourceIfNeeded(data: VideoURLModel) {
        if (!preferenceUtil.getValue(EXPORT_SHARED_SOURCE, false)) return
        val param = _mediaPlayerUIState.value.playParam
        biliPlayRepository.saveSharedSource(
            bvid = param.bvid,
            aid = param.aid,
            cid = param.cid,
            data = data
        )
    }


    private suspend fun getBangumiDetail(seasonId: Long?, epId: Long?) {
        val playParam = _mediaPlayerUIState.value.playParam
        val response = biliPlayRepository.getBangumiDetail(seasonId = seasonId, epId = epId)
        val result = response.result
        val currentEpisode = epId?.let { result.episodes.find { ep -> ep.epId == it } }
            ?: result.episodes.first()
        val newParam = (playParam as PlayParam.BangumiParam).copy(
            mediaId = result.mediaId,
            epId = currentEpisode.epId,
            aid = currentEpisode.aid,
            bvid = currentEpisode.bvid,
            cid = currentEpisode.cid,
        )
        val skipModel =
            if (preferenceUtil.getValue(AUTO_SKIP_KEY, false)) currentEpisode.skip else null
        defaultMediaManager.setSkipModel(skipModel)

        _mediaPlayerUIState.update {
            it.copy(
                playParam = newParam,
                bangumiDetail = result,
                currentEpId = currentEpisode.epId,
                initialEpisodeIndex = result.episodes.indexOfFirst { ep -> ep.epId == epId }
                    .coerceAtLeast(0),
                initialSeasonIndex = result.seasons.indexOfFirst { se -> se.seasonId == result.seasonId }
                    .coerceAtLeast(0),
                title = currentEpisode.displayTitle()
            )
        }
        coroutineScope {
            launch {
                getURL(
                    aid = currentEpisode.aid,
                    bvid = currentEpisode.bvid,
                    cid = currentEpisode.cid,
                    epId = currentEpisode.epId,
                    isVideo = false
                )
            }
            launch { refreshRelatedBangumis(seasonId = seasonId) }
            launch {
                val replies = biliPlayRepository.getVideoReplyPager(
                    type = 1,
                    oid = currentEpisode.aid.toString()
                )
                _mediaPlayerUIState.update { it.copy(repliesFlow = replies) }
            }
        }
    }

    private suspend fun getVideoDetail(aid: Long, bvid: String, cid: Long) {
        val response = biliPlayRepository.getVideoDetail(aid = aid, bvid = bvid)
        val data = response.data
        val playParam = _mediaPlayerUIState.value.playParam
        _mediaPlayerUIState.update {
            it.copy(videoDetail = data, title = data.view.title)
        }
        coroutineScope {
            if (cid <= 0) {
                val newPlayParam = when (playParam) {
                    is PlayParam.VideoParam -> playParam.copy(cid = data.view.cid)

                    is PlayParam.MediaList -> playParam.copy(cid = data.view.cid)

                    else -> playParam
                }
                launch {
                    getURL(
                        bvid = bvid,
                        aid = aid,
                        cid = data.view.cid,
                        epId = Long.MIN_VALUE,
                        isVideo = true
                    )
                }
            }
            if (playParam !is PlayParam.MediaList) {
                data.view.seasonId?.let { seasonId ->
                    launch { getArchives(mid = data.view.owner.mid, seasonId = seasonId) }
                } ?: _mediaPlayerUIState.update { it.copy(videoArchiveMeta = null) }

                launch { getPageList(bvid = bvid, cid = playParam.cid) }
            }

            launch {
                getUserInfoCard(data.view.owner.mid)
                getUserUploadedVideos(data.view.owner.mid)
            }
        }
    }

    private suspend fun getUserInfoCard(mid: Long) {
        val response = biliPlayRepository.getUserInfoCard(mid)
        _mediaPlayerUIState.update { it.copy(infoCardModel = response.data) }
    }

    private suspend fun getArchives(mid: Long, seasonId: Long) {
        val pageNum = 1
        val pageSize = 30
        val firstPage = biliPlayRepository.getArchives(
            mid = mid,
            seasonId = seasonId,
            pageNum = pageNum,
            pageSize = pageSize
        )
        firstPage.run {
            val total = data.page.total
            val remainingPageCount = ceil((total - pageSize) / pageSize.toFloat()).toInt()

            val extraArchives = coroutineScope {
                (1..remainingPageCount).map { i ->
                    async {
                        biliPlayRepository.getArchives(
                            mid = mid,
                            seasonId = seasonId,
                            pageNum = pageNum + i,
                            pageSize = pageSize
                        ).data.archives
                    }
                }.awaitAll().flatten()
            }
            val allArchives = buildList {
                addAll(data.archives)
                addAll(extraArchives)
            }
            val currentAid = _mediaPlayerUIState.value.videoDetail?.view?.aid

            _mediaPlayerUIState.update {
                it.copy(
                    videoArchiveMeta = data.meta,
                    videoArchives = allArchives,
                    currentArchiveIndex = allArchives.indexOfFirst { item -> item.aid == currentAid }
                )
            }
        }
    }

    private suspend fun getPageList(bvid: String, cid: Long) {
        val response = biliPlayRepository.getPageList(bvid)
        val pages = response.data

        if (pages.isEmpty()) return

        val currentIndex = pages.indexOfFirst { it.cid == cid }.coerceAtLeast(0)

        if (DBG) {
            Log.d(TAG, "getPageList: media serial index $currentIndex")
        }

        _mediaPlayerUIState.update { state ->
            state.copy(
                videoPageList = pages.takeIf { it.size > 1 },
                currentPageListIndex = currentIndex
            )
        }
    }

    fun reportPlaybackProgress(duration: Long) {
        viewModelScope.launch {
            val playParam = _mediaPlayerUIState.value.playParam
            biliPlayRepository.postHistory(
                aid = playParam.aid.toString(),
                cid = playParam.cid.toString(),
                progress = duration
            )
        }
    }

    fun onVideoSettingAction(action: VideoSettingAction) {
        when (action) {
            is VideoSettingAction.AutoSkip -> handleAutoSkip(action)
        }
    }

    private fun handleAutoSkip(action: VideoSettingAction.AutoSkip) {
        _mediaPlayerUIState.update { it.copy(autoSkip = action.flag) }
        preferenceUtil.setValue(AUTO_SKIP_KEY, action.flag)

        val playParam = _mediaPlayerUIState.value.playParam
        if (playParam !is PlayParam.BangumiParam) {
            return
        }
        val bangumiDetail = _mediaPlayerUIState.value.bangumiDetail

        val episode = if (playParam.epId == null) {
            bangumiDetail?.episodes?.firstOrNull()
        } else {
            bangumiDetail?.episodes?.find { it.epId == playParam.epId }
        }

        defaultMediaManager.setSkipModel(if (action.flag) episode?.skip else null)
    }

    fun onVideoMenuAction(action: VideoMenuAction) {
        when (action) {
            is VideoMenuAction.Like -> likeVideo(action.like)


            is VideoMenuAction.AddToFolders -> mSelectedAid?.let {
                modifyVideoFolders(aid = it, action.addAids, action.delAids)
            }


            is VideoMenuAction.ModifyUserRelation -> userRelationModify(action.action)

            is VideoMenuAction.AddCoin -> addVideoCoin(action.coin)

            is VideoMenuAction.SwitchVideoPage -> {
                val playParam = _mediaPlayerUIState.value.playParam
                if (playParam !is PlayParam.VideoParam) {
                    return
                }
                setPlayParam(playParam.copy(cid = action.cid))
            }

            is VideoMenuAction.SwitchEpisode -> {
                val playParam = _mediaPlayerUIState.value.playParam
                if (playParam !is PlayParam.BangumiParam) {
                    return
                }
                setPlayParam(
                    playParam.copy(
                        epId = action.episodeId,
                        aid = action.aid,
                        cid = action.cid,
                        bvid = action.bvid
                    )
                )
            }

            is VideoMenuAction.SwitchSeason -> {
                val playParam = _mediaPlayerUIState.value.playParam
                if (playParam !is PlayParam.BangumiParam) {
                    return
                }
                setPlayParam(
                    playParam.copy(seasonId = action.seasonId, mediaId = null, epId = null)
                )
            }

            is VideoMenuAction.SwitchVideo -> {
                updateMediaState(mediaState.value.reset())
                setPlayParam(action.playParam)
            }

            VideoMenuAction.AddToView -> {
                if (mSelectedAid == null && mSelectedBvid == null) {
                    return
                }
                addToView(mSelectedAid!!, mSelectedBvid!!)
            }

            VideoMenuAction.LoadSimpleFolders -> refreshFolderList()
        }
    }

    private fun addToView(aid: Long, bvid: String) {
        viewModelScope.launch {
            biliPlaylistRepository.addToView(aid, bvid).apply {
                val messageId = when {
                    code == 0 -> R.string.str_add_to_vew_success
                    else -> R.string.str_add_to_view_failed
                }
                EventBus.send(Event.VideoPlayerEvent.SnackbarEventById(messageId = messageId))
                onScreenAction(ScreenAction.SetVideoMenuVisible(false), false)
            }
        }
    }

    private fun userRelationModify(action: UserRelationAction) {
        viewModelScope.launch {
            val owner = _mediaPlayerUIState.value.videoDetail?.view?.owner
            if (owner == null) {
                return@launch
            }
            val response = biliPlayRepository.userRelationModify(
                mid = owner.mid,
                act = action
            )
            when {
                response == BiliResponseNoData.ERROR -> return@launch
                response.code == 0 -> getUserInfoCard(owner.mid)
                else -> EventBus.send(Event.AppEvent.ToastTextEvent(response.message))
            }
        }
    }

    private fun likeVideo(like: Int) {
        viewModelScope.launch {
            val playParam = _mediaPlayerUIState.value.playParam

            val response = biliPlayRepository.videoLike(
                aid = playParam.aid,
                bvid = playParam.bvid,
                like = like
            )

            if (response.code == 0) {
                val hasLike = like == 1
                _mediaPlayerUIState.update { it.copy(hasLike = hasLike) }
                onScreenAction(ScreenAction.SetLikeAnimationVisible(hasLike), false)
            } else {
                EventBus.send(Event.AppEvent.ToastTextEvent(response.message))
            }
        }
    }

    private fun addVideoCoin(multiply: Int) {
        viewModelScope.launch {
            val playParam = _mediaPlayerUIState.value.playParam

            val response = runCatching {
                biliPlayRepository.videoCoin(
                    aid = playParam.aid,
                    bvid = playParam.bvid,
                    multiply = multiply
                )
            }.getOrNull()

            response?.let {
                val messageId = mapCodeToMessage(it.code)

                if (it.code == 0) {
                    _mediaPlayerUIState.update { state -> state.copy(hasCoin = true) }
                }

                EventBus.send(Event.VideoPlayerEvent.SnackbarEventById(messageId))
            }
        }
    }

    private fun modifyVideoFolders(
        aid: Long,
        addMediaIds: Set<Long>,
        delMediaIds: Set<Long>,
    ) {
        viewModelScope.launch {
            val playParam = _mediaPlayerUIState.value.playParam
            val response = runCatching {
                biliPlayRepository.folderDeal(
                    aid = aid,
                    addMediaIds = addMediaIds,
                    delMediaIds = delMediaIds
                )
            }.getOrNull() ?: return@launch

            if (aid == playParam.aid) {
                delay(300)
            } else {
                val messageId = mapFolderResponseToMessage(response.code)
                EventBus.send(Event.VideoPlayerEvent.SnackbarEventById(messageId))
            }

            refreshFolderList()
        }
    }

    private fun refreshFolderList() {
        viewModelScope.launch {
            val playParam = _mediaPlayerUIState.value.playParam
            val aid = mSelectedAid ?: playParam.aid

            val folderResult = biliPlaylistRepository.getFolderSimpleList(aid)
            val folders = folderResult.list
            val hasFavoured = if (aid == playParam.aid) {
                folders.any { it.favState == 1 }
            } else {
                _mediaPlayerUIState.value.hasFavoured
            }

            _mediaPlayerUIState.update { it.copy(folders = folders, hasFavoured = hasFavoured) }
        }
    }

    private suspend fun refreshRelatedBangumis(seasonId: Long?) {
        val id = seasonId ?: return
        val result = biliPlayRepository.getRelatedBangumis(id)

        val relatedSeasons = result.data.season
        _mediaPlayerUIState.update { it.copy(relatedBangumis = relatedSeasons) }
    }

    fun download(quality: Pair<Int, String>) {
        val playerUIState = _mediaPlayerUIState.value
        val playParam = playerUIState.playParam
        val isLoading = playerUIState.run { videoDetail == null && bangumiDetail == null }
        if (isLoading) {
            viewModelScope.launch {
                EventBus.send(
                    Event.VideoPlayerEvent.SnackbarEventById(R.string.str_download_hint)
                )
            }
            return
        }

        val (videoUrls, audioUrls) = defaultMediaManager.getVideoSourceByQuality(quality.first)
        val (name, cover) = getMediaNameAndCover(playParam)
        val archive = getArchiveName(playParam)

        downloadManager.addTask(
            id = playParam.bvid,
            aid = playParam.aid,
            cid = playParam.cid,
            name = name,
            cover = cover ?: "",
            quality = quality.second,
            videoUrls = videoUrls,
            audioUrls = audioUrls,
            archive = archive
        )
    }

    private fun getArchiveName(playParam: PlayParam): String? {
        return if (playParam is PlayParam.VideoParam) null
        else _mediaPlayerUIState.value.bangumiDetail?.seasonTitle
    }

    private fun getMediaNameAndCover(playParam: PlayParam): Pair<String?, String?> {
        return when (playParam) {
            is PlayParam.VideoParam -> {
                val view = _mediaPlayerUIState.value.videoDetail?.view
                Pair(view?.title, view?.pic)
            }

            is PlayParam.BangumiParam -> {
                val episode = _mediaPlayerUIState.value.bangumiDetail?.episodes
                    ?.find { it.epId == playParam.epId }
                Pair(episode?.displayTitle(), episode?.cover)
            }

            else -> Pair("", "")
        }
    }

    fun onFolderNameChange(value: String) {
        _mediaPlayerUIState.update { it.copy(folderName = value) }
    }

    fun onPrivateChanged(value: Boolean) {
        _mediaPlayerUIState.update { it.copy(isPrivate = value) }
    }

    fun createFolder() {
        viewModelScope.launch {
            val folderName = _mediaPlayerUIState.value.folderName.takeIf { it.isNotBlank() }
                ?: run {
                    EventBus.send(Event.AppEvent.ToastEvent(R.string.str_folder_name_error))
                    return@launch
                }

            val privacy = _mediaPlayerUIState.value.isPrivate
            val success = biliPlaylistRepository.addNewFolder(title = folderName, privacy = privacy)

            if (success) {
                refreshFolderList()
                onScreenAction(ScreenAction.SetCreatedFolderVisible(false), true)
                EventBus.send(Event.AppEvent.ToastEvent(R.string.str_folder_created_success))
            } else {
                EventBus.send(Event.AppEvent.ToastEvent(R.string.str_folder_created_failed))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }

    fun setSelectedAid(aid: Long) {
        mSelectedAid = aid
    }

    fun setSelectedBvid(bvid: String) {
        mSelectedBvid = bvid
    }
}

private fun mapCodeToMessage(code: Int): Int = when (code) {
    0 -> R.string.str_add_coin_success
    -101 -> R.string.str_unsigin
    -102 -> R.string.str_account_suspend
    -104 -> R.string.str_not_enough_coins
    34002 -> R.string.str_not_give_yourself_coin
    else -> R.string.str_add_coin_failed
}

private fun mapFolderResponseToMessage(code: Int): Int = when (code) {
    0 -> R.string.str_add_to_folder_success
    else -> R.string.str_add_to_folder_failed
}
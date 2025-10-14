package com.laohei.bili_tube.features.player

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.cachedIn
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.laohei.bili_sdk.apis.UserRelationAction
import com.laohei.bili_sdk.model_v2.common.BiliResponseNoData
import com.laohei.bili_sdk.model_v2.folder.MediaItem
import com.laohei.bili_sdk.model_v2.video.DashModel
import com.laohei.bili_sdk.model_v2.video.EpisodeModel
import com.laohei.bili_sdk.model_v2.video.SkipModel
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.AUTO_SKIP_KEY
import com.laohei.bili_tube.core.AudioQualityMap
import com.laohei.bili_tube.core.VideoQualityMap
import com.laohei.bili_tube.core.action.VideoSettingAction
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.data.local.prefs.PreferencesUtil
import com.laohei.bili_tube.data.repository.BiliPlayRepository
import com.laohei.bili_tube.data.repository.BiliPlaylistRepository
import com.laohei.bili_tube.features.player.state.media_v2.ExoMediaController
import com.laohei.bili_tube.features.player.state.media_v2.MediaController
import com.laohei.bili_tube.features.player.state.media_v2.MediaQuality
import com.laohei.bili_tube.features.player.state.media_v2.MediaSource
import com.laohei.bili_tube.features.player.state.media_v2.SkipSegment
import com.laohei.bili_tube.features.player.state.media_v2.SkipType
import com.laohei.bili_tube.features.player.state.screen_v2.ScreenController
import com.laohei.bili_tube.features.player.state.screen_v2.ScreenControllerImpl
import com.laohei.bili_tube.features.player.state.screen_v2.ScreenEvent
import com.laohei.bili_tube.model.extension.displayTitle
import com.laohei.bili_tube.model.play.MediaPlayConfig
import com.laohei.bili_tube.network.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.ceil

private object MediaDiffCallback : DiffUtil.ItemCallback<MediaItem>() {
    override fun areItemsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
        return oldItem.bvid == newItem.bvid
    }

    override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
        return oldItem == newItem
    }
}

private class NoopListCallback : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}


@UnstableApi
internal class MediaViewModel(
    application: Application,
    playParam: MediaPlayConfig,
    private val biliPlayRepository: BiliPlayRepository,
    private val biliPlaylistRepository: BiliPlaylistRepository,
    private val preferenceUtil: PreferencesUtil,
    networkUtil: NetworkUtil,
    private val screenController: ScreenControllerImpl
) : AndroidViewModel(application), ScreenController by screenController {

    companion object {
        private val TAG = MediaViewModel::class.simpleName
        private const val DBG = true
    }

    val mediaController: MediaController<*> =
        ExoMediaController(getApplication(), networkUtil, preferenceUtil)

    private val _mediaPlayerUIState = MutableStateFlow(
        MediaPlayerUIState(
            mediaPlayConfig = playParam,
            autoSkip = preferenceUtil.getValue(AUTO_SKIP_KEY, false)
        )
    )
    val mediaPlayerUIState = _mediaPlayerUIState.onStart {
        applyMediaPlayConfig(playParam)
        refreshFolderList()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _mediaPlayerUIState.value
    )

    private var mSelectedAid: Long? = null
    private var mSelectedBvid: String? = null
    private var isPlaylistRefreshRequired = true

    private val pagingDataDiffer = AsyncPagingDataDiffer(
        diffCallback = MediaDiffCallback,
        updateCallback = NoopListCallback(),
        mainDispatcher = Dispatchers.Main,
        workerDispatcher = Dispatchers.IO
    )

    private var folderMediaFlowJob: Job? = null

    private val onPlaybackEndListener = object : MediaController.OnPlaybackEndListener {
        override fun onPlaybackEnded() {
            switchToNextVideoAutomatically()
        }
    }

    private val callbackListener = object : ScreenController.CallbackListener {
        override fun onEvent(event: ScreenEvent) {

        }
    }

    init {
        mediaController.setOnPlaybackEndListener(onPlaybackEndListener)
        screenController.setOnCallBackListener(callbackListener)
    }

    private fun switchToNextVideoAutomatically() {
        val playerUIState = _mediaPlayerUIState.value
        val mediaPlayConfig = playerUIState.mediaPlayConfig
        val newMediaPlayConfig: MediaPlayConfig? = when (mediaPlayConfig) {
            is MediaPlayConfig.MediaFolderConfig -> {
                isPlaylistRefreshRequired = false
                getNextPlaylistItemConfig(mediaPlayConfig)
            }

            is MediaPlayConfig.BasicVideoConfig -> {
                getNextVideoArchiveOrPlaylistItemConfig(mediaPlayConfig)
            }

            is MediaPlayConfig.BangumiPlayConfig -> getNextBangumiEpisodeConfig(mediaPlayConfig)

            else -> null
        }
        Log.d(TAG, "switchToNextVideoAutomatically: $newMediaPlayConfig")
        newMediaPlayConfig?.let { applyMediaPlayConfig(it) }
    }

    private fun getNextBangumiEpisodeConfig(mediaPlayConfig: MediaPlayConfig.BangumiPlayConfig): MediaPlayConfig.BangumiPlayConfig? {
        val playerUIState = _mediaPlayerUIState.value
        var newMediaPlayConfig: MediaPlayConfig.BangumiPlayConfig? = null
        playerUIState.bangumiDetail?.episodes?.let { episodes ->
            val next =
                episodes.indexOfFirst { it.epId == playerUIState.currentEpId } + 1
            if (next < episodes.size) {
                val nextEpisode = episodes[next]
                newMediaPlayConfig =
                    mediaPlayConfig.copy(
                        aid = nextEpisode.aid,
                        bvid = nextEpisode.bvid,
                        cid = nextEpisode.cid,
                        epId = nextEpisode.epId
                    )
            }
        }
        return newMediaPlayConfig
    }

    private fun getNextVideoArchiveOrPlaylistItemConfig(mediaPlayConfig: MediaPlayConfig.BasicVideoConfig): MediaPlayConfig.BasicVideoConfig? {
        val playerUIState = _mediaPlayerUIState.value
        var newMediaPlayConfig: MediaPlayConfig.BasicVideoConfig? = null
        // next playlist
        playerUIState.videoPageList?.let {
            val next = playerUIState.currentPageListIndex + 1
            if (next < it.size) {
                // only cid difference
                newMediaPlayConfig = mediaPlayConfig.copy(cid = it[next].cid)
            }
        }
        // next archive
        playerUIState.videoArchives?.let {
            if (newMediaPlayConfig != null) {
                return@let
            }
            val next = playerUIState.currentArchiveIndex + 1
            if (next < it.size) {
                val nextVideo = it[next]
                newMediaPlayConfig =
                    mediaPlayConfig.copy(aid = nextVideo.aid, bvid = nextVideo.bvid)
            }
        }
        return newMediaPlayConfig
    }

    private fun getNextPlaylistItemConfig(mediaPlayConfig: MediaPlayConfig.MediaFolderConfig): MediaPlayConfig.MediaFolderConfig? {
        val playerUIState = _mediaPlayerUIState.value
        var newMediaPlayConfig: MediaPlayConfig.MediaFolderConfig? = null
        // next playlist
        playerUIState.videoPageList?.let {
            val next = playerUIState.currentPageListIndex + 1
            if (next < it.size) {
                // only cid difference
                newMediaPlayConfig = mediaPlayConfig.copy(cid = it[next].cid)
            }
        }
        if (mediaPlayConfig.isToView && newMediaPlayConfig == null) {
            val current =
                mediaPlayConfig.medias.indexOfFirst { it.bvid == mediaPlayConfig.bvid }
            val next = current + 1
            if (current != -1 && next < mediaPlayConfig.medias.size) {
                val nextItem = mediaPlayConfig.medias[next]
                newMediaPlayConfig = mediaPlayConfig.copy(
                    bvid = nextItem.bvid,
                    aid = nextItem.aid,
                    cid = nextItem.cid
                )
            }
        } else if (!mediaPlayConfig.isToView && newMediaPlayConfig == null) {
            val current =
                pagingDataDiffer.snapshot().items.indexOfFirst { it.bvid == mediaPlayConfig.bvid }
            val next = current + 1
            if (current != -1 && next < pagingDataDiffer.snapshot().items.size) {
                val nextItem = pagingDataDiffer.snapshot().items[next]
                newMediaPlayConfig = mediaPlayConfig.copy(
                    bvid = nextItem.bvid,
                    aid = nextItem.id,
                    cid = -1L
                )
            }
        }
        return newMediaPlayConfig
    }

    fun applyMediaPlayConfig(newMediaPlayConfig: MediaPlayConfig) {
        viewModelScope.launch {
            _mediaPlayerUIState.update {
                it.copy(
                    mediaPlayConfig = newMediaPlayConfig,
                    isVideo = newMediaPlayConfig !is MediaPlayConfig.BangumiPlayConfig
                )
            }
            val playerUIState = _mediaPlayerUIState.value
            val mediaPlayConfig = playerUIState.mediaPlayConfig
            when (mediaPlayConfig) {
                is MediaPlayConfig.BangumiPlayConfig -> loadBangumi(mediaPlayConfig)

                is MediaPlayConfig.BasicVideoConfig -> loadVideo(mediaPlayConfig)

                is MediaPlayConfig.MediaFolderConfig -> loadMediaList(mediaPlayConfig)

                MediaPlayConfig.NONE -> {}
            }
        }
    }

    private suspend fun loadMediaList(mediaFolderConfig: MediaPlayConfig.MediaFolderConfig) {
        if (mediaFolderConfig.isToView) {
            loadWatchLaterList(mediaFolderConfig)
        } else {
            loadFolderMediaList(mediaFolderConfig)
        }
    }

    private suspend fun loadWatchLaterList(mediaFolderConfig: MediaPlayConfig.MediaFolderConfig) {
        coroutineScope {
            val videoJob = launch { loadVideo(mediaFolderConfig) }

            if (isPlaylistRefreshRequired) {
                val responses = (1..5).map { pn ->
                    async { biliPlaylistRepository.getWatchLaterList(pn) }
                }.awaitAll()

                val watchLaterItems = responses.flatMap { it.data.list }

                _mediaPlayerUIState.update { it.copy(watchLaterList = watchLaterItems) }
            }

            videoJob.join()
        }
    }

    private suspend fun loadFolderMediaList(mediaFolderConfig: MediaPlayConfig.MediaFolderConfig) {
        coroutineScope {
            val videoJob = launch { loadVideo(mediaFolderConfig) }
            if (isPlaylistRefreshRequired) {
                val folderFlow =
                    biliPlaylistRepository.folderMediaPagingFlow(mlid = mediaFolderConfig.fid!!)
                        .cachedIn(viewModelScope)
                _mediaPlayerUIState.update { it.copy(folderMediaFlow = folderFlow) }
                folderMediaFlowJob = viewModelScope.launch {
                    _mediaPlayerUIState.value.folderMediaFlow.collectLatest { pagingData ->
                        pagingDataDiffer.submitData(pagingData)
                    }
                }
            }
            videoJob.join()
        }
    }

    private suspend fun loadBangumi(bangumiPlayConfig: MediaPlayConfig.BangumiPlayConfig) {
        coroutineScope {
            launch {
                getURL(
                    bvid = "", aid = Long.MIN_VALUE, cid = Long.MIN_VALUE,
                    epId = bangumiPlayConfig.epId, isVideo = false
                )
            }
            launch {
                getBangumiDetail(
                    seasonId = bangumiPlayConfig.seasonId,
                    epId = bangumiPlayConfig.epId
                )
            }
        }
    }

    private suspend fun loadVideo(mediaPlayConfig: MediaPlayConfig) {
        coroutineScope {
            launch {
                getURL(
                    bvid = mediaPlayConfig.bvid, aid = mediaPlayConfig.aid,
                    cid = mediaPlayConfig.cid, epId = null, isVideo = true
                )
            }
            launch {
                getVideoDetail(
                    aid = mediaPlayConfig.aid, bvid = mediaPlayConfig.bvid,
                    cid = mediaPlayConfig.cid
                )
                getReplies(aid = mediaPlayConfig.aid)
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
        _mediaPlayerUIState.update { it.copy(repliesFlow = replies) }
    }

    private fun getUserUploadedVideos(mid: Long) {
        val uploadedVideos = biliPlayRepository.userUploadedVideos(mid)
        _mediaPlayerUIState.update { it.copy(uploadedVideosFlow = uploadedVideos) }
    }

    private suspend fun refreshLikeStatus() {
        val playParam = _mediaPlayerUIState.value.mediaPlayConfig
        val result = biliPlayRepository.hasLike(
            aid = playParam.aid,
            bvid = playParam.bvid
        )

        val isLiked = result.data == 1
        _mediaPlayerUIState.update { it.copy(hasLike = isLiked) }
    }

    private suspend fun refreshCoinStatus() {
        val playParam = _mediaPlayerUIState.value.mediaPlayConfig
        val result = biliPlayRepository.hasCoin(
            aid = playParam.aid,
            bvid = playParam.bvid
        )

        val hasCoin = result.data.multiply != 0
        _mediaPlayerUIState.update { it.copy(hasCoin = hasCoin) }
    }

    private suspend fun refreshFavouredStatus() {
        val playParam = _mediaPlayerUIState.value.mediaPlayConfig
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
        val data = fetchPlayData(bvid, aid, cid, epId, isVideo) ?: return
        val supportQualities = data.supportFormats.map {
            MediaQuality(id = it.quality, label = it.newDescription)
        }
        mediaController.setSupportQualities(supportQualities)
        val sources = data.dash?.mapToMediaSources()
            ?.map { it.copy(startPositionMs = data.lastPlayTime) } ?: return
        mediaController.load(sources)
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

    private suspend fun getBangumiDetail(seasonId: Long?, epId: Long?) {
        val playParam = _mediaPlayerUIState.value.mediaPlayConfig
        val response = biliPlayRepository.getBangumiDetail(seasonId = seasonId, epId = epId)
        val result = response.result
        val currentEpisode = epId?.let { result.episodes.find { ep -> ep.epId == it } }
            ?: result.episodes.first()
        val newParam = (playParam as MediaPlayConfig.BangumiPlayConfig).copy(
            mediaId = result.mediaId,
            epId = currentEpisode.epId,
            aid = currentEpisode.aid,
            bvid = currentEpisode.bvid,
            cid = currentEpisode.cid,
        )

        val enabledAutoSkip = preferenceUtil.getValue(AUTO_SKIP_KEY, false)
        applySkipSegments(enabledAutoSkip, currentEpisode)

        _mediaPlayerUIState.update {
            it.copy(
                mediaPlayConfig = newParam,
                bangumiDetail = result,
                currentEpId = currentEpisode.epId,
                initialEpisodeIndex = result.episodes.indexOfFirst { ep -> ep.epId == epId }
                    .coerceAtLeast(0),
                initialSeasonIndex = result.seasons.indexOfFirst { se -> se.seasonId == result.seasonId }
                    .coerceAtLeast(0),
                title = currentEpisode.displayTitle()
            )
        }
        handleScreenEvent(ScreenEvent.ResetScrollPosition)
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
        val mediaPlayConfig = _mediaPlayerUIState.value.mediaPlayConfig
        _mediaPlayerUIState.update {
            it.copy(videoDetail = data, title = data.view.title)
        }
        handleScreenEvent(ScreenEvent.ResetScrollPosition)
        coroutineScope {
            if (cid <= 0) {
                val newMediaPlayConfig = when (mediaPlayConfig) {
                    is MediaPlayConfig.BasicVideoConfig -> mediaPlayConfig.copy(cid = data.view.cid)

                    is MediaPlayConfig.MediaFolderConfig -> mediaPlayConfig.copy(cid = data.view.cid)

                    else -> mediaPlayConfig
                }
                _mediaPlayerUIState.update { it.copy(mediaPlayConfig = newMediaPlayConfig) }
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
            if (mediaPlayConfig !is MediaPlayConfig.MediaFolderConfig) {
                data.view.seasonId?.let { seasonId ->
                    launch { getArchives(mid = data.view.owner.mid, seasonId = seasonId) }
                } ?: _mediaPlayerUIState.update { it.copy(videoArchiveMeta = null) }
            }

            launch { getPageList(bvid = bvid, cid = mediaPlayConfig.cid) }

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
            val playParam = _mediaPlayerUIState.value.mediaPlayConfig
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

        val mediaPlayConfig = _mediaPlayerUIState.value.mediaPlayConfig
        if (mediaPlayConfig !is MediaPlayConfig.BangumiPlayConfig) {
            return
        }
        val bangumiDetail = _mediaPlayerUIState.value.bangumiDetail

        val episode = if (mediaPlayConfig.epId == null) {
            bangumiDetail?.episodes?.firstOrNull()
        } else {
            bangumiDetail?.episodes?.find { it.epId == mediaPlayConfig.epId }
        }
        applySkipSegments(action.flag, episode)
    }

    private fun applySkipSegments(enabledAutoSkip: Boolean, episode: EpisodeModel?) {
        val skipSegments = if (enabledAutoSkip) {
            episode?.skip?.mapToSkipSegments() ?: emptyMap()
        } else {
            emptyMap()
        }
        when {
            mediaController is ExoMediaController -> {
                mediaController.setSkipSegments(skipSegments)
            }
        }
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
                val playParam = _mediaPlayerUIState.value.mediaPlayConfig
                when (playParam) {
                    is MediaPlayConfig.BasicVideoConfig -> {
                        applyMediaPlayConfig(playParam.copy(cid = action.cid))
                    }

                    is MediaPlayConfig.MediaFolderConfig -> {
                        isPlaylistRefreshRequired = false
                        applyMediaPlayConfig(playParam.copy(cid = action.cid))
                    }

                    is MediaPlayConfig.BangumiPlayConfig,
                    MediaPlayConfig.NONE -> {
                    }
                }

            }

            is VideoMenuAction.SwitchEpisode -> {
                val playParam = _mediaPlayerUIState.value.mediaPlayConfig
                if (playParam !is MediaPlayConfig.BangumiPlayConfig) {
                    return
                }
                applyMediaPlayConfig(
                    playParam.copy(
                        epId = action.episodeId,
                        aid = action.aid,
                        cid = action.cid,
                        bvid = action.bvid
                    )
                )
            }

            is VideoMenuAction.SwitchSeason -> {
                val playParam = _mediaPlayerUIState.value.mediaPlayConfig
                if (playParam !is MediaPlayConfig.BangumiPlayConfig) {
                    return
                }
                applyMediaPlayConfig(
                    playParam.copy(seasonId = action.seasonId, mediaId = null, epId = null)
                )
            }

            is VideoMenuAction.SwitchVideo -> {
//                updateMediaState(mediaState.value.reset())
                applyMediaPlayConfig(action.playParam)
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
//                onScreenAction(ScreenAction.SetVideoMenuVisible(false), false)
                handleScreenEvent(ScreenEvent.VideoMenuVisibility(false))
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
            val playParam = _mediaPlayerUIState.value.mediaPlayConfig

            val response = biliPlayRepository.videoLike(
                aid = playParam.aid,
                bvid = playParam.bvid,
                like = like
            )

            if (response.code == 0) {
                val hasLike = like == 1
                _mediaPlayerUIState.update { it.copy(hasLike = hasLike) }
//                onScreenAction(ScreenAction.SetLikeAnimationVisible(hasLike), false)
                handleScreenEvent(ScreenEvent.LikeAnimationVisibility(hasLike))
            } else {
                EventBus.send(Event.AppEvent.ToastTextEvent(response.message))
            }
        }
    }

    private fun addVideoCoin(multiply: Int) {
        viewModelScope.launch {
            val playParam = _mediaPlayerUIState.value.mediaPlayConfig

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
            val playParam = _mediaPlayerUIState.value.mediaPlayConfig
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
            val playParam = _mediaPlayerUIState.value.mediaPlayConfig
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
                handleScreenEvent(ScreenEvent.FolderCreationVisibility(false))
                EventBus.send(Event.AppEvent.ToastEvent(R.string.str_folder_created_success))
            } else {
                EventBus.send(Event.AppEvent.ToastEvent(R.string.str_folder_created_failed))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaController.release()
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

private fun DashModel.mapToMediaSources(): List<MediaSource> {
    val videos = video
    val audios = buildList {
        addAll(audio)
        dolby?.audio?.let { addAll(it) }
        flac?.audio?.let { add(it) }
    }
    return videos.flatMap { vItem ->
        audios.map { aItem ->
            MediaSource(
                video = vItem.baseUrl,
                audio = aItem.baseUrl,
                videoQuality = MediaQuality(vItem.id, VideoQualityMap[vItem.id]!!),
                audioQuality = MediaQuality(aItem.id, AudioQualityMap[aItem.id]!!)
            )
        }
    }
}

private fun Map<String, SkipModel>.mapToSkipSegments(): Map<SkipType, SkipSegment> {
    return this.entries.associate { (key, value) ->
        val skipType = when (key) {
            "op" -> SkipType.INTRO
            "end" -> SkipType.OUTRO
            else -> SkipType.AD
        }
        skipType to SkipSegment(value.start, value.end)
    }
}
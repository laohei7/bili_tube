package com.laohei.bili_tube.presentation.player

import android.util.Log
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.laohei.bili_sdk.apis.UserRelationAction
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.common.BiliResponseNoData
import com.laohei.bili_sdk.module_v2.history.ToViewModel
import com.laohei.bili_sdk.module_v2.video.ArchiveItem
import com.laohei.bili_tube.app.PlayParam
import com.laohei.bili_tube.component.video.VideoAction
import com.laohei.bili_tube.core.AUTO_SKIP_KEY
import com.laohei.bili_tube.core.MERGE_SOURCE_KEY
import com.laohei.bili_tube.core.MOBILE_NET_AUDIO_QUALITY
import com.laohei.bili_tube.core.MOBILE_NET_VIDEO_QUALITY
import com.laohei.bili_tube.core.WLAN_AUDIO_QUALITY
import com.laohei.bili_tube.core.WLAN_VIDEO_QUALITY
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.core.util.NetworkType
import com.laohei.bili_tube.core.util.NetworkUtil
import com.laohei.bili_tube.core.util.PreferencesUtil
import com.laohei.bili_tube.presentation.player.state.media.DefaultMediaManager
import com.laohei.bili_tube.presentation.player.state.media.MediaManager
import com.laohei.bili_tube.presentation.player.state.screen.DefaultScreenManager
import com.laohei.bili_tube.presentation.player.state.screen.ScreenAction
import com.laohei.bili_tube.presentation.player.state.screen.ScreenManager
import com.laohei.bili_tube.repository.BiliPlayRepository
import com.laohei.bili_tube.repository.BiliPlaylistRepository
import com.laohei.bili_tube.utill.displayTitle
import com.laohei.bili_tube.utill.download.DownloadManager
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
internal class PlayerViewModel(
    private val downloadManager: DownloadManager,
    private val biliPlayRepository: BiliPlayRepository,
    private val biliPlaylistRepository: BiliPlaylistRepository,
    private val preferenceUtil: PreferencesUtil,
    private val networkUtil: NetworkUtil,
    var playParam: PlayParam,
    private val defaultMediaManager: DefaultMediaManager,
    private val screenManager: DefaultScreenManager,
) : ViewModel(), MediaManager by defaultMediaManager, ScreenManager by screenManager {

    companion object {
        private val TAG = PlayerViewModel::class.simpleName
        private const val DBG = true
    }

    private val _mPlayerState = MutableStateFlow(
        PlayerState(
            autoSkip = preferenceUtil.getValue(AUTO_SKIP_KEY, false)
        )
    )
    val playerState = _mPlayerState.onStart {
        updatePlayParam(playParam)
        getFolderSimpleList()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _mPlayerState.value
    )

    private var mPlaylist: List<Triple<Long, String, Long>> = emptyList()// aid,bvid,cid
    private var mPlaylistIndex = 0

    init {
        defaultMediaManager.playErrorCallback = { playErrorCallback() }
        defaultMediaManager.playEndCallback = { playEndCallback() }
    }

    private fun playErrorCallback() {

    }

    private fun playEndCallback() {
        uploadVideoHistory(exoPlayer().duration / 1000)
        autoSwitchVideo()
    }

    private fun autoSwitchVideo() {
        var newPlayPlaParam: PlayParam? = null
        if (mPlaylist.isNotEmpty()) {
            if (mPlaylistIndex >= mPlaylist.size) {
                return
            }
            val item = mPlaylist[mPlaylistIndex]
            newPlayPlaParam =
                PlayParam.Video(bvid = item.second, aid = item.first, cid = item.third)
        } else {
            when (playParam) {
                is PlayParam.Video -> {
                    // next playlist
                    _mPlayerState.value.videoPageList?.let {
                        val next = _mPlayerState.value.currentPageListIndex + 1
                        if (next < it.size) {
                            newPlayPlaParam =
                                (playParam as PlayParam.Video).copy(cid = it[next].cid)
                        }
                    }
                    // next archive
                    _mPlayerState.value.videoArchives?.let {
                        if (newPlayPlaParam != null) {
                            return@let
                        }
                        val next = _mPlayerState.value.currentArchiveIndex + 1
                        if (next < it.size) {
                            val nextVideo = it[next]
                            newPlayPlaParam =
                                (playParam as PlayParam.Video).copy(
                                    aid = nextVideo.aid,
                                    bvid = nextVideo.bvid,
                                    cid = -1L
                                )
                        }
                    }
                }

                is PlayParam.Bangumi -> {
                    _mPlayerState.value.bangumiDetail?.episodes?.run {
                        val next = indexOfFirst { it.epId == _mPlayerState.value.currentEpId } + 1
                        if (next < this.size) {
                            val nextEpisode = this[next]
                            newPlayPlaParam =
                                (playParam as PlayParam.Bangumi).copy(
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
        }
        newPlayPlaParam?.let {
            viewModelScope.launch { updatePlayParam(it) }
        }
    }

    fun updatePlayParam(other: PlayParam) {
        playParam = other
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                toggleLoading()
            }
            when (playParam) {
                is PlayParam.Bangumi -> {
                    _mPlayerState.update { it.copy(isVideo = false) }
                    loadBangumi(playParam as PlayParam.Bangumi)
                }

                is PlayParam.MediaList -> {
                    val mediaList = playParam as PlayParam.MediaList
                    _mPlayerState.update {
                        it.copy(
                            isVideo = true,
                            playlistCount = mediaList.count,
                            playlistTitle = mediaList.title
                        )
                    }
                    mPlaylist = mediaList.mediaKeys
                    launch { loadPlaylist(mediaList.bvid) }
                    if ((playParam as PlayParam.MediaList).isToView) {
                        getToViews()
                    } else {
                        val resources =
                            biliPlaylistRepository.getFolderResourcePager(mediaList.fid!!)
                        _mPlayerState.update {
                            it.copy(folderResources = resources)
                        }
                    }
                }

                is PlayParam.Video -> {
                    _mPlayerState.update { it.copy(isVideo = true) }
                    loadVideo(playParam as PlayParam.Video)
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
            _mPlayerState.update {
                it.copy(
                    toViewList = toViews,
                    nextVideoTitle = if (mPlaylistIndex + 1 < it.toViewList.size - 1) {
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
        _mPlayerState.update {
            it.copy(
                playlistIndex = mPlaylistIndex,
                nextVideoTitle = when {
                    it.toViewList.isNotEmpty() -> {
                        if (mPlaylistIndex + 1 < it.toViewList.size - 1) {
                            it.toViewList[mPlaylistIndex + 1].title
                        } else {
                            "已最后一个视频"
                        }
                    }

                    else -> ""
                }
            )
        }
        val item = mPlaylist[mPlaylistIndex]
        loadVideo(PlayParam.Video(bvid = item.second, aid = item.first, cid = item.third))
    }

    private suspend fun loadBangumi(bangumiParam: PlayParam.Bangumi) {
        withContext(Dispatchers.IO) {
            launch {
                getURL(
                    bvid = "",
                    aid = Long.MIN_VALUE,
                    cid = Long.MIN_VALUE,
                    epId = bangumiParam.epId,
                    isVideo = false
                )
            }
            launch { getBangumiDetial(seasonId = bangumiParam.seasonId, epId = bangumiParam.epId) }
        }
    }

    private suspend fun loadVideo(videoParam: PlayParam.Video) {
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
                hasLike()
                hasCoin()
                hasFavoured()
            }
        }
    }

    private fun getReplies(aid: Long) {
        val replies = biliPlayRepository.getVideoReplyPager(type = 1, oid = aid.toString())
        _mPlayerState.update {
            it.copy(replies = replies)
        }
    }

    private fun getUserUploadedVideos(mid: Long) {
        val uploadedVideos = biliPlayRepository.userUploadedVideos(mid)
        _mPlayerState.update { it.copy(uploadedVideos = uploadedVideos) }
    }

    private suspend fun hasLike() {
        biliPlayRepository.hasLike(
            aid = playParam.aid,
            bvid = playParam.bvid
        ).apply {
            _mPlayerState.update { it.copy(hasLike = data == 1) }
        }
    }

    private suspend fun hasCoin() {
        biliPlayRepository.hasCoin(
            aid = playParam.aid,
            bvid = playParam.bvid
        ).apply {
            _mPlayerState.update { it.copy(hasCoin = data.multiply != 0) }
        }
    }

    private suspend fun hasFavoured() {
        biliPlayRepository.hasFavoured(
            aid = playParam.aid,
        ).apply {
            _mPlayerState.update { it.copy(hasCoin = data.favoured) }
        }
    }

    private suspend fun getURL(bvid: String, aid: Long, cid: Long, epId: Long?, isVideo: Boolean) {
        val task = biliPlayRepository.getVideoPlayURLByLocal(bvid)
        val mergeSource = preferenceUtil.getValue(MERGE_SOURCE_KEY, false)
        if (mergeSource) {
            task?.mergedFile?.let { localUrl ->
                if (File(localUrl).exists()) {
                    if (DBG) {
                        Log.d(TAG, "getVideoURL: play by local")
                    }
                    _mPlayerState.update { it.copy(isDownloaded = true) }
                    withContext(Dispatchers.Main) {
                        play(video = localUrl, null)
                    }
                    return
                }
            }
        } else {
            task?.run {
                if (videoFile != null && audioFile != null) {
                    _mPlayerState.update { it.copy(isDownloaded = true) }
                    withContext(Dispatchers.Main) {
                        play(video = videoFile, audioFile)
                    }
                    return
                }
            }
        }

        val data = when {
            isVideo -> {
                if (cid == -1L) return
                val response = biliPlayRepository.getVideoPlayURL(
                    aid = aid,
                    bvid = bvid,
                    cid = cid,
                )
                response.data
            }

            else -> {
                if (epId == null) return
                val response = biliPlayRepository.getMediaPlayURL(
                    epId = epId
                )
                response.result
            }
        }
        data.run {
            val quality = data.supportFormats.map { Pair(it.quality, it.newDescription) }
            val userDefaultQuality = when (networkUtil.getNetworkType()) {
                NetworkType.NETWORK_TYPE_WIFI -> {
                    Pair(
                        preferenceUtil.getValue(WLAN_VIDEO_QUALITY, Int.MAX_VALUE),
                        preferenceUtil.getValue(WLAN_AUDIO_QUALITY, 30251)
                    )
                }

                NetworkType.NETWORK_TYPE_CELLULAR -> {
                    Pair(
                        preferenceUtil.getValue(MOBILE_NET_VIDEO_QUALITY, 80),
                        preferenceUtil.getValue(MOBILE_NET_AUDIO_QUALITY, 30280)
                    )
                }

                else -> return@run
            }
            val defaultQuality =
                quality.find { it.first == userDefaultQuality.first } ?: quality.first()
            updateMediaState(
                state.value.copy(
                    quality = quality,
                    videoQuality = defaultQuality,
                    audioQuality = userDefaultQuality.second
                )
            )
            withContext(Dispatchers.Main) {
                play(data)
            }
        }
    }

    private fun getBangumiDetial(seasonId: Long?, epId: Long?) {
        viewModelScope.launch {
            val response = biliPlayRepository.getBangumiDetail(seasonId = seasonId, epId = epId)
            response.run {
                _mPlayerState.update {
                    it.copy(
                        bangumiDetail = result,
                        currentEpId = when {
                            epId != null -> epId
                            else -> result.episodes.first().epId
                        },
                        initialEpisodeIndex = result.episodes.indexOfFirst { ep -> ep.epId == epId }
                            .coerceAtLeast(0),
                        initialSeasonIndex = result.seasons.indexOfFirst { se -> se.seasonId == result.seasonId }
                            .coerceAtLeast(0)
                    )
                }
                val episode = when {
                    epId == null -> result.episodes.first()
                    else -> result.episodes.find { it.epId == epId }
                }
                episode?.let {
                    _mPlayerState.update { state ->
                        state.copy(
                            title = it.displayTitle()
                        )
                    }
                    playParam = (playParam as PlayParam.Bangumi).copy(
                        mediaId = result.mediaId,
                        epId = it.epId,
                        aid = it.aid,
                        bvid = it.bvid,
                        cid = it.cid,
                    )
                    val skipModel = when {
                        preferenceUtil.getValue(AUTO_SKIP_KEY, false) -> it.skip
                        else -> null
                    }
                    defaultMediaManager.setSkipModel(skipModel)
                    launch {
                        getURL(
                            aid = it.aid,
                            bvid = it.bvid,
                            cid = it.cid,
                            epId = it.epId,
                            isVideo = false
                        )
                    }
                    launch { getRelatedBangumis(seasonId = seasonId) }
                    launch {
                        val replies = biliPlayRepository.getVideoReplyPager(
                            type = 1,
                            oid = it.aid.toString()
                        )
                        _mPlayerState.update {
                            it.copy(replies = replies)
                        }
                    }
                }
            }
        }
    }

    private suspend fun getVideoDetail(aid: Long, bvid: String, cid: Long) {
        val response = biliPlayRepository.getVideoDetail(aid = aid, bvid = bvid)
        val data = response.data
        viewModelScope.launch {
            _mPlayerState.update {
                it.copy(videoDetail = data, title = data.view.title)
            }
            if (cid == -1L) {
                playParam = when (playParam) {
                    is PlayParam.Video -> {
                        (playParam as PlayParam.Video).copy(cid = data.view.cid)
                    }

                    is PlayParam.MediaList -> {
                        (playParam as PlayParam.MediaList).copy(cid = data.view.cid)
                    }

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
            launch {
                data.view.seasonId?.let {
                    getArchives(mid = data.view.owner.mid, seasonId = it)
                } ?: run {
                    _mPlayerState.update { it.copy(videoArchiveMeta = null) }
                }
            }
            launch { getPageList(bvid = bvid, cid = playParam.cid) }
            launch {
                getUserInfoCard(data.view.owner.mid)
                getUserUploadedVideos(data.view.owner.mid)
            }
        }
    }

    private suspend fun getUserInfoCard(mid: Long) {
        biliPlayRepository.getUserInfoCard(mid)?.run {
            _mPlayerState.update { it.copy(infoCardModel = this.data) }
        }
    }

    private suspend fun getArchives(
        mid: Long,
        seasonId: Long,
    ) {
        val pageNum = 1
        val pageSize = 30
        val firstPage = biliPlayRepository.getArchives(
            mid = mid,
            seasonId = seasonId,
            pageNum = pageNum,
            pageSize = pageSize
        )
        firstPage.run {
            val count = ceil((this.data.page.total - pageSize) / pageSize.toFloat()).toInt()
            val leftovers = mutableListOf<ArchiveItem>()
            for (i in 0 until count) {
                biliPlayRepository.getArchives(
                    mid = mid,
                    seasonId = seasonId,
                    pageNum = pageNum + i + 1,
                    pageSize = pageSize
                ).let {
                    leftovers += it.data.archives
                }
            }

            val allArchives =
                this.data.archives + leftovers
            Log.d(TAG, "getArchives: ${this.data.page.total} ${allArchives.size}")
            _mPlayerState.update {
                it.copy(
                    videoArchiveMeta = this.data.meta,
                    videoArchives = allArchives,
                    currentArchiveIndex = allArchives.indexOfFirst { item ->
                        item.aid == it.videoDetail?.view?.aid
                    }
                )
            }
        }
    }

    private suspend fun getPageList(bvid: String, cid: Long) {
        val pageList = biliPlayRepository.getPageList(bvid)
        pageList.let { data ->
            val index = data.data.indexOfFirst { item -> item.cid == cid }.coerceAtLeast(0)
            if (DBG) {
                Log.d(TAG, "getPageList: media serial index $index")
            }
            _mPlayerState.update {
                it.copy(
                    videoPageList = if (data.data.size <= 1) null else data.data,
                    currentPageListIndex = index
                )
            }
        }
    }

    fun uploadVideoHistory(duration: Long) {
        viewModelScope.launch {
            biliPlayRepository.postHistory(
                aid = playParam.aid.toString(),
                cid = playParam.cid.toString(),
                progress = duration
            )
        }
    }

    fun handleVideoSettingAction(action: VideoAction.VideoSettingAction) {
        when (action) {
            is VideoAction.VideoSettingAction.AutoSkipAction -> {
                _mPlayerState.update { it.copy(autoSkip = action.flag) }
                val bangumiDetailModel = _mPlayerState.value.bangumiDetail
                val episode = when {
                    playParam is PlayParam.Bangumi &&
                            (playParam as PlayParam.Bangumi).epId == null -> bangumiDetailModel?.episodes?.first()

                    else -> bangumiDetailModel?.episodes?.find { it.epId == (playParam as PlayParam.Bangumi).epId }
                }
                val skipModel = when {
                    action.flag -> episode?.skip
                    else -> null
                }
                defaultMediaManager.setSkipModel(skipModel)
                preferenceUtil.setValue(AUTO_SKIP_KEY, action.flag)
            }
        }
    }

    fun handleVideoMenuAction(action: VideoAction.VideoMenuAction) {
        when (action) {
            is VideoAction.VideoMenuAction.VideoLikeAction -> {
                videoLike(action.like)
            }

            is VideoAction.VideoMenuAction.CoinAction -> {
                videoCoin(action.coin)
            }

            is VideoAction.VideoMenuAction.CollectAction -> {
                videoFolderDeal(action.addAids, action.delAids)
            }

            is VideoAction.VideoMenuAction.VideoDislikeAction -> {

            }

            is VideoAction.VideoMenuAction.UserRelationModifyAction -> {
                userRelationModify(action.action)
            }

            else -> {}
        }
    }

    fun handleVideoPlayAction(action: VideoAction.VideoPlayAction) {
        when (action) {
            is VideoAction.VideoPlayAction.SwitchMediaSeriesAction -> {
                updatePlayParam(
                    PlayParam.Video(
                        aid = playParam.aid,
                        bvid = playParam.bvid,
                        cid = action.cid
                    )
                )
            }

            is VideoAction.VideoPlayAction.SwitchEpisodeAction -> {
                updatePlayParam(
                    (playParam as PlayParam.Bangumi).copy(
                        epId = action.episodeId,
                        aid = action.aid,
                        cid = action.cid,
                        bvid = action.bvid
                    )
                )
            }

            is VideoAction.VideoPlayAction.SwitchSeasonAction -> {
                updatePlayParam(
                    (playParam as PlayParam.Bangumi).copy(
                        seasonId = action.seasonId,
                        mediaId = null,
                        epId = null,
                    )
                )
            }

            is VideoAction.VideoPlayAction.SwitchVideoAction -> {
                updatePlayParam(action.playParam)
            }

            is VideoAction.VideoPlayAction.SwitchPlaylistAction -> {
                viewModelScope.launch { loadPlaylist(action.bvid) }
            }
        }
    }

    private fun userRelationModify(act: UserRelationAction) {
        viewModelScope.launch {
            val owner = _mPlayerState.value.videoDetail!!.view.owner
            biliPlayRepository.userRelationModify(
                mid = owner.mid,
                act = act
            ).let {
                if (it == BiliResponseNoData.ERROR) {
                    return@launch
                }
                if (it.code == 0) {
                    getUserInfoCard(owner.mid)
                    return@let
                }
                EventBus.send(Event.AppEvent.ToastEvent(it.message))
            }
        }
    }

    private fun videoLike(like: Int) {
        viewModelScope.launch {
            biliPlayRepository.videoLike(
                aid = playParam.aid,
                bvid = playParam.bvid,
                like = like
            ).run {
                val hasLike = like == 1
                _mPlayerState.update { it.copy(hasLike = hasLike) }
                handleScreenAction(
                    ScreenAction.ShowLikeAnimationAction(hasLike),
                    false
                )
            }
        }
    }

    private fun videoCoin(multiply: Int) {
        viewModelScope.launch {
            EventBus.send(Event.PlayerEvent.SnackbarEvent(message = "投币: $multiply"))
//            biliPlayRepository.videoCoin(
//                aid = params.aid,
//                bvid = params.bvid,
//                multiply = multiply
//            )?.run {
//                val msg = when (code) {
//                    0 -> {
//                        _mPlayerState.update { it.copy(hasLike = true) }
//                        "投币成功"
//                    }
//
//                    -101 -> "账号未登录"
//                    -102 -> "账号被封停"
//                    -104 -> "硬币不足"
//                    34002 -> "不能给自己投币"
//                    else -> "投币失败"
//                }
//                EventBus.send(Event.PlayerEvent.SnackbarEvent(message = msg))
//            }
        }
    }

    private fun videoFolderDeal(
        addMediaIds: Set<Long>,
        delMediaIds: Set<Long>,
    ) {
        viewModelScope.launch {
            biliPlayRepository.folderDeal(
                aid = playParam.aid,
                addMediaIds = addMediaIds,
                delMediaIds = delMediaIds
            )?.run {
                Log.d(TAG, "videoFolderDeal: $this")
                delay(300)
                getFolderSimpleList()
            }
        }
    }

    private suspend fun getFolderSimpleList() {
        biliPlaylistRepository.getFolderSimpleList(playParam.aid).apply {
            Log.d(TAG, "getFolderSimpleList: $this")
            _mPlayerState.update {
                it.copy(
                    folders = this.list,
                    hasFavoured = this.list.any { item -> item.favState == 1 })
            }
        }
    }

    private suspend fun getRelatedBangumis(seasonId: Long?) {
        if (seasonId == null) {
            return
        }
        biliPlayRepository.getRelatedBangumis(seasonId).run {
            _mPlayerState.update { it.copy(relatedBangumis = this.data.season) }
        }
    }

    fun download(quality: Pair<Int, String>) {
        val isLoading = _mPlayerState.value.run {
            videoDetail == null && bangumiDetail == null
        }
        if (isLoading) {
            viewModelScope.launch {
                EventBus.send(Event.PlayerEvent.SnackbarEvent(message = "可下载资源正在加载中，请稍后..."))
            }
            return
        }
        val urls = defaultMediaManager.getVideoSourceByQuality(quality.first)
        val (name, cover) = when (playParam) {
            is PlayParam.Video -> {
                val view = _mPlayerState.value.videoDetail?.view
                Pair(view?.title, view?.pic)
            }

            is PlayParam.Bangumi -> {
                val episode =
                    _mPlayerState.value.bangumiDetail?.episodes
                        ?.find { it.epId == (playParam as PlayParam.Bangumi).epId }
                val title = episode?.displayTitle()
                Pair(title, episode?.cover)
            }

            else -> {
                Pair("", "")
            }
        }
        downloadManager.addTask(
            id = playParam.bvid,
            aid = playParam.aid,
            cid = playParam.cid,
            name = name,
            cover = cover ?: "",
            quality = quality.second,
            videoUrls = urls.first,
            audioUrls = urls.second,
            archive = when (playParam) {
                is PlayParam.Video -> null
                else -> _mPlayerState.value.bangumiDetail?.seasonTitle
            }
        )
    }

    fun onFolderNameChanged(value: String) {
        _mPlayerState.update { it.copy(folderName = value) }
    }

    fun onPrivateChanged(value: Boolean) {
        _mPlayerState.update { it.copy(isPrivate = value) }
    }

    fun addNewFolder() {
        viewModelScope.launch {
            val folderName = _mPlayerState.value.folderName
            val privacy = _mPlayerState.value.isPrivate
            if (folderName.isBlank()) {
                EventBus.send(
                    Event.AppEvent.ToastEvent("收藏夹名称不允许为空")
                )
                return@launch
            }
            val success = biliPlaylistRepository.addNewFolder(
                title = folderName,
                privacy = privacy
            )
            if (success) {
                getFolderSimpleList()
                handleScreenAction(ScreenAction.CreatedFolderAction(false), true)
                EventBus.send(
                    Event.AppEvent.ToastEvent("收藏夹创建成功")
                )
            } else {
                EventBus.send(
                    Event.AppEvent.ToastEvent("收藏夹创建失败")
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }
}
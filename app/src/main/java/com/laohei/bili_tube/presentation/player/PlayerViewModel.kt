package com.laohei.bili_tube.presentation.player

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.laohei.bili_sdk.module_v2.video.ArchiveItem
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.component.video.VideoAction
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.presentation.player.state.media.DefaultMediaManager
import com.laohei.bili_tube.presentation.player.state.media.MediaManager
import com.laohei.bili_tube.presentation.player.state.media.Quality
import com.laohei.bili_tube.presentation.player.state.screen.DefaultScreenManager
import com.laohei.bili_tube.presentation.player.state.screen.ScreenAction
import com.laohei.bili_tube.presentation.player.state.screen.ScreenManager
import com.laohei.bili_tube.repository.BiliPlayRepository
import com.laohei.bili_tube.repository.BiliPlaylistRepository
import com.laohei.bili_tube.utill.download.DownloadManager
import kotlinx.coroutines.Dispatchers
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
    private val playlistRepository: BiliPlaylistRepository,
    private var params: Route.Play,
    private val defaultMediaManager: DefaultMediaManager,
    private val screenManager: DefaultScreenManager,
) : ViewModel(), MediaManager by defaultMediaManager, ScreenManager by screenManager {

    companion object {
        private val TAG = PlayerViewModel::class.simpleName
        private const val DBG = true
    }

    // 视频评论
    var videoReplies = biliPlayRepository.getVideoReplyPager(
        type = 1,
        oid = params.aid.toString()
    )
        private set

    private val _mPlayerState = MutableStateFlow(PlayerState())
    val playerState = _mPlayerState.onStart {
        updateParams(params)
        getFolderSimpleList()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _mPlayerState.value
    )

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
        var newParams: Route.Play? = null
        // next playlist
        _mPlayerState.value.videoPageList?.let {
            val next = _mPlayerState.value.currentPageListIndex + 1
            if (next < it.size) {
                newParams = params.copy(cid = it[next].cid)
            }
        }
        // next archive
        _mPlayerState.value.videoArchives?.let {
            if (newParams != null) {
                return@let
            }
            val next = _mPlayerState.value.currentArchiveIndex + 1
            if (next < it.size) {
                val nextVideo = it[next]
                newParams = params.copy(aid = nextVideo.aid, bvid = nextVideo.bvid, cid = -1L)
            }
        }

        // related
//        _mPlayerState.value.videoDetail?.let {
//            if (newParams != null) {
//                return@let
//            }
//            if (it.related.isNotEmpty()) {
//                val nextVideo = it.related.first()
//                newParams =
//                    params.copy(aid = nextVideo.aid, bvid = nextVideo.bvid, cid = nextVideo.cid)
//            }
//        }
        newParams?.let {
            viewModelScope.launch { updateParams(it) }
        }
    }


    suspend fun updateParams(other: Route.Play) {
        params = other
        _mPlayerState.update { it.copy(isVideo = params.isVideo) }
        if (DBG) {
            Log.d(TAG, "updateParams: start load video, params: $params")
        }
        withContext(Dispatchers.Main) {
            toggleLoading()
        }
        when {
            params.isVideo -> loadVideo()
            else -> loadBangumi()
        }
    }

    private suspend fun loadBangumi() {
        withContext(Dispatchers.IO) {
            launch { getBangumiURL() }
            launch { getBangumiDetial() }
        }
    }

    private suspend fun loadVideo() {
        withContext(Dispatchers.IO) {
            launch { getVideoURL() }
            launch {
                getVideoDetail()
                val replies = biliPlayRepository.getVideoReplyPager(
                    type = 1,
                    oid = params.aid.toString()
                )
                _mPlayerState.update {
                    it.copy(replies = replies)
                }
            }
            launch {
                hasLike()
                hasCoin()
                hasFavoured()
            }
        }
    }

    private suspend fun hasLike() {
        biliPlayRepository.hasLike(
            aid = params.aid,
            bvid = params.bvid
        )?.apply {
            _mPlayerState.update { it.copy(hasLike = data == 1) }
        }
    }

    private suspend fun hasCoin() {
        biliPlayRepository.hasCoin(
            aid = params.aid,
            bvid = params.bvid
        )?.apply {
            _mPlayerState.update { it.copy(hasCoin = data.multiply != 0) }
        }
    }

    private suspend fun hasFavoured() {
        biliPlayRepository.hasFavoured(
            aid = params.aid,
        )?.apply {
            _mPlayerState.update { it.copy(hasCoin = data.favoured) }
        }
    }

    private suspend fun getVideoURL() {
        val task = biliPlayRepository.getVideoPlayURLByLocal(params.bvid)
        task?.mergedFile?.let { localUrl ->
            if (File(localUrl).exists()) {
                if (DBG) {
                    Log.d(TAG, "getVideoURL: play by local")
                }
                _mPlayerState.update { it.copy(isDownloaded = true) }
                withContext(Dispatchers.Main) {
                    play(localUrl)
                }
                return
            }
        }
        if (params.cid == -1L) {
            return
        }
        if (DBG) {
            Log.d(TAG, "getVideoURL: play by network")
        }
        val response = biliPlayRepository.getVideoPlayURL(
            aid = params.aid,
            bvid = params.bvid,
            cid = params.cid,
        )
        response?.run {
            val quality = data.supportFormats.map { Pair(it.quality, it.newDescription) }
            val defaultQuality = quality.find { it.first == data.quality } ?: Quality.first()
            updateMediaState(state.value.copy(quality = quality, defaultQuality = defaultQuality))
            withContext(Dispatchers.Main) {
                play(data)
            }
            return
        }
        updateMediaState(state.value.copy(isError = true))
    }

    private suspend fun getBangumiURL() {
        when {
            params.isVideo || params.epId == null -> {
                return
            }
        }
        if (DBG) {
            Log.d(TAG, "getVideoURL: play by network")
        }
        val response = biliPlayRepository.getMediaPlayURL(
            epId = params.epId
        )
        response?.run {
            val quality = result.supportFormats.map { Pair(it.quality, it.newDescription) }
            val defaultQuality = quality.find { it.first == result.quality } ?: Quality.first()
            updateMediaState(state.value.copy(quality = quality, defaultQuality = defaultQuality))
            withContext(Dispatchers.Main) {
                play(result)
            }
            return
        }
        updateMediaState(state.value.copy(isError = true))
    }

    private suspend fun getBangumiDetial() {
        val response = biliPlayRepository.getBangumiDetail(
            seasonId = params.seasonId,
            epId = params.epId
        )
        response?.run {
            _mPlayerState.update {
                it.copy(
                    bangumiDetail = result,
                    currentEpId = when {
                        params.epId != null -> params.epId!!
                        else -> result.episodes.first().epId
                    },
                    initialEpisodeIndex = result.episodes.indexOfFirst { ep -> ep.epId == params.epId }
                        .coerceAtLeast(0),
                    initialSeasonIndex = result.seasons.indexOfFirst { se -> se.seasonId == result.seasonId }
                        .coerceAtLeast(0)
                )
            }
            val episode = when {
                params.epId == null -> result.episodes.first()
                else -> result.episodes.find { it.epId == params.epId }
            }
            episode?.let {
                params = params.copy(
                    mediaId = result.mediaId,
                    epId = it.epId,
                    aid = it.aid,
                    bvid = it.bvid,
                    cid = it.cid
                )
            }
            viewModelScope.launch { getBangumiURL() }
            viewModelScope.launch {
                val replies = biliPlayRepository.getVideoReplyPager(
                    type = 1,
                    oid = params.aid.toString()
                )
                _mPlayerState.update {
                    it.copy(replies = replies)
                }
            }
        }
    }

    private suspend fun getVideoDetail() {
        val response = biliPlayRepository.getVideoDetail(
            aid = params.aid,
            bvid = params.bvid,
        )
        response?.run {
            _mPlayerState.update {
                it.copy(videoDetail = data)
            }
            if (params.cid == -1L) {
                params = params.copy(
                    cid = data.view.cid
                )
                viewModelScope.launch { getVideoURL() }
            }
            viewModelScope.launch {
                this@run.data.view.seasonId?.let {
                    getArchives(mid = this@run.data.view.owner.mid, seasonId = it)
                } ?: run {
                    _mPlayerState.update { it.copy(videoArchiveMeta = null) }
                }
            }
            viewModelScope.launch { getPageList(bvid = params.bvid, cid = params.cid) }
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
        firstPage?.run {
            val count = ceil((this.data.page.total - pageSize) / pageSize.toFloat()).toInt()
            val leftovers = mutableListOf<ArchiveItem>()
            for (i in 0 until count) {
                biliPlayRepository.getArchives(
                    mid = mid,
                    seasonId = seasonId,
                    pageNum = pageNum + i + 1,
                    pageSize = pageSize
                )?.let {
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
        pageList?.let { data ->
            _mPlayerState.update {
                it.copy(
                    videoPageList = if (data.data.size <= 1) null else data.data,
                    currentPageListIndex = data.data.indexOfFirst { item -> item.cid == cid }
                )
            }
        }
    }

    fun uploadVideoHistory(duration: Long) {
        viewModelScope.launch {
            biliPlayRepository.uploadVideoHistory(
                aid = params.aid.toString(),
                cid = params.cid.toString(),
                progress = duration
            )
        }
    }

    fun videoMenuActionHandle(action: VideoAction.VideoMenuAction) {
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

            else -> {}
        }
    }

    fun videoPlayActionHandle(action: VideoAction.VideoPlayAction) {
        when (action) {
            is VideoAction.VideoPlayAction.SwitchPlayListAction -> {
                viewModelScope.launch { updateParams(params.copy(cid = action.cid)) }
            }

            is VideoAction.VideoPlayAction.SwitchEpisodeAction -> {
                viewModelScope.launch {
                    updateParams(
                        params.copy(
                            epId = action.episodeId,
                            aid = action.aid,
                            cid = action.cid,
                            bvid = action.bvid
                        )
                    )
                }
            }

            is VideoAction.VideoPlayAction.SwitchSeasonAction -> {
                viewModelScope.launch {
                    updateParams(
                        params.copy(
                            seasonId = action.seasonId,
                            mediaId = null,
                            epId = null
                        )
                    )
                }
            }
        }
    }

    private fun videoLike(like: Int) {
        viewModelScope.launch {
            biliPlayRepository.videoLike(
                aid = params.aid,
                bvid = params.bvid,
                like = like
            )?.run {
                val hasLike = like == 1
                _mPlayerState.update { it.copy(hasLike = hasLike) }
                screenActionHandle(
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
                aid = params.aid,
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
        playlistRepository.getFolderSimpleList(params.aid)?.apply {
            Log.d(TAG, "getFolderSimpleList: $this")
            _mPlayerState.update {
                it.copy(
                    folders = this.list,
                    hasFavoured = this.list.any { item -> item.favState == 1 })
            }
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
        val (name, cover) = when {
            params.isVideo -> {
                val view = _mPlayerState.value.videoDetail?.view
                Pair(view?.title, view?.pic)
            }

            else -> {
                val episode =
                    _mPlayerState.value.bangumiDetail?.episodes?.find { it.epId == params.epId }
                val title = episode?.run {
                    longTitle.ifBlank {
                        val str = title.toDoubleOrNull()
                        when {
                            str != null && title.contains(".") -> {
                                "第${title}集"
                            }

                            str != null -> {
                                "第${title.toInt()}集"
                            }

                            else -> title
                        }
                    }
                }
                Pair(title, episode?.cover)
            }
        }
        downloadManager.addTask(
            id = params.bvid,
            aid = params.aid,
            cid = params.cid,
            name = name,
            cover = cover ?: "",
            quality = quality.second,
            videoUrls = urls.first,
            audioUrls = urls.second,
            archive = when {
                params.isVideo -> null
                else -> _mPlayerState.value.bangumiDetail?.seasonTitle
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }
}
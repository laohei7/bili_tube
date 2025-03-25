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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.ceil

@UnstableApi
internal class PlayerViewModel(
    private val biliPlayRepository: BiliPlayRepository,
    private val playlistRepository: BiliPlaylistRepository,
    private val defaultMediaManager: DefaultMediaManager,
    private var params: Route.Play,
    private val screenManager: DefaultScreenManager,
) : ViewModel(), MediaManager by defaultMediaManager, ScreenManager by screenManager {

    companion object {
        private val TAG = PlayerViewModel::class.simpleName
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
        defaultMediaManager.playEndCallback = {
            uploadVideoHistory(exoPlayer().duration / 1000)
        }
    }

    private fun playErrorCallback() {

    }


    suspend fun updateParams(other: Route.Play) =
        withContext(Dispatchers.IO) {
            params = other
            withContext(Dispatchers.Main) {
                toggleLoading()
            }
            if (params.cid != -1L) {
                launch {
                    getVideoURL()
                }
            }
            launch {
                getVideoDetail()
                videoReplies = biliPlayRepository.getVideoReplyPager(
                    type = 1,
                    oid = params.aid.toString()
                )
            }
            launch {
                hasLike()
                hasCoin()
                hasFavoured()
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
        val response = biliPlayRepository.getPlayURL(
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
        }
    }

    private suspend fun getVideoDetail() {
        val response = biliPlayRepository.getVideoDetail(
            aid = params.aid,
            bvid = params.bvid,
        )
        response?.run {
            viewModelScope.launch {
                this@run.data.view.seasonId?.let {
                    getArchives(mid = this@run.data.view.owner.mid, seasonId = it)
                }
            }
            _mPlayerState.update {
                it.copy(videoDetail = data)
            }
            if (params.cid == -1L) {
                params = params.copy(
                    cid = data.view.cid
                )
                getVideoURL()
            }
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
            else->{}
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
            biliPlayRepository.videoFolderDeal(
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

    override fun onCleared() {
        super.onCleared()
        release()
    }
}
package com.laohei.bili_tube.presentation.player

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.laohei.bili_sdk.module_v2.video.ArchiveItem
import com.laohei.bili_tube.app.Route
import com.laohei.bili_tube.presentation.player.state.media.DefaultMediaManager
import com.laohei.bili_tube.presentation.player.state.media.MediaManager
import com.laohei.bili_tube.presentation.player.state.media.Quality
import com.laohei.bili_tube.presentation.player.state.screen.DefaultScreenManager
import com.laohei.bili_tube.presentation.player.state.screen.ScreenManager
import com.laohei.bili_tube.repository.BiliPlayRepository
import kotlinx.coroutines.Dispatchers
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
        withContext(Dispatchers.IO) {
            if (params.cid != -1L) {
                launch {
                    getVideoURL()
                }
            }
            launch {
                getVideoDetail()
            }
        }
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
//            val sources = if (data.dash != null) {
//                data.dash!!.video.zip(data.dash!!.audio) { video, audio ->
//                    VideoSource(
//                        videoUrl = video.baseUrl,
//                        audioUrl = audio.baseUrl,
//                        width = video.width,
//                        height = video.height,
//                        sourceType = SourceType.Normal
//                    )
//                }
//            } else {
//                data.durl?.map { VideoSource(videoUrl = it.url, sourceType = SourceType.Normal) }
//            }
            withContext(Dispatchers.Main) {
//                play(sources ?: emptyList())
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
                updateParams(
                    params.copy(
                        cid = data.view.cid
                    )
                )
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
                    } + 1
                )
            }
        }
    }

    fun uploadVideoHistory(duration: Long) {
        viewModelScope.launch {
            Log.d(TAG, "uploadVideoHistory: upload history")
            biliPlayRepository.uploadVideoHistory(
                aid = params.aid.toString(),
                cid = params.cid.toString(),
                progress = duration
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }
}
package com.laohei.bili_tube.features.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laohei.bili_tube.room.entity.DownloadTask
import com.laohei.bili_tube.utill.download.DownloadManager
import kotlinx.coroutines.launch

class DownloadViewModel(
    private val downloadManager: DownloadManager
) : ViewModel() {
    val downloadQueue = downloadManager.downloadQueue

    fun pauseTask(task: DownloadTask) {
        viewModelScope.launch { downloadManager.pauseTask(task) }
    }

    fun startTask(task: DownloadTask) {
        viewModelScope.launch { downloadManager.startTask(task) }
    }

    fun deleteTask(task: DownloadTask) {
        viewModelScope.launch { downloadManager.deleteTask(task) }
    }
}
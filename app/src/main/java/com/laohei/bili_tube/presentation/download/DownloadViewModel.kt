package com.laohei.bili_tube.presentation.download

import androidx.lifecycle.ViewModel
import com.laohei.bili_tube.utill.download.DownloadManager

class DownloadViewModel(
    downloadManager: DownloadManager
) : ViewModel() {
    val downloadQueue = downloadManager.downloadQueue
}
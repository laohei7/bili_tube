package com.laohei.bili_tube.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.coroutines.Job

@Entity(tableName = "tb_downloads")
data class DownloadTask(
    @PrimaryKey val id: String,
    val aid: Long,
    val cid: Long,
    val name: String,
    val cover: String,
    val quality: String,
    val videoUrls: List<String>,
    val audioUrls: List<String>? = null,
    val videoFile: String? = null,
    val audioFile: String? = null,
    val mergedFile: String? = null,
    val archive: String? = null,
    val status: DownloadStatus = DownloadStatus.PENDING,
    val progress: Int = 0
)

enum class DownloadStatus {
    PENDING, DOWNLOADING, PROCESSING, COMPLETED, FAILED, PAUSE
}

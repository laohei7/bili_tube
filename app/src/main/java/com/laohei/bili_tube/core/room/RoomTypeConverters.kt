package com.laohei.bili_tube.core.room

import androidx.room.TypeConverter
import com.laohei.bili_tube.model.DownloadStatus

class RoomTypeConverters {
    @TypeConverter
    fun fromList(value: List<String>): String {
        return value.joinToString(";")
    }

    @TypeConverter
    fun toList(value: String): List<String> {
        return value.split(";")
    }

    @TypeConverter
    fun fromDownloadStatus(status: DownloadStatus): Int {
        return when (status) {
            DownloadStatus.PENDING -> 1
            DownloadStatus.DOWNLOADING -> 2
            DownloadStatus.PROCESSING -> 3
            DownloadStatus.COMPLETED -> 4
            DownloadStatus.FAILED -> 5
        }
    }

    @TypeConverter
    fun toDownloadStatus(status: Int): DownloadStatus {
        return when (status) {
            2 -> DownloadStatus.DOWNLOADING
            3 -> DownloadStatus.PROCESSING
            4 -> DownloadStatus.COMPLETED
            5 -> DownloadStatus.FAILED
            else -> DownloadStatus.PENDING
        }
    }
}
package com.laohei.bili_tube.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock

@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val keyword: String,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)

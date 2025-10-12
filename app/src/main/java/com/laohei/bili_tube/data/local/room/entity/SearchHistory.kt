package com.laohei.bili_tube.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Entity(tableName = "search_history")
data class SearchHistory @OptIn(ExperimentalTime::class) constructor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val keyword: String,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)

package com.laohei.bili_tube.utill

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.round

fun Long.formatTimeString(isMill: Boolean = true): String {
    val totalSecond = if (isMill) this / 1000 else this
    val hours = totalSecond / 3600
    val minutes = (totalSecond % 3600) / 60
    val seconds = totalSecond % 60

    val formattedMinutes = if (minutes < 10) "0$minutes" else "$minutes"
    val formattedSeconds = if (seconds < 10) "0$seconds" else "$seconds"

    return if (hours > 0) {
        val formattedHours = if (hours < 10) "0$hours" else "$hours"
        "$formattedHours:$formattedMinutes:$formattedSeconds"
    } else {
        "$formattedMinutes:$formattedSeconds"
    }
}

fun Int.formatTimeString(isMill: Boolean = true): String = this.toLong().formatTimeString(isMill)

fun Long.toViewString(): String {
    return if (this < 10_000) {
        "$this"
    } else if (this < 1_000_000_000) {
        val number = if ((this * 1.0 % 10_000).toInt() == 0) {
            this / 10_000
        } else {
            round((this * 1.0 / 10_000) * 100) / 100.0
        }
        "${number}万"
    } else {
        val number = if ((this * 1.0 % 1_000_000_000).toInt() == 0) {
            this / 1_000_000_000
        } else {
            round((this * 1.0 / 1_000_000_000) * 100) / 100.0
        }
        "${number}亿"
    }
}

fun Int.toViewString(): String = this.toLong().toViewString()

fun Long.toTimeAgoString(): String {
    val timestamp = Instant.fromEpochSeconds(this)
    val now = Clock.System.now()
    val duration = now - timestamp
    return when {
        duration.inWholeMinutes < 1 -> "刚刚"
        duration.inWholeMinutes < 60 -> "${duration.inWholeMinutes} 分钟前"
        duration.inWholeHours < 24 -> "${duration.inWholeHours} 小时前"
        duration.inWholeDays < 7 -> "${duration.inWholeDays} 天前"
        duration.inWholeDays < 30 -> "${duration.inWholeDays / 7} 周前"
        duration.inWholeDays < 365 -> "${duration.inWholeDays / 30} 个月前"
        else -> "${duration.inWholeDays / 365} 年前"
    }
}

fun Int.toTimeAgoString() = this.toLong().toTimeAgoString()

fun Long.formatDateToString(isMill: Boolean = true): String {
    val instant = when {
        isMill -> Instant.fromEpochMilliseconds(this)
        else -> Instant.fromEpochSeconds(this)
    }
    val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
    return "${localDate.year}年${localDate.monthNumber}月${localDate.dayOfMonth}日"
}

fun Int.formatDateToString(isMill: Boolean = true) = this.toLong().formatDateToString(isMill)
package com.laohei.bili_tube.utill

import android.os.Build
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.math.round

private val WEEK_DAYS =
    arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")

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

fun Long.formatDateTimeToString(isMill: Boolean = true): String {
    val dateTime = if (isMill) {
        Instant.fromEpochMilliseconds(this)
    } else {
        Instant.fromEpochSeconds(this)
    }.toLocalDateTime(TimeZone.currentSystemDefault())

    return "%02d:%02d".format(
        dateTime.hour,
        dateTime.minute
    )
}

fun Long.toTimeAgoString2(isMill: Boolean = true): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val date = if (isMill) {
        Instant.fromEpochMilliseconds(this)
    } else {
        Instant.fromEpochSeconds(this)
    }.toLocalDateTime(TimeZone.currentSystemDefault()).date

    return when {
        date == now -> "今天"
        date == now.minus(1, DateTimeUnit.DAY) -> "昨天"
        date >= now.minus(now.dayOfWeek.ordinal, DateTimeUnit.DAY) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.CHINA)
            } else {
                this.getWeekdayName()
            }
        }

        date.year == now.year -> "%02d月%02d日".format(
            date.monthNumber,
            date.dayOfMonth
        ) // 本年显示 "MM-dd"
        else -> "%04d年%02d月%02d日".format(
            date.year,
            date.monthNumber,
            date.dayOfMonth
        ) // 其他年份显示完整日期
    }
}

private fun Long.getWeekdayName(isMill: Boolean = true): String {
    val date = Calendar.getInstance()
        .apply { timeInMillis = this@getWeekdayName * if (isMill) 1000 else 1 }
    return WEEK_DAYS[date[Calendar.DAY_OF_WEEK] - 1]
}

fun Long.formatDateToString(isMill: Boolean = true): String {
    val instant = when {
        isMill -> Instant.fromEpochMilliseconds(this)
        else -> Instant.fromEpochSeconds(this)
    }
    val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
    return "${localDate.year}年${localDate.monthNumber}月${localDate.dayOfMonth}日"
}

fun Int.formatDateToString(isMill: Boolean = true) = this.toLong().formatDateToString(isMill)

fun Long.formatDateToYearString(isMill: Boolean = true): String {
    val instant = when {
        isMill -> Instant.fromEpochMilliseconds(this)
        else -> Instant.fromEpochSeconds(this)
    }
    val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
    return "${localDate.year}年"
}

fun Int.formatDateToYearString(isMill: Boolean = true) =
    this.toLong().formatDateToYearString(isMill)

fun Float.areFloatsEqualCompareTo(other: Float?, epsilon: Float = 1e-6f): Boolean {
    return other?.let {
        this.compareTo(it) == 0 || abs(this - other) < epsilon
    } == true
}
@file:OptIn(ExperimentalTime::class)

package com.laohei.bili_tube.util

import android.os.Build
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.math.round
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private val WEEK_DAYS =
    arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")

fun Long.toTimeString(isMillis: Boolean = true): String {
    val totalSeconds = if (isMillis) this / 1000 else this
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}

fun Int.toTimeString(isMillis: Boolean = true): String = this.toLong().toTimeString(isMillis)

fun Long.toViewString(): String {
    return when {
        this < 10_000 -> "$this"

        this < 1_0000_0000 -> {
            formatNumber(this, 10_000, "万")
        }

        else -> {
            formatNumber(this, 1_0000_0000, "亿")
        }
    }
}

fun Int.toViewString(): String = this.toLong().toViewString()

private fun formatNumber(value: Long, unit: Long, suffix: String): String {
    val number = value.toDouble() / unit
    val formatted = if (value % unit == 0L) {
        number.toLong().toString()
    } else {
        (round(number * 100) / 100.0).toString()
    }
    return formatted + suffix
}

fun Long.toTimeAgoString(isMillis: Boolean = true): String {
    val timestamp = if (isMillis) {
        Instant.fromEpochMilliseconds(this)
    } else {
        Instant.fromEpochSeconds(this)
    }
    val now = Clock.System.now()
    val duration = now - timestamp

    return when {
        duration.inWholeMinutes < 1 -> "刚刚"
        duration.inWholeHours < 1 -> "${duration.inWholeMinutes} 分钟前"
        duration.inWholeDays < 1 -> "${duration.inWholeHours} 小时前"
        duration.inWholeDays < 7 -> "${duration.inWholeDays} 天前"
        duration.inWholeDays < 30 -> "${duration.inWholeDays / 7} 周前"
        duration.inWholeDays < 365 -> "${duration.inWholeDays / 30} 个月前"
        else -> "${duration.inWholeDays / 365} 年前"
    }
}

fun Int.toTimeAgoString(isMillis: Boolean = true) = this.toLong().toTimeAgoString(isMillis)

fun Long.formatAs(
    isMillis: Boolean = true,
    pattern: String = "HH:mm",
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String {
    val instant = if (isMillis) {
        Instant.fromEpochMilliseconds(this)
    } else {
        Instant.fromEpochSeconds(this)
    }
    val dt = instant.toLocalDateTime(timeZone)

    return pattern
        .replace("yyyy", dt.year.toString())
        .replace("MM", "%02d".format(dt.month.number))
        .replace("dd", "%02d".format(dt.day))
        .replace("HH", "%02d".format(dt.hour))
        .replace("mm", "%02d".format(dt.minute))
        .replace("ss", "%02d".format(dt.second))
}

fun Long.toFriendlyDateString(isMillis: Boolean = true): String {
    val tz = TimeZone.currentSystemDefault()
    val nowDate = Clock.System.now().toLocalDateTime(tz).date
    val targetDate = (if (isMillis) {
        Instant.fromEpochMilliseconds(this)
    } else {
        Instant.fromEpochSeconds(this)
    }).toLocalDateTime(tz).date

    return when {
        targetDate == nowDate -> "今天"
        targetDate == nowDate.minus(1, DateTimeUnit.DAY) -> "昨天"
        targetDate >= nowDate.minus(nowDate.dayOfWeek.ordinal, DateTimeUnit.DAY) -> { // 本周内
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                targetDate.dayOfWeek.name
            } else {
                this.toWeekdayName() // 兼容低版本
            }
        }

        targetDate.year == nowDate.year -> "%02d月%02d日".format(
            targetDate.month.number,
            targetDate.day
        )

        else -> "%04d年%02d月%02d日".format(
            targetDate.year,
            targetDate.month.number,
            targetDate.day
        )
    }
}

private fun Long.toWeekdayName(isMillis: Boolean = true): String {
    val timeInMillis = if (isMillis) this else this * 1000
    val date = Calendar.getInstance().apply { this.timeInMillis = timeInMillis }
    return WEEK_DAYS[date[Calendar.DAY_OF_WEEK] - 1]
}

fun Long.toDateString(isMillis: Boolean = true): String {
    return this.formatAs(isMillis, "yyyy年MM月dd日")
}

fun Long.toYearString(isMillis: Boolean = true): String {
    return this.formatAs(isMillis, "yyyy年")
}

fun Int.toYearString(isMill: Boolean = true) =
    this.toLong().toYearString(isMill)

fun Float.isNearlyEqual(other: Float?, epsilon: Float = 1e-6f): Boolean {
    return other != null && abs(this - other) < epsilon
}
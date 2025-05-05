package com.laohei.bili_tube.utill

import com.laohei.bili_sdk.module_v2.video.EpisodeModel

fun EpisodeModel.displayTitle(): String {
    val str = title.toDoubleOrNull()
    val prefixTitle = when {
        str != null && title.contains(".") -> {
            "第${title}集"
        }

        str != null -> {
            "第${title.toInt()}集"
        }

        else -> title
    }
    return longTitle.run {
        when {
            isBlank() -> prefixTitle
            else -> "$prefixTitle $this"
        }
    }
}
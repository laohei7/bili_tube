package com.laohei.bili_tube.util

import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.trackselection.MappingTrackSelector

object TrackSupportUtils {

    /**
     * 检查当前轨道是否是不支持的 Dolby Vision。
     * @return true 表示不支持该轨道
     */
    @OptIn(UnstableApi::class)
    fun isUnsupportedDolbyTrack(
        mappedTrackInfo: MappingTrackSelector.MappedTrackInfo,
        format: Format,
        rendererIndex: Int,
        groupIndex: Int,
        trackIndex: Int,
        tag: String = "DolbyTrackSupport"
    ): Boolean {
        val mimeType = format.sampleMimeType ?: return false
        if (mimeType != MimeTypes.VIDEO_DOLBY_VISION) return false

        val codec = format.codecs ?: "unknown"
        val support = mappedTrackInfo.getTrackSupport(rendererIndex, groupIndex, trackIndex)

        Log.d(tag, "Detected track: MIME=$mimeType, CODEC=$codec, support=$support")

        return if (support == C.FORMAT_HANDLED) {
            Log.i(tag, "✅ Supported Dolby Vision track: $codec")
            false
        } else {
            Log.w(tag, "❌ Unsupported Dolby Vision track: $codec")
            true
        }
    }
}

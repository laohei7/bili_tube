package com.laohei.bili_tube.util

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.audio.AudioCapabilities

object AudioSupportChecker {
    private val TAG = AudioSupportChecker::class.simpleName

    @OptIn(UnstableApi::class)
    fun supportsPcmFloat(context: Context): Boolean {
        return runCatching {
          val isSupported =  AudioCapabilities.getCapabilities(context, AudioAttributes.DEFAULT, null)
                .supportsEncoding(C.ENCODING_PCM_FLOAT)
            Log.d(TAG, "supportsPcmFloat: $isSupported")
            isSupported
        }.getOrElse {
            Log.e(TAG, "supportsPcmFloat: ${it.message}")
            false
        }
    }
}
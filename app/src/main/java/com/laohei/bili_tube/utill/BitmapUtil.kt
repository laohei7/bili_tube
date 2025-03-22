package com.laohei.bili_tube.utill

import android.graphics.Bitmap
import android.os.Build


fun Bitmap.toNonHardwareBitmap(): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && this.config == Bitmap.Config.HARDWARE) {
        this.copy(Bitmap.Config.ARGB_8888, true)
    } else {
        this
    }
}
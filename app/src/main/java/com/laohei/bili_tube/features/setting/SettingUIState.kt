package com.laohei.bili_tube.features.setting

data class SettingUIState(
    val mobileNetVideoQuality:Int = 80,
    val wlanVideoQuality:Int = Int.MAX_VALUE,
    val mobileNetAudioQuality:Int = 30280,
    val wlanAudioQuality:Int = 30251,
    val autoSkipOpEnd:Boolean = false,
    val mergeSource:Boolean = false,
    val sharedSource: Boolean = false,
    val exportCrashDoc: Boolean = false,
)

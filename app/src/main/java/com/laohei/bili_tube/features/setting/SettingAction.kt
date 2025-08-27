package com.laohei.bili_tube.features.setting

enum class NetworkType {
    Mobile, Wlan
}

sealed interface SettingAction {
    data class ChangeVideoQuality(val type: NetworkType, val quality: Int) : SettingAction
    data class ChangeAudioQuality(val type: NetworkType, val quality: Int) : SettingAction
    data class AutoSkipAction(val skip: Boolean) : SettingAction
    data class MergeSourceAction(val merge: Boolean) : SettingAction
    data class SharedSourceAction(val shared: Boolean) : SettingAction
    data class ExportCrashDoc(val exported: Boolean) : SettingAction
}
package com.laohei.bili_tube.presentation.settings

enum class NetworkType {
    Mobile, Wlan
}

sealed interface SettingsAction {
    data class ChangeVideoQuality(val type: NetworkType, val quality: Int) : SettingsAction
    data class ChangeAudioQuality(val type: NetworkType, val quality: Int) : SettingsAction
    data class AutoSkipAction(val skip: Boolean) : SettingsAction
    data class MergeSourceAction(val merge: Boolean) : SettingsAction
}
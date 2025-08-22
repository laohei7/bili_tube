package com.laohei.bili_tube.core.action

sealed class VideoSettingAction {
    data class AutoSkip(val flag: Boolean) : VideoSettingAction()
}
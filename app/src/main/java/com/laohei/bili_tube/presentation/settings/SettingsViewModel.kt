package com.laohei.bili_tube.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laohei.bili_tube.core.AUTO_SKIP_KEY
import com.laohei.bili_tube.core.MERGE_SOURCE_KEY
import com.laohei.bili_tube.core.MOBILE_NET_AUDIO_QUALITY
import com.laohei.bili_tube.core.MOBILE_NET_VIDEO_QUALITY
import com.laohei.bili_tube.core.WLAN_AUDIO_QUALITY
import com.laohei.bili_tube.core.WLAN_VIDEO_QUALITY
import com.laohei.bili_tube.core.util.PreferencesUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    private val preferenceUtils: PreferencesUtil
) : ViewModel() {

    private val _mState = MutableStateFlow(SettingsState())
    val state = _mState.onStart {
        loadSettings()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _mState.value
    )

    private fun loadSettings() {
        _mState.update {
            it.copy(
                mobileNetVideoQuality = preferenceUtils.getValue(
                    MOBILE_NET_VIDEO_QUALITY,
                    it.mobileNetVideoQuality
                ),
                wlanVideoQuality = preferenceUtils.getValue(
                    WLAN_VIDEO_QUALITY,
                    it.wlanVideoQuality
                ),
                mobileNetAudioQuality = preferenceUtils.getValue(
                    MOBILE_NET_AUDIO_QUALITY,
                    it.mobileNetAudioQuality
                ),
                wlanAudioQuality = preferenceUtils.getValue(
                    WLAN_AUDIO_QUALITY,
                    it.wlanAudioQuality
                ),
                autoSkipOpEnd = preferenceUtils.getValue(AUTO_SKIP_KEY, it.autoSkipOpEnd)
            )
        }
    }

    fun handleSettingsAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.ChangeAudioQuality -> {
                when (action.type) {
                    NetworkType.Mobile -> {
                        _mState.update { it.copy(mobileNetAudioQuality = action.quality) }
                        preferenceUtils.setValue(MOBILE_NET_AUDIO_QUALITY, action.quality)
                    }

                    NetworkType.Wlan -> {
                        _mState.update { it.copy(wlanAudioQuality = action.quality) }
                        preferenceUtils.setValue(WLAN_AUDIO_QUALITY, action.quality)
                    }
                }
            }

            is SettingsAction.ChangeVideoQuality -> {
                when (action.type) {
                    NetworkType.Mobile -> {
                        _mState.update { it.copy(mobileNetVideoQuality = action.quality) }
                        preferenceUtils.setValue(MOBILE_NET_VIDEO_QUALITY, action.quality)
                    }

                    NetworkType.Wlan -> {
                        _mState.update { it.copy(wlanVideoQuality = action.quality) }
                        preferenceUtils.setValue(WLAN_VIDEO_QUALITY, action.quality)
                    }
                }
            }

            is SettingsAction.AutoSkipAction -> {
                _mState.update { it.copy(autoSkipOpEnd = action.skip) }
                preferenceUtils.setValue(AUTO_SKIP_KEY, action.skip)
            }

            is SettingsAction.MergeSourceAction -> {
                _mState.update { it.copy(mergeSource = action.merge) }
                preferenceUtils.setValue(MERGE_SOURCE_KEY, action.merge)
            }
        }
    }

}
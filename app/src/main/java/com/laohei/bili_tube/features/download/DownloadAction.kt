package com.laohei.bili_tube.features.download

import com.laohei.bili_tube.data.local.room.entity.DownloadTask
import com.laohei.bili_tube.nav.AppRoute

internal sealed interface DownloadAction {
    data object UpPressAction : DownloadAction
    data class NavigateAction(val route: AppRoute) : DownloadAction
    data object SettingsAction : DownloadAction
    data class PauseDownloadAction(val task: DownloadTask) : DownloadAction
    data class StartDownloadAction(val task: DownloadTask) : DownloadAction
    data class DeleteTaskAction(val task: DownloadTask) : DownloadAction
    data class ShowSnackbarAction(val message: String) : DownloadAction
}
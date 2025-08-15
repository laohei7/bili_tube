package com.laohei.bili_tube.features.player

import com.laohei.bili_sdk.apis.UserRelationAction
import com.laohei.bili_tube.PlayParam

sealed class VideoMenuAction {
    data class LikeAction(val like: Int) : VideoMenuAction() // 1点赞，2取消
    data class AddToFoldersAction(val addAids: Set<Long>, val delAids: Set<Long>) : VideoMenuAction()

    data class ModifyUserRelationAction(val action: UserRelationAction) : VideoMenuAction()

    data class AddCoinAction(val coin: Int) : VideoMenuAction()

    data class SwitchVideoAction(val playParam: PlayParam): VideoMenuAction()

    data class SwitchEpisodeAction(
        val episodeId: Long,
        val aid: Long,
        val cid: Long,
        val bvid: String
    ): VideoMenuAction()

    data class SwitchSeasonAction(val seasonId: Long): VideoMenuAction()

    data object AddToViewAction: VideoMenuAction()

    data object GetSimpleFoldersAction: VideoMenuAction()

}
package com.laohei.bili_tube.features.player

import com.laohei.bili_sdk.apis.UserRelationAction
import com.laohei.bili_tube.model.play.MediaPlayConfig

sealed class VideoMenuAction {
    data class Like(val like: Int) : VideoMenuAction() // 1点赞，2取消
    data class AddToFolders(val addAids: Set<Long>, val delAids: Set<Long>) : VideoMenuAction()

    data class ModifyUserRelation(val action: UserRelationAction) : VideoMenuAction()

    data class AddCoin(val coin: Int) : VideoMenuAction()

    data class SwitchVideo(val playParam: MediaPlayConfig) : VideoMenuAction()

    data class SwitchVideoPage(val cid: Long) : VideoMenuAction()

    data class SwitchEpisode(
        val episodeId: Long,
        val aid: Long,
        val cid: Long,
        val bvid: String
    ) : VideoMenuAction()

    data class SwitchSeason(val seasonId: Long) : VideoMenuAction()

    data object AddToView : VideoMenuAction()

    data object LoadSimpleFolders : VideoMenuAction()

}
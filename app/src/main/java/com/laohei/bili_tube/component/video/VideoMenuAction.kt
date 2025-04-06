package com.laohei.bili_tube.component.video

interface VideoAction {
    sealed class VideoMenuAction : VideoAction {
        data class VideoLikeAction(val like: Int) : VideoMenuAction() // 1点赞，2取消
        data class VideoDislikeAction(val dislike: Int) : VideoMenuAction() // 0取消，1点踩
        data class CoinAction(val coin: Int) : VideoMenuAction()
        data class CollectAction(val addAids: Set<Long>, val delAids: Set<Long>) : VideoMenuAction()
        data class CollectActionByAid(val addAids: Set<Long>, val delAids: Set<Long>,val aid:Long) : VideoMenuAction()
    }

    sealed class VideoSheetAction : VideoAction {
        data class PlaylistAction(val aid: Long) : VideoSheetAction()
        data class AddToViewAction(val aid: Long=0,val bvid:String=""):VideoSheetAction()
    }

    sealed class VideoPlayAction:VideoAction{
        data class SwitchPlayListAction(val cid:Long) :VideoPlayAction()
    }
}
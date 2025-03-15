package com.laohei.bili_tube.presentation.player.component.reply

import com.laohei.bili_sdk.model.VideoReplyItem

sealed interface ReplySheetAction {
    data class ToChildReplyListAction(val item: VideoReplyItem) : ReplySheetAction
}
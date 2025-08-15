package com.laohei.bili_tube.features.player.component.reply

import com.laohei.bili_sdk.module_v2.reply.ReplyItem

sealed interface VideoReplyAction {
    data class ToChildReplyListAction(val item: ReplyItem) : VideoReplyAction
}
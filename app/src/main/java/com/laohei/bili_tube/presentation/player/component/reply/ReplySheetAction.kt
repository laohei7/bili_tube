package com.laohei.bili_tube.presentation.player.component.reply

import com.laohei.bili_sdk.module_v2.reply.ReplyItem

sealed interface ReplySheetAction {
    data class ToChildReplyListAction(val item: ReplyItem) : ReplySheetAction
}
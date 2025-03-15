package com.laohei.bili_tube.presentation.player.state.screen

import com.laohei.bili_tube.app.Route

sealed interface ScreenAction {
    data class SwitchVideoAction(val params: Route.Play) : ScreenAction
    data object ShowReplyAction : ScreenAction
    data object ShowVideoDetailAction : ScreenAction
    data object ToUserSpaceAction : ScreenAction
    data object SubscribeAction : ScreenAction
    data object ShowRelatedAction : ScreenAction
}
package com.laohei.bili_tube.presentation.home.state

import kotlinx.coroutines.flow.StateFlow

interface HomePageManager {
    val homeState: StateFlow<HomeState>


    fun homeActionHandle(action: HomePageAction)

    fun updateState(other:HomeState)

}
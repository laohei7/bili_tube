package com.laohei.bili_tube.presentation.home.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DefaultHomePageManager : HomePageManager {

    private val _mHomeState = MutableStateFlow(HomeState())

    override val homeState: StateFlow<HomeState>
        get() = _mHomeState.asStateFlow()

    override fun homeActionHandle(action: HomePageAction) {
        when (action) {
            is HomePageAction.ShowFolderSheetAction -> {
                _mHomeState.update { it.copy(isShowFolderSheet = action.flag) }
            }

            is HomePageAction.CreatedFolderAction -> {
                _mHomeState.update {
                    it.copy(
                        isShowFolderSheet = action.flag.not(),
                        isShowAddFolder = action.flag
                    )
                }
            }

            is HomePageAction.ShowVideoMenuSheetAction -> {
                _mHomeState.update {
                    it.copy(
                        isShowMenuSheet = action.flag,
                        currentAid = action.aid,
                        currentBvid = action.bvid
                    )
                }
            }

            is HomePageAction.AnimeFilterAction -> {
                _mHomeState.update {
                    when {
                        action.isAnime -> {
                            it.copy(
                                animationFilterModel = it.animationFilterModel.update(
                                    action.key,
                                    action.value
                                )
                            )
                        }

                        else -> {
                            it.copy(
                                bangumiFilterModel = it.bangumiFilterModel.update(
                                    action.key,
                                    action.value
                                )
                            )
                        }
                    }

                }
            }
        }
    }

    override fun updateState(other: HomeState) {
        _mHomeState.update { other }
    }
}
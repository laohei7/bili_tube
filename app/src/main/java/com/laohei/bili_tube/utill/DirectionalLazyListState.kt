package com.laohei.bili_tube.utill

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class ScrollDirection {
    Up, Down, None
}

@Stable
class DirectionalLazyListState(
    private val lazyListState: LazyListState,
    private val coroutineScope: CoroutineScope
) {
    private var positionY = lazyListState.firstVisibleItemScrollOffset
    private var visibleItem = lazyListState.firstVisibleItemIndex

    private var currentTime = System.currentTimeMillis()
    var scrollDirection by mutableStateOf(ScrollDirection.None)

    init {

        coroutineScope.launch {
            while (isActive) {
                delay(120)
                if (System.currentTimeMillis() - currentTime > 120) {
                    scrollDirection = ScrollDirection.None
                }
            }
        }

        snapshotFlow {
            val scrollInt = if (lazyListState.isScrollInProgress) 20000 else 10000
            val visibleItemInt = lazyListState.firstVisibleItemIndex * 10
            scrollInt + visibleItemInt + lazyListState.firstVisibleItemScrollOffset
        }
            .onEach {
                if (lazyListState.isScrollInProgress.not()) {
                    scrollDirection = ScrollDirection.None
                } else {

                    currentTime = System.currentTimeMillis()

                    val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
                    val firstVisibleItemScrollOffset =
                        lazyListState.firstVisibleItemScrollOffset

                    // We are scrolling while first visible item hasn't changed yet
                    if (firstVisibleItemIndex == visibleItem) {
                        val direction = if (firstVisibleItemScrollOffset > positionY) {
                            ScrollDirection.Down
                        } else {
                            ScrollDirection.Up
                        }
                        positionY = firstVisibleItemScrollOffset

                        scrollDirection = direction
                    } else {

                        val direction = if (firstVisibleItemIndex > visibleItem) {
                            ScrollDirection.Down
                        } else {
                            ScrollDirection.Up
                        }
                        positionY = firstVisibleItemScrollOffset
                        visibleItem = firstVisibleItemIndex
                        scrollDirection = direction
                    }
                }
            }
            .launchIn(coroutineScope)
    }


//    val scrollDirection by derivedStateOf {
//        if (lazyListState.isScrollInProgress.not()) {
//            ScrollDirection.None
//        } else {
//            val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
//            val firstVisibleItemScrollOffset =
//                lazyListState.firstVisibleItemScrollOffset
//
//            // We are scrolling while first visible item hasn't changed yet
//            if (firstVisibleItemIndex == visibleItem) {
//                val direction = if (firstVisibleItemScrollOffset > positionY) {
//                    ScrollDirection.Down
//                } else {
//                    ScrollDirection.Up
//                }
//                positionY = firstVisibleItemScrollOffset
//
//                direction
//            } else {
//
//                val direction = if (firstVisibleItemIndex > visibleItem) {
//                    ScrollDirection.Down
//                } else {
//                    ScrollDirection.Up
//                }
//                positionY = firstVisibleItemScrollOffset
//                visibleItem = firstVisibleItemIndex
//                direction
//            }
//        }
//    }
}
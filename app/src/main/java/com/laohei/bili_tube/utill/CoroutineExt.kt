package com.laohei.bili_tube.utill

import kotlinx.coroutines.delay

suspend fun withRefreshing(
    minDuration: Long = 500,
    block: suspend () -> Unit
) {
    val start = System.currentTimeMillis()
    try {
        block()
    } finally {
        val cost = System.currentTimeMillis() - start
        if (cost < minDuration) delay(minDuration - cost)
    }
}

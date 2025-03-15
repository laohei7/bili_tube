package com.laohei.bili_tube.component.placeholder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState

@Composable
fun NoMoreData(
    loadState: LoadState
){
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        when (loadState) {
            is LoadState.Loading -> CircularProgressIndicator()
            is LoadState.Error -> Text("加载失败，点击重试")
            else -> {
                Text(
                    text = "已没有更多了，亲😙~",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
    }
}
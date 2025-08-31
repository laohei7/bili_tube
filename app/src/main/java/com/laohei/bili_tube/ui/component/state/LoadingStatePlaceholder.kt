package com.laohei.bili_tube.ui.component.state

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import com.laohei.bili_tube.R

@Composable
fun LoadingStatePlaceholder(
    loadState: LoadState,
    onRetry: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        when (loadState) {
            is LoadState.Loading -> CircularProgressIndicator()
            is LoadState.Error -> Text(
                text = stringResource(R.string.str_loading_faild),
                modifier = Modifier.clickable {
                    onRetry?.invoke()
                }
            )

            else -> {
                Text(
                    text = stringResource(R.string.str_no_more_data),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
    }
}
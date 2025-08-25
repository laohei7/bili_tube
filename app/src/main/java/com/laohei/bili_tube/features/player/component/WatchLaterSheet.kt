package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.laohei.bili_sdk.module_v2.video.VideoView
import com.laohei.bili_tube.PlayParam
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.ui.component.sheet.ModalBottomSheet
import com.laohei.bili_tube.ui.component.sheet.rememberModalBottomSheet
import com.laohei.bili_tube.ui.theme.LargePadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WatchLaterSheet(
    modifier: Modifier = Modifier,
    playParam: PlayParam.MediaList,
    isWatchLaterVisible: Boolean = false,
    watchLaterList: List<VideoView>,
    lazyListState: LazyListState = rememberLazyListState(),
    bottomPadding: Dp = 0.dp,
    currentWatchLaterIndex: Int,
    onDismiss: () -> Unit = {},
    onMaskAlphaChange: (Float) -> Unit,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
) {
    val sheetState = rememberModalBottomSheet(
        skipPartiallyExpanded = true
    )
    if (isWatchLaterVisible) {
        LaunchedEffect(sheetState) {
            snapshotFlow { sheetState.requireOffset() }
                .collect { offset ->
                    onMaskAlphaChange.invoke(offset)
                }
        }
        LaunchedEffect(currentWatchLaterIndex) {
            lazyListState.scrollToItem(currentWatchLaterIndex)
        }
        ModalBottomSheet(
            modifier = modifier
                .fillMaxSize(),
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = LargePadding, topEnd = LargePadding),
            containerColor = MaterialTheme.colorScheme.background,
            scrimColor = Color.Transparent,
            onDismissRequest = { onDismiss.invoke() },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.spacedBy(LargePadding)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = LargePadding),
                    text = buildAnnotatedString {
                        append(playParam.title)
                        withStyle(
                            style = SpanStyle(
                                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                                color = Color.Gray
                            )
                        ) {
                            append("(")
                            append("${currentWatchLaterIndex + 1}")
                            append("/")
                            append("${playParam.count}")
                            append(")")
                        }

                    },
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium,
                )

                WatchLaterList(
                    listState = lazyListState,
                    playParam = playParam,
                    watchLaterList = watchLaterList,
                    bottomPadding = bottomPadding,
                    currentWatchLaterIndex = currentWatchLaterIndex,
                    onVideoMenuAction = onVideoMenuAction
                )
            }
        }
    }
}
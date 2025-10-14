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
import androidx.paging.compose.LazyPagingItems
import com.laohei.bili_sdk.model_v2.folder.MediaItem
import com.laohei.bili_tube.model.play.MediaPlayConfig
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.ui.bottomsheet.ModalBottomSheet
import com.laohei.bili_tube.ui.bottomsheet.rememberModalBottomSheet
import com.laohei.bili_tube.ui.theme.PaddingLg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FolderMediaSheet(
    modifier: Modifier = Modifier,
    playParam: MediaPlayConfig.MediaFolderConfig,
    isFolderMediaVisible: Boolean = false,
    folderMediaList: LazyPagingItems<MediaItem>,
    lazyListState: LazyListState = rememberLazyListState(),
    bottomPadding: Dp = 0.dp,
    currentFolderMediaIndex: Int,
    onDismiss: () -> Unit = {},
    onMaskAlphaChange: (Float) -> Unit,
    onVideoMenuAction: (VideoMenuAction) -> Unit,
) {
    if (!isFolderMediaVisible) return
    val sheetState = rememberModalBottomSheet(skipPartiallyExpanded = true)
    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.requireOffset() }
            .collect { offset ->
                onMaskAlphaChange.invoke(offset)
            }
    }
    LaunchedEffect(currentFolderMediaIndex) {
        lazyListState.scrollToItem(currentFolderMediaIndex)
    }
    ModalBottomSheet(
        modifier = modifier
            .fillMaxSize(),
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = PaddingLg, topEnd = PaddingLg),
        containerColor = MaterialTheme.colorScheme.background,
        scrimColor = Color.Transparent,
        onDismissRequest = { onDismiss.invoke() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(PaddingLg)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PaddingLg),
                text = buildAnnotatedString {
                    append(playParam.title)
                    withStyle(
                        style = SpanStyle(
                            fontSize = MaterialTheme.typography.labelMedium.fontSize,
                            color = Color.Gray
                        )
                    ) {
                        append("(")
                        append("${currentFolderMediaIndex + 1}")
                        append("/")
                        append("${playParam.count}")
                        append(")")
                    }

                },
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
            )

            FolderMediaList(
                listState = lazyListState,
                playParam = playParam,
                folderMediaList = folderMediaList,
                bottomPadding = bottomPadding,
                currentFolderMediaIndex = currentFolderMediaIndex,
                onVideoMenuAction = onVideoMenuAction
            )
        }
    }
}
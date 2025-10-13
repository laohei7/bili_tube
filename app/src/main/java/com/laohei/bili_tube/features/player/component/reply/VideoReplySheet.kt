package com.laohei.bili_tube.features.player.component.reply

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.laohei.bili_sdk.model_v2.reply.ReplyItem
import com.laohei.bili_tube.features.player.component.ReplyList
import com.laohei.bili_tube.ui.bottomsheet.ModalBottomSheet
import com.laohei.bili_tube.ui.bottomsheet.ModalBottomSheetProperties
import com.laohei.bili_tube.ui.bottomsheet.rememberModalBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoReplySheet(
    isShowReplyUI: Boolean,
    shouldHideSystemBar: Boolean,
    modifier: Modifier = Modifier,
    replies: LazyPagingItems<ReplyItem>,
    bottomPadding: Dp = 0.dp,
    onDismiss: () -> Unit = {},
    onMaskAlphaChange: (Float) -> Unit = { _ -> }
) {
    if (!isShowReplyUI) return
    val sheetState = rememberModalBottomSheet(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var isInMainReplyList by remember { mutableStateOf(true) }

    fun closeSheet() {
        if (isInMainReplyList.not()) {
            isInMainReplyList = true
        } else {
            scope.launch {
                sheetState.hide()
                onDismiss.invoke()
            }
        }
    }
    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.requireOffset() }
            .collect { offset ->
                onMaskAlphaChange.invoke(offset)
            }
    }
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        scrimColor = Color.Transparent,
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = false,
            shouldHideSystemBar = shouldHideSystemBar
        ),
        onDismissRequest = {
            onDismiss.invoke()
        },
        onBackHandle = {
            closeSheet()
        }
    ) {
        ReplyList(
            replyItems = replies,
            isCloseButtonVisible = true,
            isInMainReplyList = isInMainReplyList,
            bottomPadding = bottomPadding,
            onMainReplyListChange = { isInMainReplyList = it },
            onBackClick = {
                when {
                    isInMainReplyList -> {
                        closeSheet()
                    }

                    else -> {
                        isInMainReplyList = true
                    }
                }
            }
        )
    }
}

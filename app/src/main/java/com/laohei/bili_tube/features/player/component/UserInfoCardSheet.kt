package com.laohei.bili_tube.features.player.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.laohei.bili_sdk.model_v2.user.UploadedVideoItem
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.model.UserProfile
import com.laohei.bili_tube.ui.bottomsheet.ModalBottomSheet
import com.laohei.bili_tube.ui.bottomsheet.rememberModalBottomSheet


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun UserInfoCardSheet(
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp,
    isShowSheet: Boolean,
    isLoading: Boolean,
    userProfile: UserProfile,
    currentBvid: String? = null,
    uploadedVideos: LazyPagingItems<UploadedVideoItem>,
    onDismiss: () -> Unit = {},
    onMaskAlphaChange: (Float) -> Unit,
    onVideoMenuAction: (VideoMenuAction) -> Unit
) {
    if (!isShowSheet) return
    val sheetState = rememberModalBottomSheet(skipPartiallyExpanded = true)
//    BackHandler(enabled = true) { onDismiss.invoke() }
    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.requireOffset() }
            .collect { offset ->
                onMaskAlphaChange.invoke(offset)
            }
    }
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        scrimColor = Color.Transparent,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = { onDismiss.invoke() },
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            UserWorkList(
                works = uploadedVideos,
                userProfile = userProfile,
                currentBvid = currentBvid,
                bottomPadding = bottomPadding,
                onVideoMenuAction = onVideoMenuAction
            )
        }
    }

}

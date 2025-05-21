package com.laohei.bili_tube.presentation.player.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.laohei.bili_sdk.apis.UserRelationAction
import com.laohei.bili_tube.R
import com.laohei.bili_tube.component.button.SubscribeButton
import com.laohei.bili_tube.component.video.VideoAction
import com.laohei.bili_tube.presentation.player.state.screen.ScreenAction
import com.laohei.bili_tube.utill.toViewString

@Composable
internal fun UserSubscriptionBar(
    face: String,
    name: String,
    fans: String,
    isSubscribed: Boolean,
    onClick: (ScreenAction) -> Unit,
    onSubscriptionChanged: (VideoAction.VideoMenuAction.UserRelationModifyAction) -> Unit
) {
    var localIsSubscribed by remember { mutableStateOf(isSubscribed) }
    val context = LocalContext.current
    val faceRequest = remember(face) {
        ImageRequest.Builder(context)
            .data(face)
            .crossfade(true)
            .build()
    }
    LaunchedEffect(isSubscribed) { localIsSubscribed = isSubscribed }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick.invoke(ScreenAction.ToUserSpaceAction) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = faceRequest,
                contentDescription = name,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.icon_loading_1_1),
                error = painterResource(R.drawable.icon_loading_1_1)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = fans,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }

        SubscribeButton(
            isSubscribed = localIsSubscribed,
            onClick = {
                onSubscriptionChanged(
                    VideoAction.VideoMenuAction.UserRelationModifyAction(
                        action = when {
                            localIsSubscribed -> UserRelationAction.UNFOLLOW
                            else -> UserRelationAction.FOLLOW
                        }
                    )
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
internal fun UserSubscriptionBarPreview() {
    UserSubscriptionBar(
        face = "",
        name = "IC实验室",
        fans = 10000.toViewString(),
        isSubscribed = false,
        onClick = {},
        onSubscriptionChanged = {}
    )
}

@Preview(showBackground = true)
@Composable
internal fun UserSubscriptionBarPreview2() {
    UserSubscriptionBar(
        face = "",
        name = "IC实验室",
        fans = 10000.toViewString(),
        isSubscribed = true,
        onClick = {},
        onSubscriptionChanged = {}
    )
}

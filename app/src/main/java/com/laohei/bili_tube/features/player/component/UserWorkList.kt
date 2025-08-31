package com.laohei.bili_tube.features.player.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_sdk.apis.UserRelationAction
import com.laohei.bili_sdk.module_v2.user.UploadedVideoItem
import com.laohei.bili_tube.model.play.PlayParam
import com.laohei.bili_tube.R
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.model.UserProfile
import com.laohei.bili_tube.ui.component.button.SubscribeButton
import com.laohei.bili_tube.ui.component.icons.Level0
import com.laohei.bili_tube.ui.component.icons.Level1
import com.laohei.bili_tube.ui.component.icons.Level2
import com.laohei.bili_tube.ui.component.icons.Level3
import com.laohei.bili_tube.ui.component.icons.Level4
import com.laohei.bili_tube.ui.component.icons.Level5
import com.laohei.bili_tube.ui.component.icons.Level6
import com.laohei.bili_tube.ui.component.widget.LabeledData
import com.laohei.bili_tube.ui.component.video.HorizontalVideoItem2
import com.laohei.bili_tube.util.toTimeString

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun UserWorkList(
    works: LazyPagingItems<UploadedVideoItem>,
    userProfile: UserProfile,
    currentBvid: String? = null,
    bottomPadding: Dp,
    onVideoMenuAction: (VideoMenuAction) -> Unit
) {
    val isSubscribed by rememberUpdatedState(userProfile.isSubscribed)
    LazyColumn {
        item {
            UserProfileWidget(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .padding(horizontal = 16.dp),
                face = userProfile.face,
                name = userProfile.name,
                sign = userProfile.sign,
                isSubscribed = isSubscribed,
                onSubscriptionClick = {
                    onVideoMenuAction(
                        VideoMenuAction.ModifyUserRelation(
                            action = when {
                                isSubscribed -> UserRelationAction.UNFOLLOW
                                else -> UserRelationAction.FOLLOW
                            }
                        )
                    )
                }
            )
        }
        item {
            UserDataWidget(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                follower = userProfile.follower,
                likeNum = userProfile.likeNum,
                attention = userProfile.attention,
            )
        }
        item {
            UserLevelAndOfficialWidget(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                level = userProfile.level,
                official = userProfile.official
            )
        }
        stickyHeader {
            Text(
                text = stringResource(R.string.str_uploaded_videos),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 8.dp)
            )
        }
        items(works.itemCount) { index ->
            val item = works[index]
            item?.let {
                HorizontalVideoItem2(
                    cover = it.cover,
                    title = it.title,
                    view = it.viewContent,
                    pubdate = it.publishTimeText,
                    progress = 0f,
                    isCurrentPlaying = it.bvid == currentBvid,
                    onClick = {
                        onVideoMenuAction(
                            VideoMenuAction.SwitchVideo(
                                PlayParam.VideoParam(
                                    aid = item.aid.toLong(),
                                    bvid = item.bvid,
                                    cid = it.cid
                                )
                            )
                        )
                    },
                    duration = it.duration.toTimeString(false)
                )
            }
        }
        item { Spacer(Modifier.height(bottomPadding)) }
    }
}


@Composable
private fun UserProfileWidget(
    modifier: Modifier = Modifier,
    face: String,
    name: String,
    sign: String,
    isSubscribed: Boolean,
    onSubscriptionClick: () -> Unit
) {
    val context = LocalContext.current

    val faceRequest = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(face)
            .crossfade(false)
            .placeholder(R.drawable.icon_loading_1_1)
            .error(R.drawable.icon_loading_1_1)
            .build()
    )
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = faceRequest,
            contentDescription = "face",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(66.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = sign,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondary
            )
        }
        SubscribeButton(
            isSubscribed = isSubscribed,
            onClick = onSubscriptionClick
        )
    }
}

@Composable
private fun UserDataWidget(
    modifier: Modifier = Modifier,
    follower: Long,
    likeNum: Long,
    attention: Long,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        LabeledData(
            data = follower,
            label = stringResource(R.string.str_follower)
        )
        LabeledData(
            data = attention,
            label = stringResource(R.string.str_attention)
        )
        LabeledData(
            data = likeNum,
            label = stringResource(R.string.str_get_like)
        )
    }
}

@Composable
private fun UserLevelAndOfficialWidget(
    modifier: Modifier = Modifier,
    level: Int,
    official: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val iconModifier = Modifier.size(32.dp)
        when (level) {
            1 -> Icon(
                imageVector = Icons.Outlined.Level1,
                contentDescription = Icons.Outlined.Level1.name,
                tint = colorResource(R.color.level1),
                modifier = iconModifier
            )

            2 -> Icon(
                imageVector = Icons.Outlined.Level2,
                contentDescription = Icons.Outlined.Level2.name,
                tint = colorResource(R.color.level2),
                modifier = iconModifier
            )

            3 -> Icon(
                imageVector = Icons.Outlined.Level3,
                contentDescription = Icons.Outlined.Level3.name,
                tint = colorResource(R.color.level3),
                modifier = iconModifier
            )

            4 -> Icon(
                imageVector = Icons.Outlined.Level4,
                contentDescription = Icons.Outlined.Level4.name,
                tint = colorResource(R.color.level4),
                modifier = iconModifier
            )

            5 -> Icon(
                imageVector = Icons.Outlined.Level5,
                contentDescription = Icons.Outlined.Level5.name,
                tint = colorResource(R.color.level5),
                modifier = iconModifier
            )

            6 -> Icon(
                imageVector = Icons.Outlined.Level6,
                contentDescription = Icons.Outlined.Level6.name,
                tint = colorResource(R.color.level6),
                modifier = iconModifier
            )

            else -> Icon(
                imageVector = Icons.Outlined.Level0,
                contentDescription = Icons.Outlined.Level0.name,
                tint = colorResource(R.color.level0),
                modifier = iconModifier
            )
        }
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
        Text(
            text = official.ifEmpty { stringResource(R.string.str_no_official) },
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
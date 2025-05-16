package com.laohei.bili_tube.presentation.player.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_tube.R
import com.laohei.bili_tube.component.button.SubscribeButton
import com.laohei.bili_tube.component.icons.Level0
import com.laohei.bili_tube.component.icons.Level1
import com.laohei.bili_tube.component.icons.Level2
import com.laohei.bili_tube.component.icons.Level3
import com.laohei.bili_tube.component.icons.Level4
import com.laohei.bili_tube.component.icons.Level5
import com.laohei.bili_tube.component.icons.Level6
import com.laohei.bili_tube.component.sheet.ModalBottomSheet
import com.laohei.bili_tube.component.sheet.rememberModalBottomSheet
import com.laohei.bili_tube.component.text.VerticalDataText
import com.laohei.bili_tube.presentation.player.state.screen.ScreenAction

@Composable
internal fun UserSimpleInfo(
    face: String,
    name: String,
    fans: String,
    isSubscribed: Boolean,
    onClick: (ScreenAction) -> Unit
) {
    val context = LocalContext.current
    val faceRequest = remember(face) {
        ImageRequest.Builder(context)
            .data(face)
            .crossfade(true)
            .build()
    }
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
                    .clip(CircleShape),
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
            isSubscribed = isSubscribed
        ) {
            onClick.invoke(ScreenAction.SubscribeAction)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoCardSheet(
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp,
    isShowSheet: Boolean,
    isLoading: Boolean,
    face: String,
    name: String,
    sign: String,
    follower: Long,
    likeNum: Long,
    attention: Long,
    isSubscribed: Boolean,
    level: Int,
    official: String,
    onSubscriptionClick: () -> Unit,
    onDismiss: () -> Unit = {},
    maskAlphaChanged: (Float) -> Unit = { _ -> }
) {
    val sheetState = rememberModalBottomSheet(
        skipPartiallyExpanded = true
    )
    BackHandler(enabled = isShowSheet) {
        onDismiss.invoke()
    }
    if (isShowSheet) {
        LaunchedEffect(sheetState) {
            snapshotFlow { sheetState.requireOffset() }
                .collect { offset ->
                    maskAlphaChanged.invoke(offset)
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
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        UserProfileWidget(
                            face = face,
                            name = name,
                            sign = sign,
                            isSubscribed = isSubscribed,
                            onSubscriptionClick = onSubscriptionClick
                        )
                    }
                    item {
                        UserDataWidget(
                            modifier = Modifier.fillMaxWidth(),
                            follower,
                            likeNum,
                            attention
                        )
                    }
                    item {
                        UserLevelAndOfficialWidget(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 16.dp, vertical = 2.dp),
                            level, official
                        )
                    }
                    item { Spacer(Modifier.height(bottomPadding)) }
                }
            }
        }
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
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
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
        VerticalDataText(
            data = follower,
            label = stringResource(R.string.str_follower)
        )
        VerticalDataText(
            data = attention,
            label = stringResource(R.string.str_attention)
        )
        VerticalDataText(
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
                .background(MaterialTheme.colorScheme.primary)
        )
        Text(
            text = official.ifEmpty { stringResource(R.string.str_no_official) },
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}


@Preview
@Composable
fun UserInfoCardSheetPreview() {
    UserInfoCardSheet(
        isShowSheet = true,
        isLoading = false,
        face = "", name = "IC实验室",
        sign = "关注大众消费、互联网商业和流行文化。@IC实验室，全网同名。",
        isSubscribed = false,
        follower = 10000,
        likeNum = 10000000,
        attention = 100,
        official = "bilibili 2020新人奖UP主、知名UP主",
        level = 6,
        onSubscriptionClick = {}
    )
}
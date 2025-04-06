package com.laohei.bili_tube.presentation.player.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.laohei.bili_tube.R
import com.laohei.bili_tube.component.animation.APNGAnimationWidget
import com.laohei.bili_tube.component.button.ExtendedIconButton
import com.laohei.bili_tube.component.lottie.LottieIconLike
import com.laohei.bili_tube.component.sheet.ModalBottomSheet
import com.laohei.bili_tube.component.sheet.ModalBottomSheetProperties
import com.laohei.bili_tube.component.sheet.rememberModalBottomSheet
import com.laohei.bili_tube.component.video.VideoAction
import kotlinx.coroutines.launch


@Composable
internal fun VideoMenus(
    great: String,
    coin: String,
    star: String,
    share: String,
    hasLike: Boolean,
    hasCoin: Boolean,
    hasFavoured: Boolean,
    isDownloaded: Boolean,
    isShowLikeAnimation: Boolean,
    onClick: (VideoAction.VideoMenuAction) -> Unit,
    coinClick: () -> Unit,
    favouredClick: () -> Unit,
    downloadClick: () -> Unit,
    onAnimationEndCallback: (() -> Unit)? = null
) {
    var localHasLike by remember { mutableStateOf(hasLike) }
    var localHasCoin by remember { mutableStateOf(hasCoin) }
    var localHasFavoured by remember { mutableStateOf(hasFavoured) }
    LaunchedEffect(hasLike, hasCoin, hasFavoured) {
        localHasLike = hasLike
        localHasCoin = hasCoin
        localHasFavoured = hasFavoured
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier)
        Box {
            ExtendedIconButton(
                icon1 = Icons.Outlined.ThumbUp,
                icon2 = Icons.Outlined.ThumbDown,
                label = great,
                icon1Color = if (localHasLike) Color.Red else MaterialTheme.colorScheme.onBackground,
                onIcon1Click = {
                    onClick.invoke(VideoAction.VideoMenuAction.VideoLikeAction(if (localHasLike) 2 else 1))
                },
                onIcon2Click = {}
            )
            if (isShowLikeAnimation) {
                Popup(
                    offset = IntOffset(10, -120)
                ) {
                    LottieIconLike(
                        modifier = Modifier.size(46.dp),
                        iterateForever = false,
                        onAnimationEndCallback = onAnimationEndCallback
                    )
                }
            }
        }
        Spacer(modifier = Modifier)
        ExtendedIconButton(
            icon = Icons.Outlined.Paid,
            label = coin,
            color = if (localHasCoin) Color.Red else MaterialTheme.colorScheme.onBackground,
            onClick = {
                if (localHasCoin.not()) {
                    coinClick.invoke()
                }
            }
        )
        Spacer(modifier = Modifier)
        ExtendedIconButton(
            icon = Icons.Outlined.StarOutline,
            color = if (localHasFavoured) Color.Red else MaterialTheme.colorScheme.onBackground,
            label = star,
            onClick = { favouredClick.invoke() }
        )
        Spacer(modifier = Modifier)
        ExtendedIconButton(
            icon = Icons.Outlined.Share,
            label = share,
            onClick = {}
        )
        Spacer(modifier = Modifier)
        ExtendedIconButton(
            enabled = isDownloaded.not(),
            icon = Icons.Outlined.Download,
            label = when {
                isDownloaded -> stringResource(R.string.str_downloaded)
                else -> stringResource(R.string.str_download)
            },
            onClick = { downloadClick.invoke() }
        )
        Spacer(modifier = Modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CoinSheet(
    isShowSheet: Boolean = true,
    onDismiss: () -> Unit = {},
    onClick: (VideoAction.VideoMenuAction) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheet(skipPartiallyExpanded = true)
    fun closeSheet() {
        scope.launch {
            sheetState.hide()
            onDismiss.invoke()
        }
    }
    if (isShowSheet) {
        var selectCoin by remember { mutableIntStateOf(1) }
        ModalBottomSheet(
            shape = RoundedCornerShape(12.dp),
            sheetState = sheetState,
            modifier = Modifier
                .padding(8.dp)
                .navigationBarsPadding(),
            containerColor = MaterialTheme.colorScheme.background,
            properties = ModalBottomSheetProperties(shouldDispatcherEvent = false),
            onDismissRequest = { closeSheet() }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CoinItem(
                        id = R.drawable.one_coin_ani,
                        coin = 1,
                        isSelected = selectCoin == 1,
                        frameCount = 24,
                        frameWidth = 187f,
                        frameHeight = 300f,
                        animationDuration = 1500
                    ) {
                        selectCoin = 1
                    }
                    CoinItem(
                        id = R.drawable.two_coin_ani,
                        coin = 2,
                        isSelected = selectCoin == 2,
                        frameCount = 24,
                        frameWidth = 187f,
                        frameHeight = 300f,
                        animationDuration = 1500
                    ) {
                        selectCoin = 2
                    }
                }

                FilledTonalButton(
                    onClick = { onClick.invoke(VideoAction.VideoMenuAction.CoinAction(coin = selectCoin)) },
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text(text = stringResource(R.string.str_coin_flip))
                }
                Spacer(Modifier.height(8.dp))
            }

        }
    }

}

@Composable
private fun CoinItem(
    @DrawableRes id: Int,
    coin: Int = 1,
    isSelected: Boolean,
    frameCount: Int = 24,
    frameWidth: Float = 187f,
    frameHeight: Float = 300f,
    animationDuration: Int = 1500,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary
    else Color.LightGray
    Card(
        modifier = Modifier
            .drawBehind {
                val strokeWidth = 4.dp.toPx()
                val pathEffect =
                    if (isSelected.not()) {
                        PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)
                    } else {
                        null
                    }

                drawRoundRect(
                    color = borderColor,
                    size = size,
                    style = Stroke(width = strokeWidth, pathEffect = pathEffect),
                    cornerRadius = CornerRadius(12.dp.toPx())
                )
            }
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick.invoke() }
            .padding(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Text(
            text = stringResource(R.string.str_coin, coin),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = borderColor
        )
        Spacer(Modifier.height(8.dp))
        APNGAnimationWidget(
            id = id,
            frameCount = frameCount,
            frameWidth = frameWidth,
            frameHeight = frameHeight,
            animationDuration = animationDuration
        )
    }
}


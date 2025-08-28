package com.laohei.bili_tube.features.player.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.R
import com.laohei.bili_tube.features.player.VideoMenuAction
import com.laohei.bili_tube.ui.component.animation.SpriteAnimation
import com.laohei.bili_tube.ui.component.sheet.ModalBottomSheet
import com.laohei.bili_tube.ui.component.sheet.ModalBottomSheetProperties
import com.laohei.bili_tube.ui.component.sheet.rememberModalBottomSheet
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddCoinSheet(
    isShowAddCoinUI: Boolean = true,
    onDismiss: () -> Unit = {},
    onVideoMenuAction: (VideoMenuAction) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheet(skipPartiallyExpanded = true)
    fun closeSheet() {
        scope.launch {
            sheetState.hide()
            onDismiss.invoke()
        }
    }
    if (isShowAddCoinUI) {
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
                        animationDuration = 1500
                    ) {
                        selectCoin = 1
                    }
                    CoinItem(
                        id = R.drawable.two_coin_ani,
                        coin = 2,
                        isSelected = selectCoin == 2,
                        frameCount = 24,
                        animationDuration = 1500
                    ) {
                        selectCoin = 2
                    }
                }

                FilledTonalButton(
                    onClick = { onVideoMenuAction(VideoMenuAction.AddCoin(coin = selectCoin)) },
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
    frameWidth: Int = 187,
    frameHeight: Int = 300,
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
        SpriteAnimation(
            spriteResId = id,
            frameCount = frameCount,
            frameWidthPx = frameWidth,
            frameHeightPx = frameHeight,
            durationMillis = animationDuration
        )
    }
}

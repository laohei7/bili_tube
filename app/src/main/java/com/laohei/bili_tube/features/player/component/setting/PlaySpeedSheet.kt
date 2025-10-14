package com.laohei.bili_tube.features.player.component.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.laohei.bili_tube.R
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val SpeedList = listOf(
    0.25f, 0.5f, 1.0f, 1.5f, 2.0f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaySpeedSheet(
    isSheetVisible: Boolean = true,
    speed: Float = 1.0f,
    onDismiss: () -> Unit,
    onSpeedUpdated: (Float) -> Unit
) {
    if (!isSheetVisible) return
    val localSpeed by rememberUpdatedState(speed)
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val iconButtonColor = IconButtonDefaults.filledTonalIconButtonColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    )

    fun adjustSpeed(newValue: Float) {
        val step = 0.05f
        val newSpeed = (newValue / step).roundToInt() * step
        onSpeedUpdated(newSpeed.coerceIn(0.25f, 2.0f))
    }

    fun closeSheet() {
        scope.launch {
            sheetState.hide()
            onDismiss.invoke()
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .padding(8.dp)
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = { closeSheet() }
    ) {
        Text(
            text = stringResource(R.string.str_speed_label, localSpeed),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        ListItem(
            leadingContent = {
                FilledTonalIconButton(
                    onClick = {
                        if (localSpeed > 0.25f) {
                            adjustSpeed(localSpeed - 0.05f)
                        }
                    },
                    colors = iconButtonColor
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Remove,
                        contentDescription = Icons.Rounded.Remove.name,
                    )
                }
            },
            headlineContent = {
                Slider(
                    value = localSpeed,
                    steps = 35,
                    onValueChange = {
                        adjustSpeed(it)
                    },
                    valueRange = 0.25f..2.0f,
                    thumb = {},
                    track = { sliderState ->
                        val fraction =
                            (sliderState.value - sliderState.valueRange.start) / (sliderState.valueRange.endInclusive - sliderState.valueRange.start)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .clip(CircleShape)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                                ),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction)
                                    .height(20.dp)
                                    .padding(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        color = Color.White
                                    )
                            )
                        }
                    }
                )
            },
            trailingContent = {
                FilledTonalIconButton(
                    onClick = {
                        if (localSpeed < 2.0f) {
                            adjustSpeed(localSpeed + 0.05f)
                        }
                    },
                    colors = iconButtonColor
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = Icons.Rounded.Add.name,
                    )
                }
            }
        )

        ListItem(
            headlineContent = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SpeedList.fastForEach { item ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AssistChip(
                                onClick = { adjustSpeed(item) },
                                shape = CircleShape,
                                border = AssistChipDefaults.assistChipBorder(
                                    enabled = true,
                                    borderColor = Color.Transparent,
                                    borderWidth = 0.dp
                                ),
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                ),
                                label = {
                                    Text(text = "$item")
                                }
                            )
                            if (item == 1.0f) {
                                Text(
                                    text = stringResource(R.string.str_normal),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                    }
                }
            }
        )
    }
}
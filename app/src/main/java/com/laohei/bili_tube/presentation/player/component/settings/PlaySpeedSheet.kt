package com.laohei.bili_tube.presentation.player.component.settings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.laohei.bili_tube.R
import kotlin.math.roundToInt

private val SpeedList = listOf(
    0.25f, 0.5f, 1.0f, 1.5f, 2.0f
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PlaySpeedSheet(
    isShowSheet: Boolean = true,
    speed: Float = 1.0f,
    onDismiss: () -> Unit = {},
    onSpeedChanged:(Float)-> Unit={}
) {
    var localSpeed by remember { mutableFloatStateOf(speed) }

    fun adjustSpeed(newValue: Float) {
        localSpeed = ((newValue * 20).roundToInt() / 20.0f).coerceIn(0.25f, 2.0f)
        onSpeedChanged.invoke(localSpeed)
    }

    LaunchedEffect(speed) { localSpeed = speed }

    if (isShowSheet) {
        ModalBottomSheet(
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(8.dp)
                .navigationBarsPadding(),
            containerColor = MaterialTheme.colorScheme.background,
            onDismissRequest = { onDismiss.invoke() }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${localSpeed}x",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            ListItem(
                leadingContent = {
                    FilledTonalIconButton(
                        onClick = {
                            if (localSpeed > 0.25f) {
                                adjustSpeed(localSpeed - 0.05f)
                            }
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Remove,
                            contentDescription = Icons.Outlined.Remove.name,
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
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = Icons.Outlined.Add.name,
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
                                    onClick = {adjustSpeed(item)},
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
}
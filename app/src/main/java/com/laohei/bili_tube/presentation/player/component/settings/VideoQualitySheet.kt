package com.laohei.bili_tube.presentation.player.component.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.VIP_STATUS_KEY
import com.laohei.bili_tube.core.util.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VideoQualitySheet(
    isShowSheet: Boolean = true,
    quality: List<Pair<Int, String>>,
    defaultQuality: Pair<Int, String>,
    onDismiss: () -> Unit = {},
    onQualityChanged: (Pair<Int, String>) -> Unit
) {
    val context = LocalContext.current
    val isVip = context.getValue(VIP_STATUS_KEY.name, 0) != 0
    var localQuality by remember { mutableStateOf(quality) }
    var localDefaultQuality by remember { mutableStateOf(defaultQuality) }

    LaunchedEffect(quality, defaultQuality) {
        localQuality = quality
        localDefaultQuality = defaultQuality
    }

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
            Column(
                modifier = Modifier.verticalScroll(
                    state = rememberScrollState()
                )
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(
                                R.string.str_current_quarity,
                                localDefaultQuality.second
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
                localQuality.fastForEach {
                    val flag = when {
                        isVip -> true
                        !isVip && it.first <= 80 -> true
                        else -> false
                    }
                    ListItem(
                        modifier = Modifier.clickable(
                            enabled = flag
                        ) { onQualityChanged.invoke(it) },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = Icons.Outlined.Check.name,
                                tint = if (localDefaultQuality.first == it.first) LocalContentColor.current
                                else Color.Transparent
                            )
                        },
                        headlineContent = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = it.second,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (it.first > 80) {
                                    Surface(
                                        contentColor = Color.White,
                                        color = Color.Red,
                                        shape = CircleShape
                                    ) {
                                        Text(
                                            modifier = Modifier.padding(
                                                horizontal = 6.dp,
                                                vertical = 1.dp
                                            ),
                                            style = MaterialTheme.typography.labelSmall,
                                            text = stringResource(R.string.str_vip)
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
                HorizontalDivider()
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.str_quality_hint),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
            }
        }
    }
}
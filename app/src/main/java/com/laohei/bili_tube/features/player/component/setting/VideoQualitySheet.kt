package com.laohei.bili_tube.features.player.component.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.VIP_STATUS_KEY
import com.laohei.bili_tube.core.extension.getValue
import com.laohei.bili_tube.features.player.state.media_v2.MediaQuality
import com.laohei.bili_tube.ui.theme.PaddingSm
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VideoQualitySheet(
    isShowSheet: Boolean = true,
    qualities: List<MediaQuality>,
    activeQuality: MediaQuality,
    onDismiss: () -> Unit = {},
    onQualityChanged: (Pair<Int, String>) -> Unit
) {
    if (!isShowSheet) return
    val context = LocalContext.current
    val isVip = context.getValue(VIP_STATUS_KEY.name, 0) != 0
    val localQualities by rememberUpdatedState(qualities)
    val localActiveQuality by rememberUpdatedState(activeQuality)


    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
            .padding(PaddingSm)
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = { closeSheet() }
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
                            R.string.str_current_quality,
                            localActiveQuality.label
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
            localQualities.fastForEach {
                val flag = when {
                    isVip -> true
                    !isVip && it.id <= 80 -> true
                    else -> false
                }
                ListItem(
                    modifier = Modifier.clickable(
                        enabled = flag
                    ) { onQualityChanged.invoke(it.id to it.label) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = Icons.Outlined.Check.name,
                            tint = if (localActiveQuality.id == it.id) LocalContentColor.current
                            else Color.Transparent
                        )
                    },
                    headlineContent = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = it.label,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (it.id > 80) {
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
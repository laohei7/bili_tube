package com.laohei.bili_tube.features.player.component.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DownloadSheet(
    isShowSheet: Boolean = true,
    quality: List<Pair<Int, String>>,
    defaultQuality: Pair<Int, String>,
    onDismiss: () -> Unit = {},
    onDownloadClick: (Pair<Int, String>) -> Unit
) {
    if (!isShowSheet) return
    val context = LocalContext.current
    val isVip = context.getValue(VIP_STATUS_KEY.name, 0) != 0
    var localQuality by remember { mutableStateOf(quality) }
    var localDefaultQuality by remember { mutableStateOf(defaultQuality) }

    LaunchedEffect(quality, defaultQuality) {
        localQuality = quality
        localDefaultQuality = defaultQuality
    }

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
            .padding(8.dp)
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = { closeSheet() }
    ) {
        Column(
            modifier = Modifier.verticalScroll(
                state = rememberScrollState()
            )
        ) {
            localQuality.fastForEach {
                val flag = when {
                    isVip -> true
                    !isVip && it.first <= 80 -> true
                    else -> false
                }
                ListItem(
                    modifier = Modifier.clickable(
                        enabled = flag
                    ) { localDefaultQuality = it },
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
                    Button(
                        onClick = {
                            onDownloadClick.invoke(localDefaultQuality)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.str_download))
                    }
                }
            )
        }
    }
}
package com.laohei.bili_tube.features.player.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import com.laohei.bili_sdk.model_v2.video.VideoDetailModel
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.bottomsheet.ModalBottomSheet
import com.laohei.bili_tube.ui.bottomsheet.rememberModalBottomSheet
import com.laohei.bili_tube.ui.component.chip.TagChip
import com.laohei.bili_tube.util.toDateString
import com.laohei.bili_tube.util.toViewString
import kotlinx.coroutines.launch


@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
internal fun VideoDetailSheet(
    modifier: Modifier = Modifier,
    videoDetail: VideoDetailModel?,
    isShowVideoDetailUI: Boolean = true,
    bottomPadding: Dp = 0.dp,
    onDismiss: () -> Unit = {},
    onMaskAlphaChange: (Float) -> Unit = { _ -> }
) {
    if (!isShowVideoDetailUI) return
    val sheetState = rememberModalBottomSheet(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val publishDate =
        videoDetail?.view?.pubdate?.toDateString(false)
    val tags = videoDetail?.tags?.fastMap { it.tagName } ?: emptyList()
    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.requireOffset() }
            .collect { offset ->
                onMaskAlphaChange.invoke(offset)
            }
    }
//    BackHandler(enabled = true) { onDismiss.invoke() }
    ModalBottomSheet(
        modifier = modifier.fillMaxSize(),
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = MaterialTheme.colorScheme.background,
        scrimColor = Color.Transparent,
        onDismissRequest = { onDismiss.invoke() },
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.str_description),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            scope.launch {
                                sheetState.hide()
                                onDismiss.invoke()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = Icons.Default.Close.name,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = videoDetail?.view?.title ?: "",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                )
            }
            item {
                ListItem(
                    headlineContent = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            TitleAndLabel(
                                title = videoDetail?.view?.stat?.like?.toViewString() ?: "-",
                                label = "赞"
                            )
                            TitleAndLabel(
                                title = videoDetail?.view?.stat?.view?.toViewString() ?: "-",
                                label = "观看次数"
                            )
                            TitleAndLabel(
                                title = ((publishDate?.substringBefore("年") + ("年"))),
                                label = publishDate?.substringAfter("年") ?: ""
                            )
                        }
                    }
                )
            }

            videoDetail?.view?.desc?.let {
                item {
                    ListItem(
                        headlineContent = {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = it.ifBlank { stringResource(R.string.str_empty) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    )
                }
            }

            item {
                ListItem(
                    headlineContent = {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            tags.fastForEach {
                                TagChip(it)
                            }
                        }
                    }
                )
            }
            item { Spacer(Modifier.height(bottomPadding)) }
        }
    }
}

@Composable
private fun TitleAndLabel(
    title: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}


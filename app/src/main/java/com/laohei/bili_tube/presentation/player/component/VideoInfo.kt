package com.laohei.bili_tube.presentation.player.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.laohei.bili_tube.R
import com.laohei.bili_tube.component.color.sheetListItemColors
import com.laohei.bili_tube.presentation.player.MAX_LENGTH
import com.laohei.bili_tube.utill.formatDateToString
import com.laohei.bili_tube.utill.toViewString
import kotlinx.coroutines.launch


@Composable
internal fun VideoSimpleInfo(
    title: String,
    view: String,
    date: String,
    tag: String? = null,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable { onClick.invoke() }
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 2,
            color = MaterialTheme.colorScheme.onBackground,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${view}次观看", style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Text(
                text = date, style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            tag?.let {
                Text(
                    text = it.run {
                        if (length > MAX_LENGTH) {
                            substring(0, MAX_LENGTH) + "..."
                        } else {
                            this
                        }
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "...展开", style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }

    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
internal fun VideoDetailSheet(
    modifier: Modifier = Modifier,
    title: String? = "拉片24小时，全网没人发现的《边水往事》隐藏剧情！万字细嗦《边水往事》P1 包含；原著、镜头语言、细节、隐喻、科普等",
    description: String? = "第一期2集内容已经高达2万字！求求各位宝贝们三连吧！突破1万赞爆肝下一期！挑战全网最细的《边水往事》",
    like: String? = 15871.toViewString(),
    view: String? = 316995.toViewString(),
    publishDate: String? = 1740625200.formatDateToString(false),
    tags: List<String> = listOf(
        "人人都能聊影视",
        "悬疑",
        "犯罪",
        "剧情",
        "郭麒麟",
        "吴镇宇",
        "冒险",
        "电视剧",
        "边水往事",
        "人人都能聊影视5.0",
        "电视剧解说"
    ),
    isShowDetail: Boolean = true,
    bottomPadding: Dp = 0.dp,
    onDismiss: () -> Unit = {},
    maskAlphaChanged: (Float) -> Unit = { _ -> }
) {
    BackHandler(enabled = isShowDetail) {
        onDismiss.invoke()
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    if (isShowDetail) {
        LaunchedEffect(sheetState) {
            snapshotFlow { sheetState.requireOffset() }
                .collect { offset ->
                    maskAlphaChanged.invoke(offset)
                }
        }
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
                    .fillMaxHeight()
                    .padding(bottom = bottomPadding),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                stickyHeader {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
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
                    HorizontalDivider()
                }
                item {
                    ListItem(
                        colors = sheetListItemColors(),
                        headlineContent = {
                            Text(
                                text = title ?: "",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    )
                }
                item {
                    ListItem(
                        colors = sheetListItemColors(),
                        headlineContent = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                TitleAndLabel(title = like ?: "-", label = "赞")
                                TitleAndLabel(title = view ?: "-", label = "观看次数")
                                TitleAndLabel(
                                    title = ((publishDate?.substringBefore("年") + ("年"))),
                                    label = publishDate?.substringAfter("年") ?: ""
                                )
                            }
                        }
                    )
                }

                item {
                    ListItem(
                        colors = sheetListItemColors(),
                        headlineContent = {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = description ?: "",
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    )
                }
                item {
                    ListItem(
                        colors = sheetListItemColors(),
                        headlineContent = {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                tags.fastForEach {
                                    TagItem(it)
                                }
                            }
                        }
                    )
                }
            }
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

@Composable
private fun TagItem(
    tag: String
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = CircleShape
    ) {
        Text(
            text = tag, style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
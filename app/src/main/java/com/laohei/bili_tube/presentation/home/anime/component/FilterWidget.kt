package com.laohei.bili_tube.presentation.home.anime.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.model.BangumiFilterModel
import com.laohei.bili_tube.presentation.home.anime.BangumiFilters


@Composable
internal fun FilterWidget(
    modifier: Modifier = Modifier,
    filters: Map<String, List<Pair<String, String>>>,
    bangumiFilterModel: BangumiFilterModel,
    onClick: (String, String) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { (key, value) ->
            FilterRow(
                title = key, filters = value, selected = bangumiFilterModel.getValue(key),
                onClick = onClick
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FilterRow(
    title: String,
    filters: List<Pair<String, String>>,
    selected: String,
    onClick: (String, String) -> Unit
) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        stickyHeader {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        items(filters) {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onClick.invoke(title, it.second)
                    },
                contentColor = when {
                    selected == it.second -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.onBackground
                },
            ) {
                Text(
                    text = it.first,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilterWidgetPreview() {
    var bangumiFilterModel by remember {
        mutableStateOf(BangumiFilterModel())
    }
    FilterWidget(bangumiFilterModel = bangumiFilterModel, filters = BangumiFilters) { key, value ->
        bangumiFilterModel = bangumiFilterModel.update(key, value)
    }
}
package com.laohei.bili_tube.features.search.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.laohei.bili_tube.R
import com.laohei.bili_tube.features.search.SearchTabs
import com.laohei.bili_tube.room.entity.SearchHistory
import com.laohei.bili_tube.ui.component.ScrollTabRow
import com.laohei.bili_tube.ui.component.text.BasicInput
import kotlinx.coroutines.flow.flowOf


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    expanded: Boolean,
    histories: LazyPagingItems<SearchHistory>,
    value: String,
    placeholder: String = "",
    onValueChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    upPress: () -> Unit,
    onSearch: () -> Unit,
) {
    SearchBar(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        inputField = {
            val textStyle = MaterialTheme.typography.bodySmall
            BasicInput(
                value = value,
                onValueChanged = onValueChange,
                borderStroke = BorderStroke(1.dp, Color.LightGray),
                inputIconColor = MaterialTheme.colorScheme.primary,
                textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onBackground),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                singleLine = true,
                navigationIcon = {
                    IconButton(onClick = { upPress.invoke() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = Icons.AutoMirrored.Outlined.ArrowBack.name,
                        )
                    }
                },
                trailingIcon = {
                    TextButton(onClick = { onSearch.invoke() }) {
                        Text(text = stringResource(R.string.str_search))
                    }
                },
                placeholder = {
                    Text(
                        text = placeholder.ifBlank { stringResource(R.string.str_search_hint) },
                        style = textStyle, color = Color.Gray
                    )
                }
            )
        },
        colors = SearchBarDefaults.colors(
            dividerColor = Color.Transparent,
            containerColor = Color.Transparent
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        LazyColumn {
            items(histories.itemCount) { index ->
                histories[index]?.let {
                    SearchHistoryItem(
                        history = it.keyword,
                        onClick = {
                            onValueChange(it.keyword)
                            onSearch()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchHistoryItem(
    history: String,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = Icons.Outlined.History.name,
            )
        },
        headlineContent = {
            Text(text = history, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Outlined.ArrowOutward,
                contentDescription = Icons.Outlined.ArrowOutward.name,
            )
        }
    )
}

@Composable
internal fun SearchTopBar(
    tabs: List<Int>,
    histories: LazyPagingItems<SearchHistory>,
    selectedTabIndex: Int,
    expanded: Boolean,
    keyword: String,
    onValueChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    upPress: () -> Unit,
    onSearch: () -> Unit,
    onTabClick: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        SearchTopBar(
            expanded = expanded,
            histories = histories,
            value = keyword,
            onValueChange = onValueChange,
            onExpandedChange = onExpandedChange,
            upPress = upPress,
            onSearch = onSearch
        )

        ScrollTabRow(
            tabs = tabs.map { stringResource(it) },
            selectedTabIndex = selectedTabIndex,
            onTabClick = onTabClick
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchTopBarPreview() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    SearchTopBar(
        tabs = SearchTabs,
        histories = flowOf(PagingData.empty<SearchHistory>()).collectAsLazyPagingItems(),
        selectedTabIndex = selectedTabIndex,
        onTabClick = { selectedTabIndex = it },
        expanded = false,
        keyword = "",
        onValueChange = {},
        onSearch = {},
        upPress = {},
        onExpandedChange = {}
    )
}
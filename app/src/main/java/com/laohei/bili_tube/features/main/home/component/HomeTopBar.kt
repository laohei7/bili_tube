package com.laohei.bili_tube.features.main.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import com.laohei.bili_tube.features.main.component.LogoTopAppBar
import com.laohei.bili_tube.features.main.home.HomeTabs
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.component.widget.ChipTabRow

@Composable
internal fun HomeTopBar(
    isOnlyTabs: Boolean = false,
    tabs: List<String>,
    offset: IntOffset,
    alpha: Float,
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit,
    navigateToAppRoute: (AppRoute) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .offset { offset }
            .background(
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.98f)
            )
    ) {
        if (isOnlyTabs.not()) {
            LogoTopAppBar(
                alpha = alpha,
                onSearchClick = {
                    navigateToAppRoute(AppRoute.Search)
                }
            )
        }

        ChipTabRow(
            tabs = tabs,
            selectedTabIndex = selectedTabIndex,
            onTabClick = onTabClick
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeTopBarPreview() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    HomeTopBar(
        tabs = HomeTabs.map { stringResource(it) },
        offset = IntOffset(0, 0),
        alpha = 1f,
        selectedTabIndex = selectedTabIndex,
        onTabClick = { selectedTabIndex = it },
        navigateToAppRoute = {}
    )
}


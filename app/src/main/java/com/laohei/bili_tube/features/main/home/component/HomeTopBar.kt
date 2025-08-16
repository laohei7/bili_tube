package com.laohei.bili_tube.features.main.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.laohei.bili_tube.features.main.component.LogoTopAppBar
import com.laohei.bili_tube.features.main.home.Tabs
import com.laohei.bili_tube.nav.AppRoute
import com.laohei.bili_tube.ui.theme.LargePadding
import com.laohei.bili_tube.ui.theme.SmallPadding

@Composable
internal fun HomeTopBar(
    isOnlyTabs: Boolean = false,
    tabs: List<Int>,
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
        if(isOnlyTabs.not()){
            LogoTopAppBar(
                alpha = alpha,
                onSearchClick = {
                    navigateToAppRoute(AppRoute.Search)
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(LargePadding)
        ) {
            Spacer(Modifier)
            tabs.fastForEachIndexed { index, tab ->
                AssistChip(
                    onClick = { onTabClick(tab) },
                    shape = RoundedCornerShape(SmallPadding),
                    colors = when {
                        selectedTabIndex == index -> {
                            AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                labelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        else -> {
                            AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                labelColor = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    border = BorderStroke(0.dp, Color.Transparent),
                    label = {
                        Text(text = stringResource(tab))
                    }
                )
            }
            Spacer(Modifier)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeTopBarPreview() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    HomeTopBar(
        tabs = Tabs,
        offset = IntOffset(0, 0),
        alpha = 1f,
        selectedTabIndex = selectedTabIndex,
        onTabClick = { selectedTabIndex = Tabs.indexOf(it).coerceAtLeast(0) },
        navigateToAppRoute = {}
    )
}


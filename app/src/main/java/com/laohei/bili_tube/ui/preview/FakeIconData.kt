package com.laohei.bili_tube.ui.preview

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.laohei.bili_tube.ui.component.icons.AutoSkip
import com.laohei.bili_tube.ui.component.icons.Level0
import com.laohei.bili_tube.ui.component.icons.Level1
import com.laohei.bili_tube.ui.component.icons.Level2
import com.laohei.bili_tube.ui.component.icons.Level3
import com.laohei.bili_tube.ui.component.icons.Level4
import com.laohei.bili_tube.ui.component.icons.Level5
import com.laohei.bili_tube.ui.component.icons.Level6
import com.laohei.bili_tube.ui.component.icons.SleepTimer
import com.laohei.bili_tube.ui.theme.ColorLevel0
import com.laohei.bili_tube.ui.theme.ColorLevel1
import com.laohei.bili_tube.ui.theme.ColorLevel2
import com.laohei.bili_tube.ui.theme.ColorLevel3
import com.laohei.bili_tube.ui.theme.ColorLevel4
import com.laohei.bili_tube.ui.theme.ColorLevel5
import com.laohei.bili_tube.ui.theme.ColorLevel6

data class FakeIcon(
    val icon: ImageVector,
    val color: Color = Color.Black
)

class FakeIconData : PreviewParameterProvider<List<FakeIcon>> {
    override val values: Sequence<List<FakeIcon>>
        get() = sequenceOf(
            listOf(
                FakeIcon(Icons.Outlined.SleepTimer),
                FakeIcon(Icons.Outlined.AutoSkip),
            ),
            listOf(
                FakeIcon(
                    icon = Icons.Outlined.Level0,
                    color = ColorLevel0
                ),
                FakeIcon(
                    icon = Icons.Outlined.Level1,
                    color = ColorLevel1
                ),
                FakeIcon(
                    icon = Icons.Outlined.Level2,
                    color = ColorLevel2
                ),
                FakeIcon(
                    icon = Icons.Outlined.Level3,
                    color = ColorLevel3
                ),
                FakeIcon(
                    icon = Icons.Outlined.Level4,
                    color = ColorLevel4
                ),
                FakeIcon(
                    icon = Icons.Outlined.Level5,
                    color = ColorLevel5
                ),
                FakeIcon(
                    icon = Icons.Outlined.Level6,
                    color = ColorLevel6
                ),
            )
        )
}
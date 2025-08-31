package com.laohei.bili_tube.ui.component.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.theme.PaddingXs
import com.laohei.bili_tube.util.toTimeAgoString
import com.laohei.bili_tube.util.toViewString

enum class IconPosition {
    Left, Right, None
}

@Composable
fun IconWithText(
    modifier: Modifier = Modifier,
    iconPosition: IconPosition = IconPosition.Left,
    icon: @Composable () -> Unit,
    label: String,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
    textColor: Color = Color.Gray,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PaddingXs)
    ) {
        if (iconPosition == IconPosition.Left) {
            icon()
        }

        Text(
            text = label,
            maxLines = 1,
            style = textStyle,
            color = textColor
        )

        if (iconPosition == IconPosition.Right) {
            icon()
        }
    }
}

@Composable
fun ViewAtLabel(label: String) {
    IconWithText(
        icon = {
            Icon(
                imageVector = Icons.Rounded.AccessTime,
                contentDescription = Icons.Rounded.AccessTime.name,
                tint = Color.Gray,
                modifier = DefaultIconModifier
            )
        },
        label = label
    )
}

@Composable
fun ViewAndDateLabel(
    modifier: Modifier = Modifier,
    view: String,
    publishDate: String
) {
    IconWithText(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.Rounded.PlayCircleOutline,
                contentDescription = Icons.Rounded.PlayCircleOutline.name,
                tint = Color.Red.copy(alpha = 0.5f),
                modifier = DefaultIconModifier
            )
        },
        label = stringResource(R.string.str_video_play_and_date, view, publishDate),
        textColor = Color.Gray
    )
}

@Composable
fun ViewLabel(
    modifier: Modifier = Modifier,
    view: String,
) {
    IconWithText(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.Rounded.PlayCircleOutline,
                contentDescription = Icons.Rounded.PlayCircleOutline.name,
                tint = Color.LightGray,
                modifier = DefaultIconModifier
            )
        },
        label = view,
        textColor = Color.LightGray
    )
}

@Composable
fun FavoriteLabel(
    modifier: Modifier = Modifier,
    favorites: String,
) {
    IconWithText(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.Rounded.FavoriteBorder,
                contentDescription = Icons.Rounded.FavoriteBorder.name,
                tint = Color.LightGray,
                modifier = DefaultIconModifier
            )
        },
        label = favorites,
        textColor = Color.LightGray
    )
}

@Composable
fun DetailLabel(
    modifier: Modifier = Modifier,
    label: String,
) {
    IconWithText(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = Icons.AutoMirrored.Rounded.KeyboardArrowRight.name,
                tint = Color.LightGray,
                modifier = DefaultIconModifier
            )
        },
        iconPosition = IconPosition.Right,
        label = label,
        textColor = Color.LightGray
    )
}

@Preview(showBackground = true)
@Composable
private fun ViewAtLabelPreview() {
    val viewAt = System.currentTimeMillis() - 5 * 60 * 1000
    ViewAtLabel(label = viewAt.toTimeAgoString())
}

@Preview(showBackground = true)
@Composable
private fun ViewAndDateLabelPreview() {
    val publishDate = System.currentTimeMillis() - 60 * 60 * 1000
    ViewAndDateLabel(view = 555.toViewString(), publishDate = publishDate.toTimeAgoString())
}

@Preview(showBackground = true)
@Composable
private fun ViewLabelPreview() {
    ViewLabel(view = "3849.2万")
}

@Preview(showBackground = true)
@Composable
private fun FavoriteLabelPreview() {
    FavoriteLabel(favorites = "3849.2万")
}

@Preview(showBackground = true)
@Composable
private fun DetailLabelPreview() {
    DetailLabel(label = stringResource(R.string.str_detail))
}


private val DefaultIconModifier = Modifier.size(14.dp)